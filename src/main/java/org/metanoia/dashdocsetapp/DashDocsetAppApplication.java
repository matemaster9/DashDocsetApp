package org.metanoia.dashdocsetapp;

import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.ConfigurableEnvironment;

@Slf4j
@SpringBootApplication
@ConfigurationPropertiesScan
public class DashDocsetAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(DashDocsetAppApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(ConfigurableEnvironment env) {
		return args -> {
			log.info("Application started:{}", env.getProperty("server.port"));
			log.info("javadocset-tool.location:{}", env.getProperty("dash-docset.javadocset-tool"));
			log.info("zsh-working-dir:{}", env.getProperty("dash-docset.zsh-working-dir"));
			log.info("docset-store-path:{}", env.getProperty("dash-docset.docset-store-path"));
			log.info("web-jar-temp-store-path:{}", env.getProperty("dash-docset.web-jar-temp-store-path"));
		};
	}
}
