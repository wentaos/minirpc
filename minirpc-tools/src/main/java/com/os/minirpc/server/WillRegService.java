package com.os.minirpc.server;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

/**
 * RPC 服务注解
 * PS：将此注解使用在需要被注册的服务实现类上
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface WillRegService {
	/**
	 * 默认属性为 服务接口
	 */
	Class<?> value();
	
	/**
	 * 服务版本号 区别服务
	 */
	String version() default "";
}
