package com.jbo.demo.configuration;

import org.elasticsearch.ElasticsearchSecurityException;
import org.elasticsearch.common.settings.Settings;

/**
 *
 * @Author jiangbo
 * @Date 2019/2/16 14:14
 * @Version 1.0
 * @Description
 */
public interface ConfigChangeListener {

    public void onChange(String event, Settings settings);

    //TODO remove? currently unused
    public void validate(String event, Settings settings) throws ElasticsearchSecurityException;

    public boolean isInitialized();
}
