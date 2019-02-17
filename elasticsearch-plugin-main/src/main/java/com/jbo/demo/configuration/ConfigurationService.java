package com.jbo.demo.configuration;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.component.AbstractLifecycleComponent;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;

import java.io.Closeable;

/**
 *
 * @Author jiangbo
 * @Date 2019/2/16 14:14
 * @Version 1.0
 * @Description
 */
public class ConfigurationService extends AbstractLifecycleComponent<ConfigurationService> implements Closeable {
	
	public final static String CONFIGNAME_ROLES = "roles";
	public final static String CONFIGNAME_ROLES_MAPPING = "rolesmapping";
	public final static String CONFIGNAME_ACTION_GROUPS = "actiongroups";
	public final static String CONFIGNAME_INTERNAL_USERS = "internalusers";
	public final static String CONFIGNAME_CONFIG = "config";
	public final static String CONFIGNAME_WHITELIST = "whitelist";
	public final static String[] CONFIGNAMES = new String[] {CONFIGNAME_ROLES, CONFIGNAME_ROLES_MAPPING, 
			CONFIGNAME_ACTION_GROUPS, CONFIGNAME_INTERNAL_USERS, CONFIGNAME_CONFIG, CONFIGNAME_WHITELIST};
	
    @Inject
    public ConfigurationService(final Settings settings, final Client client) {
        super(settings);
    }

    @Override
    protected void doStart() {
     // do nothing
    }

    @Override
    protected void doStop() {
     // do nothing
    }

    @Override
    protected void doClose() {
     // do nothing
    }   
}
