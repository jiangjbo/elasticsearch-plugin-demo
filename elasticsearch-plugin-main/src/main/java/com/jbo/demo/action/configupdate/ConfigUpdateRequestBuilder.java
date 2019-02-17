package com.jbo.demo.action.configupdate;

import org.elasticsearch.action.support.nodes.NodesOperationRequestBuilder;
import org.elasticsearch.client.ClusterAdminClient;
import org.elasticsearch.client.ElasticsearchClient;

/**
 *
 * @Author jiangbo
 * @Date 2019/2/16 14:14
 * @Version 1.0
 * @Description
 */
public class ConfigUpdateRequestBuilder extends NodesOperationRequestBuilder<ConfigUpdateRequest, ConfigUpdateResponse, ConfigUpdateRequestBuilder> {

    public ConfigUpdateRequestBuilder(final ClusterAdminClient client) {
        this(client, ConfigUpdateAction.INSTANCE);
    }

    public ConfigUpdateRequestBuilder(final ElasticsearchClient client, final ConfigUpdateAction action) {
        super(client, action, new ConfigUpdateRequest());
    }

    public ConfigUpdateRequestBuilder setShardId(final String[] configTypes) {
        request().setConfigTypes(configTypes);
        return this;
    }
}
