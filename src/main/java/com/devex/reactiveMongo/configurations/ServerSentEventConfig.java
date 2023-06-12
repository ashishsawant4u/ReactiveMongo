package com.devex.reactiveMongo.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.devex.reactiveMongo.entity.Product;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Configuration
public class ServerSentEventConfig 
{
	@Bean
	public Sinks.Many<Product> productSink()
	{
		return Sinks.many().replay().limit(1);
	}
	
	@Bean
	public Flux<Product> productFlux(Sinks.Many<Product> productSink)
	{
		return productSink.asFlux();
	}
}
