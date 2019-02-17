package com.jbo.demo.auth;

import com.jbo.demo.action.configupdate.TransportConfigUpdateAction;
import com.jbo.demo.configuration.ConfigChangeListener;
import com.jbo.demo.configuration.ConfigurationService;
import org.elasticsearch.ElasticsearchSecurityException;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @Author jiangbo
 * @Date 2019/2/16 14:14
 * @Version 1.0
 * @Description
 */
public class WhitelistAuthenticationBackend implements ConfigChangeListener {

    //elasticsearch.yml配置文件中的白名单，只对当前机器生效
    private Set<String> nodeWhitelist = new HashSet<>();
    //es中的白名单，对所有es节点生效
    private volatile Set<String> globalWhitelist = new HashSet<>();
    //总白名单
    private Set<String> whitelist = new HashSet<>();

    @Inject
    public WhitelistAuthenticationBackend(final Settings settings, final TransportConfigUpdateAction tcua) {
        super();
        tcua.addConfigChangeListener(ConfigurationService.CONFIGNAME_WHITELIST, this);
    }

    @Override
    public void onChange(String event, Settings settings) {
        globalWhitelist = new HashSet<>(settings.getAsMap().values());
    }

    @Override
    public void validate(String event, Settings settings) throws ElasticsearchSecurityException {

    }

    @Override
    public boolean isInitialized() {
        return true;
    }

    public void setNodeWhitelist(String... ips){
        nodeWhitelist = new HashSet<>(Arrays.asList(ips));
    }

    public void addNodeWhitelist(String... ips){
        nodeWhitelist.addAll(Arrays.asList(ips));
    }

    public void clearNodeWhitelist(){
        nodeWhitelist.clear();
    }

    public Set<String> globalWhitelist(){
        return globalWhitelist;
    }

    public Set<String> nodeWhitelist(){
        return nodeWhitelist;
    }

    public boolean isWhitelist(String ip){
        if (ip == null || "".equals(ip)) {
            return false;
        }
        return nodeWhitelist.contains(ip) || globalWhitelist.contains(ip);
    }

}
