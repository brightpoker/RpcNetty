package com.poke.configuration;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @ClassName RpcScannerConfigurer
 * @Description //TODO
 * @Author poke
 * @Date 2020/3/11 2:19 上午
 */
@Component
public class RpcScannerConfigurer implements BeanDefinitionRegistryPostProcessor {
    String basePackage = "com.poke.service";

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        ClassPathRpcScanner scanner = new ClassPathRpcScanner(beanDefinitionRegistry);
        scanner.setAnnotationClass(null);
        scanner.registerFilters();
        scanner.doScan(StringUtils.tokenizeToStringArray(basePackage, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS));
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }
}
