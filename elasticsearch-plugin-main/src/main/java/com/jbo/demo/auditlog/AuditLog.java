package com.jbo.demo.auditlog;

import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.transport.TransportRequest;

import java.io.Closeable;

/**
 *
 * @Author jiangbo
 * @Date 2019/2/16 14:14
 * @Version 1.0
 * @Description
 */
public interface AuditLog extends Closeable {

    // TODO store action in request
    
    void logFailedLogin(String username, TransportRequest request);
    
    void logFailedLogin(String username, RestRequest request);

    void logMissingPrivileges(String privilege, TransportRequest request);

    void logBadHeaders(TransportRequest request);

    void logBadHeaders(RestRequest request);

    void logSgIndexAttempt(TransportRequest request, String action);
    
    void logSSLException(TransportRequest request, Throwable t, String action);
    
    void logSSLException(RestRequest request, Throwable t, String action);

    //void logBadCertificate(X509Certificate[] x509Certs, ContextAndHeaderHolder request);
    
    void logAuthenticatedRequest(TransportRequest request, final String action);
}
