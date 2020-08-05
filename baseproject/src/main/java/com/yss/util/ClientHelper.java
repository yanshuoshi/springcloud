package com.yss.util;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author shuoshi.yan
 * @package:com.ganinfo.utils
 * @className:
 * @description:ES连接池
 * @date 2018-04-18 11:26
 **/
@Component
@RefreshScope
@Slf4j
public class ClientHelper {
    @Value("${spring.data.elasticsearch.ip}")
    private String host; // 服务器地址
    @Value("${spring.data.elasticsearch.port}")
    private int port; // 端口
    @Value("${spring.data.elasticsearch.clustername}")
    private String clusterName;
//    @Value("${spring.data.elasticsearch.username}")
//    private String username;
//    @Value("${spring.data.elasticsearch.password}")
//    private String password;
    private String keypath;

    private Settings setting;

    private ConcurrentHashMap<String, Client> clientMap = new ConcurrentHashMap<String, Client>();

    private Map<String, Integer> ips = new HashMap<String, Integer>(); // hostname port

    public static final ClientHelper getInstance() {
        return ClientHolder.INSTANCE;
    }

    private static class ClientHolder {
        private static final ClientHelper INSTANCE = new ClientHelper();
    }

    /**
     * 初始化默认的client
     */
    public void init() {
        log.info("host：" + host);
        log.info("port：" + port);
        log.info("clusterName：" + clusterName);
        if (host.contains(",")) {
            String[] split = host.split(",");
            for (String ip : split) {
                ips.put(ip, port);
            }
        } else {
            ips.put(host, port);
        }
//读取检验文件
//        keypath=ResourceFileUtil.getFilePath("elastic-certificates.p12");
//        if (StringUtils.isEmpty(keypath)){
//            log.error("not find elastic-certificates.p12 path");
//            return;
//        }

        setting = Settings.builder()
                .put("cluster.name", clusterName)
//                .put("xpack.security.user", username + ":" + password)
//                .put("xpack.security.transport.ssl.enabled", true)
//                .put("xpack.security.transport.ssl.verification_mode", "certificate")
//                .put("xpack.security.transport.ssl.keystore.path", keypath)
//                .put("xpack.security.transport.ssl.truststore.path", keypath)
                .put("client.transport.sniff", true)
                .put("thread_pool.search.size", 10).build();
        addClient(setting, ips);
    }

    /**
     * 获得所有的地址端口
     *
     * @return
     */
    public List<TransportAddress> getAllAddress(Map<String, Integer> ips) {
        List<TransportAddress> addressList = new ArrayList<TransportAddress>();
        for (String ip : ips.keySet()) {
            try {
                addressList.add(new TransportAddress(InetAddress.getByName(ip), Integer.valueOf(ips.get(ip).toString())));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
        return addressList;
    }

    public Client getClient() {
        if (!CommonUtil.isNotMapNull(clientMap) || !CommonUtil.isNotMapNull(ips)) {
            init();
        }
        return getClient(clusterName);
    }

    public Client getClient(String clusterName) {
        if (!CommonUtil.isNotMapNull(clientMap) || !CommonUtil.isNotMapNull(ips)) {
            init();
        }
        return clientMap.get(clusterName);
    }

    /**
     * @author:chengyu.duan
     * @description:未加校验ES
     * @date: 2019/4/23 10:48
     * @param: null
     * @return: null
    */
    public void addClient(Settings setting, Map<String, Integer> ips) {
        TransportClient client = new PreBuiltTransportClient(setting);

        for (String ip : ips.keySet()) {
            try {
                client = client.addTransportAddress(new TransportAddress(InetAddress.getByName(ip), Integer.valueOf(ips.get(ip).toString())));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
        if (client != null) {
            clientMap.put(setting.get("cluster.name"), client);
        } else {
            log.error("TransportClient is null ,can not build cennection for es");
        }
    }
   /**
    * @author:chengyu.duan
    * @description:加校验的ES
    * @date: 2019/4/23 10:47
    * @param: null
    * @return: null
   */
//    public void addClient(Settings setting, Map<String, Integer> ips) {
//        TransportClient client = new PreBuiltXPackTransportClient(setting);
//        for (String ip : ips.keySet()) {
//            try {
//                client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(ip), Integer.valueOf(ips.get(ip).toString())));
//            } catch (UnknownHostException e) {
//                e.printStackTrace();
//            }
//            if (client != null) {
//                clientMap.put(setting.get("cluster.name"), client);
//            }
//
//        }
//
//    }

}
