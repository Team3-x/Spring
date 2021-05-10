package com.spring;

/**
 * @author Team3
 * @date 2021/5/10 21:54
 */
public interface InitializingBean {

    void afterPropertiesSet() throws Exception;
}
