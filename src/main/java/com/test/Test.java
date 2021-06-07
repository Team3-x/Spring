package com.test;

import com.spring.SpringApplicationContext;
import com.test.service.UserService;
import com.test.service.UserServiceImpl;

/**
 * @author Team3
 * @date 2021/4/21 21:53
 */
public class Test {

    public static void main(String[] args) {
        SpringApplicationContext applicationContext = new SpringApplicationContext(AppConfig.class);

        UserService userService = (UserService)applicationContext.getBean("userService");
        userService.test();  // 1.代理对象  2.业务test

        //测试scope注解
//        System.out.println(applicationContext.getBean("userService"));
//        System.out.println(applicationContext.getBean("userService"));
//        System.out.println(applicationContext.getBean("userService"));




    }
}
