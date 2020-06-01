package com.clei.controller;

import com.alibaba.fastjson.JSONObject;
import com.clei.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * hello controller
 *
 * @author KIyA
 * @date 2020-04-14
 */
@RestController
public class HelloController {

    @Autowired
    private SessionRegistry sessionRegistry;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    @Qualifier("workExecutor")
    private ThreadPoolTaskExecutor workExecutor;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value = "hello")
    public List<Map<String,Object>> hello(){
        logger.info("hello");

        Map<String,Object> m1 = new HashMap<>();
        m1.put("cname","英语");
        m1.put("sname","杨必宇");
        m1.put("score",0);
        Map<String,Object> m2 = new HashMap<>();
        m2.put("cname","数学");
        m2.put("sname","杨必宇");
        m2.put("score",0);

        List<Map<String,Object>> list = new ArrayList<>();
        list.add(m1);
        list.add(m2);

        for (int i = 0; i < list.size(); i++) {
            Map<String,Object> m = list.get(i);

            m.put("id",i);
        }

        // 这个要是在父线程调用 不能在创建的线程内 不然还是没有
        Map<String, String> mdcMap = MDC.getCopyOfContextMap();

        new Thread(() -> {

            logger.info("mdcMap : " + mdcMap);

            // 子线程接收 mdc变量
            if(null != mdcMap){
                MDC.setContextMap(mdcMap);
            }

            logger.info("what the fuck is wrong with you?");
        }).start();

        workExecutor.execute(() -> {
            logger.info("what the fuck is wrong with you??");
        });

        return list;

        // return "Hello World!";
    }

    @RequestMapping(value = "admin/hello")
    public String helloAdmin(){
        logger.info("helloAdmin");

        return "Hello Admin!";
    }

    @RequestMapping(value = "user/hello")
    public String helloUser(){
        logger.info("helloUser");

        return "Hello User!";
    }

    @RequestMapping(value = "index")
    public String index(){
        logger.info("index");

        return "Index";
    }

    @RequestMapping(value = "remember")
    public String remember(){
        logger.info("remember");

        return "remember";
    }

    @RequestMapping(value = "loginFailure")
    public String loginFailure(){
        logger.info("loginFailure");

        return "loginFailure";
    }

    @RequestMapping(value = "test")
    public String test(){

        List<Object> allPrincipals = sessionRegistry.getAllPrincipals();

        logger.info("all principal size : " + allPrincipals.size());

        logger.info("all principal : " + JSONObject.toJSONString(allPrincipals));

        for (Object obj : allPrincipals){

            List<SessionInformation> allSessions = sessionRegistry.getAllSessions(obj, true);

            logger.info("session information size : " + allSessions.size());

            logger.info("session information : " + JSONObject.toJSONString(allSessions));

        }

        return "test";
    }


    /**
     * redis set exists del 测试
     * @param param
     * @return
     */
    @RequestMapping(value = "redisTest")
    public String redisTest(@RequestParam Map<String,Object> param){
        logger.info("redisTest param : " + param);

        param.forEach((k,v) -> {
            redisUtil.set(k,v);
        });

        param.forEach((k,v) -> {
            logger.info(k + " exists " + redisUtil.exists(k));
        });

        param.forEach((k,v) -> {
            logger.info(k + " del " + redisUtil.del(k));
        });

        param.forEach((k,v) -> {
            logger.info(k + " exists " + redisUtil.exists(k));
        });

        return "redisTest";
    }

    /**
     * redis set 设置时间测试
     * @param param
     * @return
     */
    @RequestMapping(value = "redisTest2")
    public String redisTest2(@RequestParam Map<String,Object> param){
        logger.info("redisTest2 param : " + param);

        param.forEach((k,v) -> {
            redisUtil.set(k,v,1000, TimeUnit.MILLISECONDS);
        });

        param.forEach((k,v) -> {
            logger.info(k + " exists " + redisUtil.exists(k));
        });

        try {
            Thread.sleep(1000);
        }catch (Exception e){
            logger.error("redisTest2 error : " , e);
        }

        param.forEach((k,v) -> {
            logger.info(k + " exists " + redisUtil.exists(k));
        });

        return "redisTest2";
    }

}
