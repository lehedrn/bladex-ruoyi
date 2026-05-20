package org.springblade.core.secure.config;

import lombok.AllArgsConstructor;
import org.springblade.core.secure.aspect.AdminAspect;
import org.springblade.core.secure.aspect.AdministratorAspect;
import org.springblade.core.secure.aspect.AuthAspect;
import org.springblade.core.secure.auth.AuthFun;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * 权限注解配置类
 *
 * @author BladeX
 */
@EnableAspectJAutoProxy(proxyTargetClass = true)
@AutoConfiguration
@AllArgsConstructor
public class PreAuthConfiguration {

	@Bean
	public AuthAspect authAspect() {
		return new AuthAspect(new AuthFun());
	}

	@Bean
	public AdminAspect adminAspect() {
		return new AdminAspect();
	}

	@Bean
	public AdministratorAspect administratorAspect() {
		return new AdministratorAspect();
	}

}
