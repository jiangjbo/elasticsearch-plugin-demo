package com.jbo.demo.configuration;

import com.jbo.demo.auth.BackendRegistry;
import org.elasticsearch.common.inject.AbstractModule;

/**
 *
 * @Author jiangbo
 * @Date 2019/2/16 14:14
 * @Version 1.0
 * @Description
 */
public class BackendModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(BackendRegistry.class).asEagerSingleton();
    }

}
