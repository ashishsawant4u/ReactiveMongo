package com.devex.reactiveMongo.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.devex.reactiveMongo.entity.Product;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository("productRepository")
public interface ProductRepository extends ReactiveMongoRepository<Product, String>
{
	
	public Mono<Product> findByCode(String code);
	
	public Flux<Product> findAll();	
	
	public Flux<Product> findByPriceBetween(double to,double from);
}
