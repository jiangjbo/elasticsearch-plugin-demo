package com.jbo.demo.filter;

import com.jbo.demo.auditlog.AuditLog;
import com.jbo.demo.auth.BackendRegistry;
import com.jbo.demo.util.JsonUtils;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.ESLoggerFactory;
import org.elasticsearch.rest.*;

import java.net.InetSocketAddress;

/**
 *
 * @Author jiangbo
 * @Date 2019/2/16 14:14
 * @Version 1.0
 * @Description
 */
public class DemoRestFilter extends RestFilter {

    private final ESLogger logger = ESLoggerFactory.getLogger(this.getClass().getName());
    private final BackendRegistry registry;
    private final AuditLog auditLog;

    public DemoRestFilter(final BackendRegistry registry, AuditLog auditLog) {
        super();
        this.registry = registry;
        this.auditLog = auditLog;
    }

    @Override
    public void process(final RestRequest request, final RestChannel channel, final RestFilterChain filterChain) throws Exception {

        if(logger.isInfoEnabled()){
            InetSocketAddress remoteAddress = (InetSocketAddress)request.getRemoteAddress();
            logger.info("http request remote address {}", remoteAddress.toString());
            if(request.path().endsWith("_search")){
                logger.info("http request path {}, headers {}, params {}, body {}, context {}",
                        request.path(), request.getHeaders(), request.params(), JsonUtils.convertBytesReferenceToJson(request.content()), request.getContext());
            } else{
                logger.info("http request path {}, headers {}, params {}, context {}",
                        request.path(), request.getHeaders(), request.params(), request.getContext());
            }
        }

        filterChain.continueProcessing(request, channel);
    }

}
