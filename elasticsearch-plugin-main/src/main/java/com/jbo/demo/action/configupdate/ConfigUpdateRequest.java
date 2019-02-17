package com.jbo.demo.action.configupdate;

import org.elasticsearch.action.ActionRequestValidationException;
import org.elasticsearch.action.support.nodes.BaseNodesRequest;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;

import java.io.IOException;

/**
 *
 * @Author jiangbo
 * @Date 2019/2/16 14:14
 * @Version 1.0
 * @Description
 */
public class ConfigUpdateRequest extends BaseNodesRequest<ConfigUpdateRequest> {

    private String[] configTypes;

    public ConfigUpdateRequest() {
        super();
    }

    public ConfigUpdateRequest(final String[] configTypes) {
        super();
        this.configTypes = configTypes;
    }

    @Override
    public void readFrom(final StreamInput in) throws IOException {
        super.readFrom(in);
        this.configTypes = in.readStringArray();
    }

    @Override
    public void writeTo(final StreamOutput out) throws IOException {
        super.writeTo(out);
        out.writeStringArray(configTypes);
    }

    public String[] getConfigTypes() {
        return configTypes;
    }

    public void setConfigTypes(final String[] configTypes) {
        this.configTypes = configTypes;
    }

    @Override
    public ActionRequestValidationException validate() {
        if (configTypes == null || configTypes.length == 0) {
            return new ActionRequestValidationException();
        }
        return null;
    }
}
