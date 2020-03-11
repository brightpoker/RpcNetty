package com.poke.registry;

import lombok.extern.log4j.Log4j;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @ClassName ServiceRegistry
 * @Description //TODO
 * @Author poke
 * @Date 2020/3/10 10:24 下午
 */
@Component
@Log4j
public class ServiceRegistry {

    @Value("${registry.address}")
    private String registryAddress;

    private static final String ZK_REGISTRY_PATH = "/rpc";

    private ZkClient connectServer() {
        ZkClient client = new ZkClient(registryAddress, 2000, 2000);
        return client;
    }

    public void register(String data) {
        if (data != null) {
            ZkClient client = connectServer();
            if (client != null) {
                AddRootNode(client);
                createNode(client, data);
            }
        }
    }

    private void AddRootNode(ZkClient client) {
        if (!client.exists(ZK_REGISTRY_PATH)) {
            client.createPersistent(ZK_REGISTRY_PATH);
            log.info("创建zookeeper主节点:" + ZK_REGISTRY_PATH);
        }
    }

    private void createNode(ZkClient client, String data) {
        String path = client.create(ZK_REGISTRY_PATH + "/provider", data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        log.info("创建zookeeper数据节点 (" + path + " => " + data + ")");
    }
}
