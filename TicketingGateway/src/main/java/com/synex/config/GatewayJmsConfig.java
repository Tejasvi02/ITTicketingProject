package com.synex.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;

import jakarta.jms.ConnectionFactory;

@Configuration
@EnableJms
public class GatewayJmsConfig {

	@Bean(name = "gatewayConnectionFactory")
	public ConnectionFactory connectionFactory() {
	    ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("admin", "admin", "tcp://localhost:61616");
	    factory.setTrustAllPackages(true);
	    return factory;
	}

	@Bean(name = "gatewayJmsTemplate")
	public JmsTemplate jmsTemplate(@Qualifier("gatewayConnectionFactory") ConnectionFactory connectionFactory) {
	    return new JmsTemplate(connectionFactory);
	}

}