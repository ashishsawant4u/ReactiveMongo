package com.devex.reactiveMongo.controllers;

import java.util.Optional;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devex.reactiveMongo.entity.Product;
import com.devex.reactiveMongo.repository.ProductRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@RestController
@RequestMapping("/prod")
public class ProductsRestController 
{
	@Resource(name = "productSink")
	Sinks.Many<Product> productSink;
	
	@Resource(name = "productFlux")
	Flux<Product> productFlux;
	
	@Resource(name = "productRepository")
	ProductRepository productRepository;
	
	
	@GetMapping("/all")
	public Flux<Product> getProducts()
	{
		return productRepository.findAll();
	}
	
	@GetMapping("/get/{code}")
	public Mono<ResponseEntity<Product>> findByCode(@PathVariable String code)
	{
		
		
		return productRepository.findByCode(code)
								.map(p -> new ResponseEntity<>(p, HttpStatus.OK))
								.defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
	
	@PostMapping("/save")
	public Mono<ResponseEntity<Product>> saveProduct(@RequestBody Mono<Product> newProd)
	{
		return newProd
					  .flatMap(p -> productRepository.save(p))
					  .map(p -> new ResponseEntity<>(p, HttpStatus.ACCEPTED))
					  .doOnNext(p -> productSink.tryEmitNext(p.getBody()));
	}
	
	@PutMapping("/update/{code}")
	public Mono<ResponseEntity<Product>> updateProduct(@PathVariable String code,@RequestBody Mono<Product> updateProd)
	{
		return productRepository.findByCode(code)
								.map(Optional::of)
								.defaultIfEmpty(Optional.empty())
								.flatMap(exProd -> {
									          if (exProd.isPresent()) 
									          {
									        	  return updateProd
														  .flatMap(p -> {
															  p.set_id(exProd.get().get_id());
															  return productRepository.save(p);
														  })
														  .map(p -> new ResponseEntity<>(p, HttpStatus.ACCEPTED));
									          }
							
									          return Mono.just(ResponseEntity.notFound().build());
									        });
	}
	
	@DeleteMapping("/delete/{code}")
	public Mono<Void> deleteByCode(@PathVariable String code)
	{
		return productRepository.findByCode(code)
				                .flatMap(p -> productRepository.delete(p));
	}
	
	@GetMapping("/getByPriceRange/{min}/{max}")
	public Flux<Product> findByPriceRange(@PathVariable("min") String min,@PathVariable("max") String max)
	{
		return productRepository.findByPriceBetween(Double.parseDouble(min), Double.parseDouble(max));
	}
	
	@GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<Product> streamNewProd()
	{
		return productFlux;
	}
}
