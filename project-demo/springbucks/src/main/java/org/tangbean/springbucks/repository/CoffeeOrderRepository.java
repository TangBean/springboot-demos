package org.tangbean.springbucks.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.tangbean.springbucks.model.CoffeeOrder;

public interface CoffeeOrderRepository extends MongoRepository<CoffeeOrder, String> {
}
