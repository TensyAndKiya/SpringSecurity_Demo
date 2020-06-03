package com.clei.config;

import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池配置类
 *
 * @author KIyA
 * @date 2020-06-01
 */
@Configuration
@EnableAsync
public class WorkPoolConfig {

    @Bean("workExecutor")
    public ThreadPoolTaskExecutor asyncServiceExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(9);
        executor.setQueueCapacity(20);
        executor.setThreadNamePrefix("work-thread-");
        executor.setKeepAliveSeconds(60);
        // 不在新线程执行任务 在调用者所在线程执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        /*executor.setRejectedExecutionHandler((r,exe) -> {
            exe.getQueue().add(r)
        });*/

        // 设置装饰器 这里只增添了MDC处理
        // 传入一个Runnable 返回一个 Runnable
        executor.setTaskDecorator(r -> {

            Map<String, String> mdcMap = MDC.getCopyOfContextMap();

            return () -> {
                MDC.setContextMap(mdcMap);

                r.run();

                MDC.clear();
            };

        });

        executor.initialize();
        return executor;
    }

}
