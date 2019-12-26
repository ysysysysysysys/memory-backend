package com.yesong.memory.memory.handler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.logging.MemoryHandler;

@Configuration
public class InterceptorConfig extends WebMvcConfigurationSupport {
    @Bean
    public MemoryHandler getMemoryHandler(){
        return new MemoryHandler();
    }


    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getMemoryHandler()).addPathPatterns("/**")
                .excludePathPatterns("/user/**")
                .excludePathPatterns("/oss/test");
        super.addInterceptors(registry);
    }

}
