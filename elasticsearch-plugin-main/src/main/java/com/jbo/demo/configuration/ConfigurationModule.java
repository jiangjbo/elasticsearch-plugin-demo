package com.jbo.demo.configuration;

import com.jbo.demo.auth.WhitelistAuthenticationBackend;
import org.elasticsearch.common.inject.AbstractModule;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;

/**
 * @ClassName #{name}
 * @Author jiangbo
 * @Date 2019/2/16 14:14
 * @Version 1.0
 * @Description
 *
 */
public class ConfigurationModule extends AbstractModule {

    protected final ESLogger log = Loggers.getLogger(this.getClass());
    
    @Override
    protected void configure() {
        bind(DemoSettingsFilter.class).asEagerSingleton();
        bind(ConfigurationService.class).asEagerSingleton();
        bind(WhitelistAuthenticationBackend.class).asEagerSingleton();

    }
    
    
}
