package com.test.service;

import com.spring.BeanPostProcessor;
import com.spring.Component;

/**
 * @author Team3
 * @date 2021/5/11 22:15
 */
@Component
public class TestBeanPostProcessor implements BeanPostProcessor {


    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        if (beanName.equals("userService")) {
            System.out.println("初始化前");
            ((UserService)bean).setName("123");
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        System.out.println("初始化后");
        return bean;
    }
}
