package com.jbo.demo.action.configupdate;

import org.elasticsearch.action.Action;
import org.elasticsearch.client.ElasticsearchClient;

/**
 *
 * @Author jiangbo
 * @Date 2019/2/16 14:14
 * @Version 1.0
 * @Description
 */
public class ConfigUpdateAction extends Action<ConfigUpdateRequest, ConfigUpdateResponse, ConfigUpdateRequestBuilder> {

    public static final ConfigUpdateAction INSTANCE = new ConfigUpdateAction();
    public static final String NAME = "cluster:admin/demo/config/update";

    protected ConfigUpdateAction() {
        super(NAME);
    }

    @Override
    public ConfigUpdateRequestBuilder newRequestBuilder(final ElasticsearchClient client) {
        return new ConfigUpdateRequestBuilder(client, this);
    }

    @Override
    public ConfigUpdateResponse newResponse() {
        return new ConfigUpdateResponse();
    }

}
