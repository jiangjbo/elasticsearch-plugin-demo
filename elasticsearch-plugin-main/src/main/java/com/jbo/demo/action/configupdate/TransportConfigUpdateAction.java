package com.jbo.demo.action.configupdate;

import com.jbo.demo.action.configupdate.ConfigUpdateResponse.Node;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimaps;
import com.jbo.demo.auth.BackendRegistry;
import com.jbo.demo.configuration.ConfigChangeListener;
import com.jbo.demo.configuration.ConfigurationLoader;
import com.jbo.demo.util.ConfigConstants;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.support.ActionFilters;
import org.elasticsearch.action.support.nodes.BaseNodeRequest;
import org.elasticsearch.action.support.nodes.TransportNodesAction;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.ClusterName;
import org.elasticsearch.cluster.ClusterService;
import org.elasticsearch.cluster.health.ClusterHealthStatus;
import org.elasticsearch.cluster.metadata.IndexNameExpressionResolver;
import org.elasticsearch.common.component.LifecycleListener;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.Provider;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.threadpool.ThreadPool;
import org.elasticsearch.transport.TransportService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 *
 * @Author jiangbo
 * @Date 2019/2/16 14:14
 * @Version 1.0
 * @Description
 */
public class TransportConfigUpdateAction extends
        TransportNodesAction<ConfigUpdateRequest, ConfigUpdateResponse, TransportConfigUpdateAction.NodeConfigUpdateRequest, Node> {

    private final ClusterService clusterService;
    private final ConfigurationLoader cl;
    private final Provider<BackendRegistry> backendRegistry;
    private final ListMultimap<String, ConfigChangeListener> multimap = Multimaps.synchronizedListMultimap(ArrayListMultimap.<String, ConfigChangeListener>create());

    private final String configIndex;

    @Inject
    public TransportConfigUpdateAction(final Provider<Client> clientProvider, final Settings settings, final ClusterName clusterName,
                                       final ThreadPool threadPool, final ClusterService clusterService, final TransportService transportService,
                                       final ConfigurationLoader cl, final ActionFilters actionFilters, final IndexNameExpressionResolver indexNameExpressionResolver,
                                       Provider<BackendRegistry> backendRegistry) {
        super(settings, ConfigUpdateAction.NAME, clusterName, threadPool, clusterService, transportService, actionFilters,
                indexNameExpressionResolver, ConfigUpdateRequest.class, NodeConfigUpdateRequest.class,
                ThreadPool.Names.MANAGEMENT);
        this.cl = cl;
        this.clusterService = clusterService;
        this.backendRegistry = backendRegistry;
        this.configIndex = settings.get(ConfigConstants.SG_CONFIG_INDEX, ConfigConstants.SG_DEFAULT_CONFIG_INDEX);

        clusterService.addLifecycleListener(new LifecycleListener() {

            @Override
            public void afterStart() {

                final Thread ct = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            Client client = clientProvider.get();
                            logger.debug("Node started, try to initialize it. Wait for at least yellow cluster state....");
                            ClusterHealthResponse response = null;
                            try {
                                response = client.admin().cluster().health(new ClusterHealthRequest(configIndex).waitForYellowStatus()).actionGet();
                            } catch (Exception e1) {
                                logger.debug("Catched a {} but we just try again ...", e1.toString());
                            }

                            while (response == null || response.isTimedOut() || response.getStatus() == ClusterHealthStatus.RED) {
                                logger.warn("index '{}' not healthy yet, we try again ... (Reason: {})", configIndex, response == null ? "no response" : (response.isTimedOut() ? "timeout" : "other, maybe red cluster"));
                                try {
                                    Thread.sleep(3000);
                                } catch (InterruptedException e1) {
                                    //ignore
                                }
                                try {
                                    response = client.admin().cluster().health(new ClusterHealthRequest(configIndex).waitForYellowStatus()).actionGet();
                                } catch (Exception e1) {
                                    logger.debug("Catched again a {} but we just try again ...", e1.toString());
                                }
                                continue;
                            }

                            Map<String, Settings> setn = null;

                            while (setn == null || !setn.keySet().containsAll(Lists.newArrayList("config", "roles"))) {// whitelist不是必须的

                                if (setn != null) {
                                    try {
                                        Thread.sleep(3000);
                                    } catch (InterruptedException e) {
                                        Thread.currentThread().interrupt();
                                        logger.debug("Thread was interrupted so we cancle initialization");
                                        return;
                                    }
                                }

                                logger.debug("Try to load config ...");

                                try {
                                    setn = cl.load(new String[]{"config", "roles", "whitelist"}, 1, TimeUnit.MINUTES);
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                    logger.debug("Thread was interrupted so we cancle initialization");
                                    return;
                                } catch (TimeoutException e) {
                                    logger.warn("Timeout, we just try again in a few seconds ... ");
                                }

                            }

                            logger.debug("Retrieved {} configs", setn.keySet());


                            logger.info("Retrieved config on node startup and will now update config change listeners");
                            for (final String evt : setn.keySet()) {
                                for (final ConfigChangeListener cl : new ArrayList<ConfigChangeListener>(multimap.get(evt))) {
                                    Settings settings = setn.get(evt);
                                    if (settings != null) {
                                        cl.onChange(evt, settings);
                                        logger.debug("Updated {} for {} due to initial configuration on node '{}'", evt, cl.getClass().getSimpleName(), clusterService.localNode().getName());
                                    }
                                }
                            }

                            logger.info("Node '{}' initialized", clusterService.localNode().getName());

                        } catch (Exception e) {
                            logger.error("Unexpected exception while initializing node " + e, e);
                        }
                    }
                });

                logger.info("Check if " + configIndex + " index exists ...");

                try {
                    IndicesExistsRequest ier = new IndicesExistsRequest(configIndex)
                            .masterNodeTimeout(TimeValue.timeValueMinutes(1));
                    ier.putHeader(ConfigConstants.SG_CONF_REQUEST_HEADER, "true");
                    clientProvider.get().admin().indices().exists(ier, new ActionListener<IndicesExistsResponse>() {

                        @Override
                        public void onResponse(IndicesExistsResponse response) {
                            if (response != null && response.isExists()) {
                                ct.start();
                            } else {
                                if (settings.getAsBoolean("action.master.force_local", false) && settings.getByPrefix("tribe").getAsMap().size() > 0) {
                                    logger.info("{} index does not exist yet, but we are a tribe node. So we will load the config anyhow until we got it ...", configIndex);
                                    ct.start();
                                } else {
                                    logger.info("{} index does not exist yet, so no need to load config on node startup.", configIndex);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Throwable e) {
                            logger.error("Failure while checking {} index {}", e, configIndex, e);
                            ct.start();
                        }
                    });
                } catch (Throwable e2) {
                    logger.error("Failure while executing IndicesExistsRequest {}", e2, e2);
                    ct.start();
                }
            }
        });

    }

    public static class NodeConfigUpdateRequest extends BaseNodeRequest {

        ConfigUpdateRequest request;

        public NodeConfigUpdateRequest() {
        }

        public NodeConfigUpdateRequest(final String nodeId, final ConfigUpdateRequest request) {
            super(request, nodeId);
            this.request = request;
        }

        @Override
        public void readFrom(final StreamInput in) throws IOException {
            super.readFrom(in);
            request = new ConfigUpdateRequest();
            request.readFrom(in);
        }

        @Override
        public void writeTo(final StreamOutput out) throws IOException {
            super.writeTo(out);
            request.writeTo(out);
        }
    }

    @Override
    protected ConfigUpdateResponse newResponse(final ConfigUpdateRequest request, final AtomicReferenceArray nodesResponses) {

        final List<ConfigUpdateResponse.Node> nodes = Lists.<Node>newArrayList();
        for (int i = 0; i < nodesResponses.length(); i++) {
            final Object resp = nodesResponses.get(i);
            if (resp instanceof Node) {
                nodes.add((Node) resp);
            }
        }
        return new ConfigUpdateResponse(this.clusterName, nodes.toArray(new Node[nodes.size()]));

    }

    @Override
    protected NodeConfigUpdateRequest newNodeRequest(final String nodeId, final ConfigUpdateRequest request) {
        return new NodeConfigUpdateRequest(nodeId, request);
    }

    @Override
    protected Node newNodeResponse() {
        return new Node(clusterService.localNode(), new String[0], null);
    }

    @Override
    protected Node nodeOperation(final NodeConfigUpdateRequest request) {
        try {
            final Map<String, Settings> setn = cl.load(request.request.getConfigTypes(), 30, TimeUnit.SECONDS);
            logger.debug("Retrieved config ({}) due to config update request and will now update config change listeners", Arrays.toString(request.request.getConfigTypes()));
            backendRegistry.get().invalidateCache();
            for (final String evt : setn.keySet()) {
                for (final ConfigChangeListener cl : new ArrayList<ConfigChangeListener>(multimap.get(evt))) {
                    Settings settings = setn.get(evt);
                    if (settings != null) {
                        cl.onChange(evt, settings);
                        logger.debug("Updated {} for {} due to node operation on node {}", evt, cl.getClass().getSimpleName(),
                                clusterService.localNode().getName());
                    }
                }
            }
            return new Node(clusterService.localNode(), setn.keySet().toArray(new String[0]), null);
        } catch (InterruptedException e1) {
            Thread.currentThread().interrupt();
            logger.debug("Thread was interrupted, we return just a empty response");
            return new Node(clusterService.localNode(), new String[0], "Interrupted");
        } catch (TimeoutException e1) {
            logger.error("Timeout {}", e1, e1);
            return new Node(clusterService.localNode(), new String[0], "Timeout (" + e1 + ")");
        }
    }

    public void addConfigChangeListener(final String event, final ConfigChangeListener listener) {
        logger.debug("Add config listener {}", listener.getClass());
        multimap.put(event, listener);
    }

    @Override
    protected boolean accumulateExceptions() {
        return false;
    }

}
