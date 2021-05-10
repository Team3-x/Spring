package com.spring;

import com.sun.org.apache.bcel.internal.classfile.ClassParser;

import javax.sound.sampled.SourceDataLine;
import javax.xml.transform.Source;
import java.io.File;
import java.io.FileReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Team3
 * @date 2021/4/21 21:54
 */
public class SpringApplicationContext {

    private Class configClass;

    //单例池
    private ConcurrentHashMap<String, Object> singletonObjects = new ConcurrentHashMap<>();

    private ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();


    public SpringApplicationContext(Class configClass) {
        this.configClass = configClass;

        //解析配置类
        //ComponentScan解析--->扫描路径--->扫描--->BeanDefinition--->BeanDefinitionMap
        scan(configClass);

        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()) {
            String beanName = entry.getKey();
            BeanDefinition beanDefinition = entry.getValue();

            if (beanDefinition.getScope().equals("singleton")) {
                Object bean = createBean(beanName, beanDefinition);
                singletonObjects.put(beanName, bean);
            }
        }

    }

    public Object createBean(String beanName, BeanDefinition beanDefinition) {

        Class clazz = beanDefinition.getClazz();
        try {
            Object instance = clazz.getDeclaredConstructor().newInstance();

            //依赖注入
            for (Field declaredField : clazz.getDeclaredFields()) {
                if (declaredField.isAnnotationPresent(Autowired.class)) {
                    Object bean = getBean(declaredField.getName());
//                    if (bean == null) {
//
//                    }
                    declaredField.setAccessible(true);
                    declaredField.set(instance, bean);
                }
            }
            // Aware回调
            if (instance instanceof BeanNameAware) {
                ((BeanNameAware)instance).setBeanName(beanName);
            }

            // 初始化
            if (instance instanceof InitializingBean) {
                try {
                    ((InitializingBean)instance).afterPropertiesSet();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // BeanPostProcessor



             return instance;

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void scan(Class configClass) {

        ComponentScan componentScanAnnotation = (ComponentScan)configClass.getDeclaredAnnotation(ComponentScan.class);
        //扫描路径  com.test.service
        String path = componentScanAnnotation.value();
        //System.out.println(path);

        //扫描
        //Bootstrap-->jre/lib
        //Ext-------->jre/ext/lib
        //App-------->classpath

        //app
        path = path.replace(".", "/");
        ClassLoader classLoader = SpringApplicationContext.class.getClassLoader();
        URL resource = classLoader.getResource(path);
        File file = new File(resource.getFile());
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                //System.out.println(f);

                String fileName = f.getAbsolutePath();
                if (fileName.endsWith(".class")) {
                    String className = fileName.substring(fileName.indexOf("com"), fileName.indexOf(".class"));
                    className = className.replace("\\", ".");
                    //System.out.println(className);

                    try {
                        Class<?> clazz = classLoader.loadClass(className);

                        if (clazz.isAnnotationPresent(Component.class)) {
                            //表示当前这个类是一个Bean
                            //解析类，判断当前bean是单例，还是prototype原型bean
                            //BeanDefinition
                            Component componentAnnotation = clazz.getDeclaredAnnotation(Component.class);
                            String beanName = componentAnnotation.value();

                            BeanDefinition beanDefinition = new BeanDefinition();
                            beanDefinition.setClazz(clazz);

                            if (clazz.isAnnotationPresent(Scope.class)) {
                                Scope scopeAnnotation = clazz.getDeclaredAnnotation(Scope.class);
                                beanDefinition.setScope(scopeAnnotation.value());
                            } else {
                                beanDefinition.setScope("singleton");
                            }

                            beanDefinitionMap.put(beanName, beanDefinition);

                        }

                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                }

            }

        }
    }

    public Object getBean(String beanName) {

        if (beanDefinitionMap.containsKey(beanName)) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if (beanDefinition.getScope().equals("singleton")) {
                Object o = singletonObjects.get(beanName);
                return o;
            } else {
                //创建Bean对象
                Object bean = createBean(beanName, beanDefinition);
                return bean;
            }
        } else {
            //不存在对应的Bean
            throw new NullPointerException();
        }

    }

}
