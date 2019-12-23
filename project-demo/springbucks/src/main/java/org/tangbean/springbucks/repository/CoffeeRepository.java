package org.tangbean.springbucks.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.tangbean.springbucks.model.Coffee;

import java.util.List;


public interface CoffeeRepository extends MongoRepository<Coffee, String> {
    List<Coffee> findByName(String name);
}
