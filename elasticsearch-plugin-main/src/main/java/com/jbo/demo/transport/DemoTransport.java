package com.jbo.demo.transport;

import com.jbo.demo.auditlog.AuditLog;
import com.jbo.demo.auth.BackendRegistry;
import com.jbo.demo.util.ConfigConstants;
import com.jbo.demo.util.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.Provider;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.tasks.Task;
import org.elasticsearch.threadpool.ThreadPool;
import org.elasticsearch.transport.*;

import java.util.concurrent.Callable;

public class DemoTransport extends TransportService {

    private final Provider<BackendRegistry> backendRegistry;
    private final AuditLog auditLog;

    @Inject
    public DemoTransport(final Settings settings, final Transport transport, final ThreadPool threadPool,
                         final Provider<BackendRegistry> backendRegistry, AuditLog auditLog) {
        super(settings, transport, threadPool);
        this.backendRegistry = backendRegistry;
        this.auditLog = auditLog;
    }

    @Override
    public <Request extends TransportRequest> void registerRequestHandler(final String action, final Callable<Request> requestFactory,
                                                                          final String executor, final TransportRequestHandler<Request> handler) {
        super.registerRequestHandler(action, requestFactory, executor, new Interceptor<Request>(handler, action));
    }

    @Override
    public <Request extends TransportRequest> void registerRequestHandler(String action, Class<Request> request, String executor,
                                                                          boolean forceExecution, TransportRequestHandler<Request> handler) {
        super.registerRequestHandler(action, request, executor, forceExecution, new Interceptor<Request>(handler, action));
    }

    @Override
    public <T extends TransportResponse> void sendRequest(final DiscoveryNode node, final String action, final TransportRequest request,
                                                          final TransportResponseHandler<T> handler) {
        //LogHelper.logUserTrace("<-- Send {} to {} with {}/{}", action, node.getName(), request.getContext(), request.getHeaders());
        super.sendRequest(node, action, request, handler);
    }

    @Override
    public <T extends TransportResponse> void sendRequest(final DiscoveryNode node, final String action, final TransportRequest request,
                                                          final TransportRequestOptions options, final TransportResponseHandler<T> handler) {
        //LogHelper.logUserTrace("<-- Send {} to {} with {}/{}", action, node.getName(), request.getContext(), request.getHeaders());
        super.sendRequest(node, action, request, options, handler);
    }

    private class Interceptor<Request extends TransportRequest> extends TransportRequestHandler<Request> {

        private final TransportRequestHandler<Request> handler;
        private final String action;

        public Interceptor(final TransportRequestHandler<Request> handler, final String action) {
            super();
            this.handler = handler;
            this.action = action;
        }

        @Override
        public void messageReceived(Request request, TransportChannel channel) throws Exception {
            messageReceived(request, channel, null);
        }

        @Override
        public void messageReceived(final Request request, final TransportChannel transportChannel, Task task) throws Exception {

            try {
                final RequestHolder context = new RequestHolder(request);
                RequestHolder.setCurrent(context);

                request.putInContext(ConfigConstants.SG_CHANNEL_TYPE, transportChannel.getChannelType());

                //bypass non-netty requests
                if(transportChannel.getChannelType().equals("local") || transportChannel.getChannelType().equals("direct")) {
                    handler.messageReceived(request, transportChannel, task);
                    return;
                }

                TransportAddress originalRemoteAddress = request.remoteAddress();
                if(originalRemoteAddress != null && (originalRemoteAddress instanceof InetSocketTransportAddress)) {
                    request.putInContext(ConfigConstants.SG_REMOTE_ADDRESS, originalRemoteAddress);
                } else {
                    logger.error("Request has no proper remote address {}", originalRemoteAddress);
                    transportChannel.sendResponse(new ElasticsearchException("Request has no proper remote address"));
                    return;
                }

                if(logger.isInfoEnabled()){
                    if(request instanceof SearchRequest){
                        logger.info("transport request remote address {}", originalRemoteAddress.toString());
                        SearchRequest searchRequest = (SearchRequest) request;
                        String endpoint = endpoint(searchRequest.indices(), searchRequest.types(), "_search");
                        logger.info("transport request endpoint {}, headers {}, params {}, body {}, context {}",
                                endpoint, request.getHeaders(), searchRequest.indicesOptions().toString(), JsonUtils.convertBytesReferenceToJson(searchRequest.source()), request.getContext());
                    }
                }

                handler.messageReceived(request, transportChannel, task);
            } finally {
                RequestHolder.removeCurrent();
            }

        }

    }

    private String endpoint(String[] indices, String[] types, String endpoint) {
        String[] strings = new String[]{StringUtils.join(indices, ","), StringUtils.join(types, ","), endpoint};
        return StringUtils.join(strings, "/");
    }

}
