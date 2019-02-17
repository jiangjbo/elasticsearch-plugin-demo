package com.jbo.demo.configuration;

import com.jbo.demo.util.ConfigConstants;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.IndexSearcher;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.engine.EngineConfig;
import org.elasticsearch.index.engine.EngineException;
import org.elasticsearch.index.engine.IndexSearcherWrapper;
import org.elasticsearch.index.shard.AbstractIndexShardComponent;
import org.elasticsearch.index.shard.IndexShard;
import org.elasticsearch.index.shard.ShardId;
import org.elasticsearch.indices.IndicesLifecycle;
import org.elasticsearch.indices.IndicesLifecycle.Listener;

import java.io.IOException;

/**
 *
 * @Author jiangbo
 * @Date 2019/2/16 14:14
 * @Version 1.0
 * @Description
 */
public class DemoIndexSearcherWrapper extends AbstractIndexShardComponent implements IndexSearcherWrapper {

    private volatile boolean shardReady;

    private final String configIndex;
    private final String admin;

    @Inject
    public DemoIndexSearcherWrapper(final ShardId shardId, final IndicesLifecycle indicesLifecycle, final Settings indexSettings) {
        super(shardId, indexSettings);
        this.configIndex = indexSettings.get(ConfigConstants.SG_CONFIG_INDEX, ConfigConstants.SG_DEFAULT_CONFIG_INDEX);
        this.admin = indexSettings.get(ConfigConstants.SG_ADMIN, ConfigConstants.DEFAULT_ADMIN);
        if(!isConfigIndexRequest()) {
            indicesLifecycle.addListener(new Listener() {

                @Override
                public void afterIndexShardPostRecovery(IndexShard indexShard) {
                    if(shardId.equals(indexShard.shardId())) {
                        shardReady = true;
                    }
                }
                
            });
        } else {
            shardReady = true;
        }
    }

    @Override
    public final DirectoryReader wrap(final DirectoryReader reader) throws IOException {

        if (logger.isTraceEnabled()) {
            logger.trace("DirectoryReader {} should be wrapped", reader.getClass());
        }
        
        if(!shardReady) {
            return reader;
        }

        if (!isAdminAuhtenticatedOrInternalRequest()) {

        }

        return reader;

    }

    @Override
    public final IndexSearcher wrap(final EngineConfig engineConfig, final IndexSearcher searcher) throws EngineException {

        if (logger.isTraceEnabled()) {
            logger.trace("IndexSearcher {} should be wrapped (reader is {})", searcher.getClass(), searcher.getIndexReader().getClass());
        }

        if(!shardReady) {
            return searcher;
        }
        
        if (isConfigIndexRequest() && !isAdminAuhtenticatedOrInternalRequest()) {
            return new IndexSearcher(new EmptyReader());
        }

        return searcher;
    }

    protected final boolean isAdminAuhtenticatedOrInternalRequest() {
//        final RequestHolder current = RequestHolder.current();
//
//        if (current != null) {
//            final TransportRequest request = current.getRequest();
//
//            if (request != null) {
//
//                if (user != null && (admindns.isAdmin(user.getName())))) {
//                    return true;
//                }
//
//                if ("true".equals(HeaderHelper.getSafeFromHeader(request, ConfigConstants.SG_CONF_REQUEST_HEADER))) {
//                    return true;
//                }
//            }
//        }

        return true;
    }

    protected final boolean isConfigIndexRequest() {
        return shardId.index().getName().equals(configIndex);
    }
}
