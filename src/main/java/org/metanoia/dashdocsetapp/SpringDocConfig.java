package org.metanoia.dashdocsetapp;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author cyborg
 * @since 2024-07-03 13:17
 */
@Configuration(proxyBeanMethods = false)
public class SpringDocConfig {


	@Bean
	public OpenAPI dashDocsetAppOpenAPI() {
		Info info = new Info();
		info.setTitle("DashDocSetApp");
		info.setVersion("v1.0.2");

		License license = new License();
		license.setName("Apache License Version 2.0");
		license.setUrl("http://www.apache.org/licenses/LICENSE-2.0.html");
		info.setLicense(license);


		return new OpenAPI().info(info);
	}
}
