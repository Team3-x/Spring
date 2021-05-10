package com.spring;

/**
 * @author Team3
 * @date 2021/4/27 21:45
 */
public class BeanDefinition {

    private Class  clazz;
    private String scope;


    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
