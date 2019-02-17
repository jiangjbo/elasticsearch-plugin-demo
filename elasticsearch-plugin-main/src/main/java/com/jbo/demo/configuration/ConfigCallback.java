package com.jbo.demo.configuration;

import org.elasticsearch.action.get.MultiGetResponse.Failure;
import org.elasticsearch.common.settings.Settings;

/**
 *
 * @Author jiangbo
 * @Date 2019/2/16 14:14
 * @Version 1.0
 * @Description
 */
public interface ConfigCallback {
    
    void success(String type, Settings settings);
    void noData(String type);
    void singleFailure(Failure failure);
    void failure(Throwable t);

}
