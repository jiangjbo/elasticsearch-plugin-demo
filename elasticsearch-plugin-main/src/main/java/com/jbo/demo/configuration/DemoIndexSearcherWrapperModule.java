
package com.jbo.demo.configuration;

import org.elasticsearch.common.inject.AbstractModule;
import org.elasticsearch.common.inject.multibindings.Multibinder;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.index.engine.IndexSearcherWrapper;

/**
 *
 * @Author jiangbo
 * @Date 2019/2/16 14:14
 * @Version 1.0
 * @Description
 */
public class DemoIndexSearcherWrapperModule extends AbstractModule {

    protected final ESLogger logger = Loggers.getLogger(this.getClass());
    private static volatile Class searchGuardFlsDlsIndexSearcherWrapper = null;

    @Override
    protected void configure() {
        //TODO how often called?
        final Multibinder wrapperMultibinder = Multibinder.newSetBinder(binder(), IndexSearcherWrapper.class);

        if(searchGuardFlsDlsIndexSearcherWrapper != null) {
            wrapperMultibinder.addBinding().to(searchGuardFlsDlsIndexSearcherWrapper);
            return;
        }
        
        try {
            Class _searchGuardFlsDlsIndexSearcherWrapper;
            if ((_searchGuardFlsDlsIndexSearcherWrapper = Class
                    .forName("com.jbo.demo.configuration.SearchGuardFlsDlsIndexSearcherWrapper")) != null) {
                wrapperMultibinder.addBinding().to(_searchGuardFlsDlsIndexSearcherWrapper);
                searchGuardFlsDlsIndexSearcherWrapper = _searchGuardFlsDlsIndexSearcherWrapper;
                logger.info("FLS/DLS enabled");
            } else {
                throw new ClassNotFoundException();
            }

        } catch (final ClassNotFoundException e) {
            logger.debug("FLS/DLS not enabled");
            wrapperMultibinder.addBinding().to(DemoIndexSearcherWrapper.class);
        }

    }

}
