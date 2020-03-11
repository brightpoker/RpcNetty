package com.poke.connection;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.log4j.Log4j;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName ServiceDiscovery
 * @Description //TODO
 * @Author poke
 * @Date 2020/3/11 9:07 下午
 */
@Log4j
@Component
public class ServiceDiscovery {

    @Value("${registry.address}")
    private String registryAddress;

    @Autowired
    private ConnectManage manage;

    private static final String ZK_REGISTRY_PATH = "/rpc";

    private volatile List<String> addressList = new ArrayList<>();

    private ZkClient client;

    @PostConstruct
    public void init() {
        client = connectServer();
        if (client != null) {
            watchNode(client);
        }
    }

    private void watchNode(final ZkClient client) {
        List<String> nodeList = client.subscribeChildChanges(ZK_REGISTRY_PATH, (s, nodes) -> {
            log.info("监听到子节点数据变化:" + JSONObject.toJSONString(nodes));
            addressList.clear();
            getNodeData(nodes);
            updateConnectedServer();
        });
        getNodeData(nodeList);
        log.info("已发现服务列表..." + JSONObject.toJSONString(addressList));
        updateConnectedServer();
    }

    private void updateConnectedServer() {
        manage.updateConnectServer(addressList);
    }

    private void getNodeData(List<String> nodes) {
        log.info("/rpc子节点数据为:" + JSONObject.toJSONString(nodes));
        for (String node : nodes) {
            String address = client.readData(ZK_REGISTRY_PATH + "/" + node);
            addressList.add(address);
        }
    }

    private ZkClient connectServer() {
        ZkClient client = new ZkClient(registryAddress, 2000, 2000);
        return client;
    }
}
