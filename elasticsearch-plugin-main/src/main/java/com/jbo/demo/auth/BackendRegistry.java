package com.jbo.demo.auth;

import com.jbo.demo.action.configupdate.TransportConfigUpdateAction;
import com.jbo.demo.auditlog.AuditLog;
import com.jbo.demo.configuration.ConfigChangeListener;
import com.jbo.demo.configuration.ConfigurationService;
import com.jbo.demo.filter.DemoRestFilter;
import com.jbo.demo.http.XFFResolver;
import org.elasticsearch.ElasticsearchSecurityException;
import org.elasticsearch.cluster.ClusterService;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.rest.RestChannel;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.transport.TransportChannel;
import org.elasticsearch.transport.TransportRequest;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @Author jiangbo
 * @Date 2019/2/16 14:14
 * @Version 1.0
 * @Description
 */
public class BackendRegistry implements ConfigChangeListener {

    protected final ESLogger log = Loggers.getLogger(this.getClass());
    private final Map<String, String> authImplMap = new HashMap<String, String>();
    private volatile boolean initialized;
    private final TransportConfigUpdateAction tcua;
    private final XFFResolver xffResolver;
    private volatile boolean anonymousAuthEnabled = false;
    private final Settings esSettings;


    @Inject
    public BackendRegistry(final Settings settings, final RestController controller, final TransportConfigUpdateAction tcua, final ClusterService cse,
                           final XFFResolver xffResolver, final AuditLog auditLog) {
        tcua.addConfigChangeListener(ConfigurationService.CONFIGNAME_CONFIG, this);
        controller.registerFilter(new DemoRestFilter(this, auditLog));
        this.tcua = tcua;
        this.esSettings = settings;
        this.xffResolver = xffResolver;


    }

    public void invalidateCache() {
    }

    private <T> T newInstance(final String clazzOrShortcut, String type, final Settings settings) throws ClassNotFoundException, NoSuchMethodException,
            SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

        String clazz = clazzOrShortcut;

        if(authImplMap.containsKey(clazz+"_"+type)) {
            clazz = authImplMap.get(clazz+"_"+type);
        }

        final Class<T> t = (Class<T>) Class.forName(clazz);

        try {
            final Constructor<T> tctor = t.getConstructor(Settings.class);
            return tctor.newInstance(settings);
        } catch (final Exception e) {
            log.warn("Unable to create instance of class {} with (Settings.class) constructor due to {}", e, t, e.toString());
            final Constructor<T> tctor = t.getConstructor(Settings.class, TransportConfigUpdateAction.class);
            return tctor.newInstance(settings, tcua);
        }
    }

    @Override
    public void onChange(final String event, final Settings settings) {
        initialized = true;
    }

    @Override
    public void validate(final String event, final Settings settings) throws ElasticsearchSecurityException {

    }

    public boolean authenticate(final TransportRequest request, final TransportChannel channel) throws ElasticsearchSecurityException {

        return true;
    }

    /**
     *
     * @param request
     * @param channel
     * @return The authenticated user, null means another roundtrip
     * @throws ElasticsearchSecurityException
     */
    public boolean authenticate(final RestRequest request, final RestChannel channel) throws ElasticsearchSecurityException {

        return true;
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }


}
