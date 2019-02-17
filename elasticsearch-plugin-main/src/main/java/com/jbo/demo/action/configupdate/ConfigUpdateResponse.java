package com.jbo.demo.action.configupdate;

import org.elasticsearch.action.support.nodes.BaseNodeResponse;
import org.elasticsearch.action.support.nodes.BaseNodesResponse;
import org.elasticsearch.cluster.ClusterName;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;

import java.io.IOException;
import java.util.Arrays;

/**
 *
 * @Author jiangbo
 * @Date 2019/2/16 14:14
 * @Version 1.0
 * @Description
 */
public class ConfigUpdateResponse extends BaseNodesResponse<ConfigUpdateResponse.Node> {

    public ConfigUpdateResponse() {
    }

    public ConfigUpdateResponse(final ClusterName clusterName, final Node[] nodes) {
        super(clusterName, nodes);
    }

    @Override
    public void readFrom(final StreamInput in) throws IOException {
        super.readFrom(in);
        nodes = new Node[in.readVInt()];
        for (int i = 0; i < nodes.length; i++) {
            nodes[i] = Node.readNodeResponse(in);
        }
    }

    @Override
    public void writeTo(final StreamOutput out) throws IOException {
        super.writeTo(out);
        out.writeVInt(nodes.length);
        for (final Node node : nodes) {
            node.writeTo(out);
        }
    }

    public static class Node extends BaseNodeResponse {
        
        private String[] updatedConfigTypes;
        private String message;
        
        Node() {
        }

        Node(final DiscoveryNode node, String[] updatedConfigTypes, String message) {
            super(node);
            this.updatedConfigTypes = updatedConfigTypes == null?null:Arrays.copyOf(updatedConfigTypes, updatedConfigTypes.length);
            this.message = message;
        }
        
        @Override
        public void readFrom(StreamInput in) throws IOException {
            super.readFrom(in);
            updatedConfigTypes = in.readStringArray();
            message = in.readOptionalString();
        }

        @Override
        public void writeTo(StreamOutput out) throws IOException {
            super.writeTo(out);
            out.writeStringArray(updatedConfigTypes);
            out.writeOptionalString(message);
        }

        public static Node readNodeResponse(final StreamInput in) throws IOException {
            final Node node = new Node();
            node.readFrom(in);
            return node;
        }

        @Override
        public String toString() {
            return "Node [updatedConfigTypes=" + Arrays.toString(updatedConfigTypes) + ", remoteAddress()=" + remoteAddress() + "]";
        }
        
        public String[] getUpdatedConfigTypes() {
            return updatedConfigTypes==null?null:Arrays.copyOf(updatedConfigTypes, updatedConfigTypes.length);
        }

        public String getMessage() {
            return message;
        }

    }
}
