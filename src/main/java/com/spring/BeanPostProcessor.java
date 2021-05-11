package com.spring;

/**
 * @author Team3
 * @date 2021/5/11 22:09
 */
public interface BeanPostProcessor {

    Object postProcessBeforeInitialization(Object bean, String beanName);

    Object postProcessAfterInitialization(Object bean, String beanName);
}
