/*
 * Copyright 2015 floragunn UG (haftungsbeschränkt)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package com.jbo.demo.filter;

import com.jbo.demo.auditlog.AuditLog;
import com.jbo.demo.auth.BackendRegistry;
import com.jbo.demo.util.ConfigConstants;
import com.jbo.demo.util.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.support.ActionFilter;
import org.elasticsearch.action.support.ActionFilterChain;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.Provider;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.tasks.Task;

import java.util.Map;

/**
 *
 * @Author jiangbo
 * @Date 2019/2/16 14:14
 * @Version 1.0
 * @Description transport请求 rest 请求 最终都会路由到这个类
 */
public class DemoFilter implements ActionFilter {

    // "internal:*",
    // "indices:monitor/*",
    // "cluster:monitor/*",
    // "cluster:admin/reroute",
    // "indices:admin/mapping/put"

    protected final ESLogger logger = Loggers.getLogger(this.getClass());
    private final Settings settings;
    private final AuditLog auditLog;

    @Inject
    public DemoFilter(final Settings settings,
                      final Provider<BackendRegistry> backendRegistry, AuditLog auditLog) {
        this.settings = settings;
        this.auditLog = auditLog;
    }

    @Override
    public int order() {
        return Integer.MIN_VALUE;
    }

    @Override
    public void apply(Task task, final String action, final ActionRequest request, final ActionListener listener, final ActionFilterChain chain) {

        if (logger.isTraceEnabled()) {
            final Object remoteAddress = request.getFromContext(ConfigConstants.SG_REMOTE_ADDRESS);
            logger.trace("remote address: {}", String.valueOf(remoteAddress));
        }

        if(request instanceof SearchRequest){
            SearchRequest searchRequest = (SearchRequest)request;
            if(searchRequest.source() != null){
                Map<String, Object> query = JsonUtils.convertBytesReferenceToStructuredMap(searchRequest.source());
                ((SearchRequest) request).source(query);
                final Object remoteAddress = request.getFromContext(ConfigConstants.SG_REMOTE_ADDRESS);
                logger.info("remote address: {}", String.valueOf(remoteAddress));
                String endpoint = endpoint(searchRequest.indices(), searchRequest.types(), "_search");
                logger.info("transport request endpoint {}, headers {}, params {}, body {}, context {}",
                        endpoint, request.getHeaders(), searchRequest.indicesOptions().toString(), JsonUtils.convertBytesReferenceToJson(searchRequest.source()), request.getContext());

            }

        }

        auditLog.logAuthenticatedRequest(request, action);
        chain.proceed(task, action, request, listener);
    }

    @Override
    public void apply(final String action, final ActionResponse response, final ActionListener listener, final ActionFilterChain chain) {
        chain.proceed(action, response, listener);
    }

    private String endpoint(String[] indices, String[] types, String endpoint) {
        String[] strings = new String[]{StringUtils.join(indices, ","), StringUtils.join(types, ","), endpoint};
        return StringUtils.join(strings, "/");
    }

}
