package com.poke.connection;

import com.poke.netty.NettyClient;
import io.netty.channel.Channel;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @ClassName ConnectManage
 * @Description //TODO
 * @Author poke
 * @Date 2020/3/11 9:37 下午
 */
@Component
@Log4j
public class ConnectManage {

    @Autowired
    NettyClient nettyClient;

    private AtomicInteger id = new AtomicInteger(0);
    private Map<SocketAddress, Channel> channelNodes = new ConcurrentHashMap<>();
    private CopyOnWriteArrayList<Channel> channels = new CopyOnWriteArrayList<>();

    public Channel chooseChannel() {
        if (channels.size()>0) {
            int size = channels.size();
            int index = (id.getAndAdd(1) + size) % size;
            return channels.get(index);
        }else{
            return null;
        }
    }

    public synchronized void updateConnectServer(List<String> addressList) {
        if (addressList == null || addressList.size() == 0) {
            log.error("没有可用的服务器节点, 全部服务节点已关闭!");
            for (final Channel channel : channels) {
                SocketAddress address = channel.remoteAddress();
                Channel oldChannel = channelNodes.get(address);
                oldChannel.close();
            }
            channels.clear();
            channelNodes.clear();
            return;
        }
        Set<SocketAddress> serverNodeSet = addressList.stream()
                .map(s -> s.split(":"))
                .filter(strings -> strings.length == 2)
                .map(strings -> new InetSocketAddress(strings[0], Integer.parseInt(strings[1])))
                .collect(Collectors.toSet());
        for (SocketAddress serverAddress: serverNodeSet) {
            Channel channel = channelNodes.get(serverAddress);
            if (channel != null && channel.isOpen()) {
                log.info("当前服务节点已存在,无需重新连接:" + serverAddress);
            } else {
                connectServerNode(serverAddress);
            }
        }
        for (int i = 0; i < channels.size(); i++) {
            Channel channel = channels.get(i);
            SocketAddress address = channel.remoteAddress();
            if (!serverNodeSet.contains(address)) {
                log.info("删除无效节点:" + address);
                Channel oldChannel = channelNodes.get(address);
                if (oldChannel != null) {
                    oldChannel.close();
                }
                channels.remove(channel);
                channelNodes.remove(address);
            }
        }
    }

    private void connectServerNode(SocketAddress serverAddress){
        try {
            Channel channel = nettyClient.doConnect(serverAddress);
            addChannel(serverAddress, channel);
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.info("未能成功连接到服务器:" + serverAddress);
        }
    }

    private void addChannel(SocketAddress serverAddress, Channel channel) {
        log.info("加入Channel到连接管理器" +  serverAddress);
        channels.add(channel);
        channelNodes.put(serverAddress, channel);
    }

    public void removeChannel(Channel channel){
        log.info("从连接管理器中移除失效Channel." + channel.remoteAddress());
        SocketAddress address = channel.remoteAddress();
        channelNodes.remove(address);
        channels.remove(channel);
    }
}
