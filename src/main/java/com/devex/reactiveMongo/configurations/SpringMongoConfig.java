package com.devex.reactiveMongo.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;

import com.mongodb.reactivestreams.client.MongoClient;

@Configuration
public class SpringMongoConfig 
{
	@Autowired
	MongoClient mongoClient;
	
	@Value("${spring.data.mongodb.database}")
	private String database;
	
	@Bean
    public ReactiveMongoTemplate reactiveMongoTemplate() 
	{
	  //remove _class
      ReactiveMongoTemplate template = new ReactiveMongoTemplate(mongoClient, database);
      MappingMongoConverter converter = (MappingMongoConverter) template.getConverter();
      converter.setTypeMapper(new DefaultMongoTypeMapper(null));
      converter.afterPropertiesSet();
      return template;
    }
}
