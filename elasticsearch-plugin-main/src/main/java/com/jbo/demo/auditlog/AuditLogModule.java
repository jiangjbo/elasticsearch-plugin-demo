package com.jbo.demo.auditlog;

import org.elasticsearch.common.inject.AbstractModule;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;

/**
 *
 * @Author jiangbo
 * @Date 2019/2/16 14:14
 * @Version 1.0
 * @Description
 */
public class AuditLogModule extends AbstractModule {

    protected final ESLogger log = Loggers.getLogger(this.getClass());
    
    @Override
    protected void configure() {
        try {
            Class auditLogImpl;
            if ((auditLogImpl = Class
                    .forName("com.jbo.demo.auditlog.impl.AuditLogImpl")) != null) {
                bind(AuditLog.class).to(auditLogImpl).asEagerSingleton();
                log.info("Auditlog available ({})", auditLogImpl.getSimpleName());
            } else {
                throw new ClassNotFoundException();
            }
        } catch (ClassNotFoundException e) {
            bind(AuditLog.class).to(NullAuditLog.class).asEagerSingleton();
            log.info("Auditlog not available");
        }
        
       
    }
}
