package com.poke.configuration;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Proxy;

/**
 * @ClassName RpcFactoryBean
 * @Description //TODO
 * @Author poke
 * @Date 2020/3/11 2:43 上午
 */
public class RpcFactoryBean<T> implements FactoryBean<T> {

    private Class<T> rpcInterface;

    @Autowired
    RpcFactoryBeanInvocationHandler<T> invocationHandler;

    public RpcFactoryBean() {
    }

    public RpcFactoryBean(Class<T> rpcInterface) {
        this.rpcInterface = rpcInterface;
    }

    @Override
    public T getObject() throws Exception {
        return (T)Proxy.newProxyInstance(rpcInterface.getClassLoader(), new Class[]{rpcInterface}, invocationHandler);
    }

    @Override
    public Class<?> getObjectType() {
        return this.rpcInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
