package com.han.ruoji;

import com.han.ruoji.common.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.cbor.MappingJackson2CborHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;


@Slf4j
@SpringBootApplication
@ServletComponentScan
@EnableTransactionManagement(proxyTargetClass = true)
@EnableAsync
public class RuojiAppcaton implements WebMvcConfigurer {
    public static void main(String[] args) {
        SpringApplication.run(RuojiAppcaton.class,args);
        log.info("项目已启动");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("开始静态资源映射");
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");

    }

    //扩展消息转换器
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {

        //防止java转json 导致js数据精度不够
        //创建消息转换器对象
        MappingJackson2HttpMessageConverter messageConverter  = new MappingJackson2HttpMessageConverter();
        //设置对象转换器，使用 jackson将java对象转为json
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        //将上面的转换器对象追加到mvc底层框架转换器集合中‘
        converters.add(0,messageConverter);

    }
}
