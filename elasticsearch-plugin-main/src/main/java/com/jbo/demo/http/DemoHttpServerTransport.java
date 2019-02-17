package com.jbo.demo.http;

import com.jbo.demo.auditlog.AuditLog;
import com.jbo.demo.util.ConfigConstants;
import com.jbo.demo.util.JsonUtils;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.network.NetworkService;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.util.BigArrays;
import org.elasticsearch.http.HttpChannel;
import org.elasticsearch.http.HttpRequest;
import org.elasticsearch.http.netty.NettyHttpServerTransport;

/**
 *
 * @Author jiangbo
 * @Date 2019/2/16 14:14
 * @Version 1.0
 * @Description
 */
public class DemoHttpServerTransport extends NettyHttpServerTransport {

    private final XFFResolver xffResolver;
    private final AuditLog auditLog;
    
    @Inject
    public DemoHttpServerTransport(Settings settings, NetworkService networkService,
                                   BigArrays bigArrays, XFFResolver xffResolver, AuditLog auditLog) {
        super(settings, networkService, bigArrays);
        this.auditLog = auditLog;
        this.xffResolver = xffResolver;
    }

    @Override
    public void dispatchRequest(final HttpRequest request, final HttpChannel channel) {
        TransportAddress remoteAddress = xffResolver.resolve(request);
        request.putInContext(ConfigConstants.SG_REMOTE_ADDRESS, remoteAddress);

        if(logger.isInfoEnabled()){
            logger.info("http request remote address {}", remoteAddress.toString());
            if(request.path().endsWith("_search")){
                logger.info("http request path {}, headers {}, params {}, body {}, context {}",
                        request.path(), request.getHeaders(), request.params(), JsonUtils.convertBytesReferenceToJson(request.content()), request.getContext());
            } else{
                logger.info("http request path {}, headers {}, params {}, context {}",
                        request.path(), request.getHeaders(), request.params(), request.getContext());
            }
        }
        super.dispatchRequest(request, channel);
    }
    
}
