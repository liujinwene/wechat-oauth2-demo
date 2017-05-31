package com.everhomes.base.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@ComponentScan("com.everhomes")
@EnableAutoConfiguration(exclude={
	    DataSourceAutoConfiguration.class, 
	    HibernateJpaAutoConfiguration.class,
	    FreeMarkerAutoConfiguration.class
	})
@EnableScheduling
@PropertySource(value={"classpath:config/application.properties"}, ignoreResourceNotFound=true)
@ImportResource("classpath:META-INF/applicationContext.xml")
public class CoreServerApp {
	
	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(CoreServerApp.class);
		app.setWebEnvironment(true);
		app.run(args);
	}

}
