package com.jbo.demo;

import com.google.common.collect.ImmutableList;
import com.jbo.demo.action.configupdate.ConfigUpdateAction;
import com.jbo.demo.action.configupdate.TransportConfigUpdateAction;
import com.jbo.demo.auditlog.AuditLogModule;
import com.jbo.demo.configuration.BackendModule;
import com.jbo.demo.configuration.ConfigurationModule;
import com.jbo.demo.configuration.DemoIndexSearcherWrapperModule;
import com.jbo.demo.filter.DemoFilter;
import com.jbo.demo.http.DemoHttpServerTransport;
import com.jbo.demo.util.ReflectionUtils;
import com.jbo.demo.transport.DemoTransport;
import com.jbo.demo.util.JsonUtils;
import org.elasticsearch.action.ActionModule;
import org.elasticsearch.common.inject.Module;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.http.HttpServerModule;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.rest.RestModule;
import org.elasticsearch.transport.TransportModule;

import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @Author jiangbo
 * @Date 2019/2/16 14:14
 * @Version 1.0
 * @Description
 */
public class DemoPlugin extends Plugin{

    private final ESLogger log = Loggers.getLogger(this.getClass());
    private static final String CLIENT_TYPE = "client.type";
    private Settings settings;
    private boolean client;
    private boolean tribeNodeClient;
    private boolean disabled;

    public DemoPlugin(final Settings settings) {
        super();
        log.info("cluster name: {}", settings.get("cluster.name","elasticsearch"));

        disabled = settings.getAsBoolean("plugin.demo.disabled", false);
        if(disabled) {
            this.settings = null;
            this.client = false;
            this.tribeNodeClient = false;
            log.warn("plugin installed but disabled. This can expose your configuration to the public.");
            return;
        }
        this.settings = settings;
        client = !"node".equals(this.settings.get(CLIENT_TYPE, "node"));
        log.info("settings config {}", JsonUtils.convertSettingsToJson(settings));
        boolean tribeNode = this.settings.getAsBoolean("action.master.force_local", false) && this.settings.getByPrefix("tribe").getAsMap().size() > 0;
        tribeNodeClient = this.settings.get("tribe.name", null) != null;

        log.info("Node [{}] is a transportClient: {}/tribeNode: {}/tribeNodeClient: {}", settings.get("node.name"), client, tribeNode, tribeNodeClient);

    }

    @Override
    public String name() {
        return "plugin demo";
    }

    @Override
    public String description() {
        return "es plugin demo";
    }

    public Collection<Module> shardModules(Settings settings) {
        if (!client && !tribeNodeClient && !disabled) {
            //TODO query caching
            return ImmutableList.<Module>of(new DemoIndexSearcherWrapperModule());
        }
        return ImmutableList.of();
    }

    @Override
    public Collection<Module> nodeModules() {
        final Collection<Module> modules = new ArrayList<>();
        if (!client && !tribeNodeClient && !disabled) {
            modules.add(new ConfigurationModule());
            modules.add(new BackendModule());
            modules.add(new AuditLogModule());
        }
        return modules;
    }

    public void onModule(final ActionModule module) {

        if(!tribeNodeClient && !disabled) {
            module.registerAction(ConfigUpdateAction.INSTANCE, TransportConfigUpdateAction.class);
            if (!client) {
                module.registerFilter(DemoFilter.class);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void onModule(final RestModule module) {
        if (!client && !tribeNodeClient  && !disabled) {
            if(ReflectionUtils.canLoad("com.jbo.rest.api.SearchGuardRestApiActions")) {
                try {
                    ReflectionUtils
                            .load("com.jbo.rest.api.SearchGuardRestApiActions")
                            .getDeclaredMethod("addActions", RestModule.class)
                            .invoke(null, module);
                } catch(Exception ex) {
                    log.error("Failed to register SearchGuardRestApiActions, management API not available. Cause: {}", ex.getMessage());
                }
            }
        }
    }

    public void onModule(final TransportModule module) {
        if (!client && !tribeNodeClient && !disabled) {
            module.setTransportService(DemoTransport.class, name());
        }
    }

    public void onModule(final HttpServerModule module) {
        if (!client && !tribeNodeClient  && !disabled) {
            module.setHttpServerTransport(DemoHttpServerTransport.class, name());
        }
    }

    @Override
    public Settings additionalSettings() {
        if(disabled) {
            return Settings.EMPTY;
        }
        final Settings.Builder builder = Settings.settingsBuilder();
        return builder.build();
    }

    private void checkSSLPluginAvailable() {
        try {
            getClass().getClassLoader().loadClass("com.floragunn.searchguard.ssl.SearchGuardSSLPlugin");
        } catch (final ClassNotFoundException cnfe) {
            throw new IllegalStateException("SearchGuardSSLPlugin must be be installed");
        }
    }

}
