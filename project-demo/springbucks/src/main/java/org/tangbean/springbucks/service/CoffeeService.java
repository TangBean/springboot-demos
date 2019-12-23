package org.tangbean.springbucks.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tangbean.springbucks.model.Coffee;
import org.tangbean.springbucks.model.CoffeeCache;
import org.tangbean.springbucks.repository.CoffeeRepository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
//@Transactional
public class CoffeeService {
    @Autowired
    private CoffeeRepository coffeeRepository;
    @Autowired
    private RedisTemplate<String, CoffeeCache> redisTemplate;

    private static final String CACHE_COLLECTION = "springbucks-coffee";

    public List<Coffee> findAllCoffee() {
        return coffeeRepository.findAll();
    }

    public Optional<Coffee> findOneCoffee(String name) {
        List<Coffee> coffees = coffeeRepository.findByName(name);
        Optional<Coffee> coffee = coffees.size() > 0 ? Optional.of(coffees.get(0)) : Optional.empty();
        return coffee;
    }

    public Optional<Coffee> findSimpleCoffeeFromCache(String name) {
        HashOperations<String, String, CoffeeCache> hashOperations = redisTemplate.opsForHash();
        // 命中缓存
        if (redisTemplate.hasKey(CACHE_COLLECTION) && hashOperations.hasKey(CACHE_COLLECTION, name)) {
            CoffeeCache coffeeCache = hashOperations.get(CACHE_COLLECTION, name);
            Coffee coffee = Coffee.builder()
                    .name(coffeeCache.getName())
                    .price(coffeeCache.getPrice())
                    .build();
            log.info("Get coffee {} from Redis.", name);
            return Optional.of(coffee);
        }

        Optional<Coffee> raw = findOneCoffee(name);
        log.info("Get coffee {} from Mongo.", name);
        // 将得到的Coffee对象存入缓存
        if (raw.isPresent()) {
            log.info("Put coffee {} to Redis.", name);
            Coffee c = raw.get();
            CoffeeCache coffeeCache = CoffeeCache.builder()
                    .id(c.getId())
                    .name(c.getName())
                    .price(c.getPrice())
                    .build();
            hashOperations.put(CACHE_COLLECTION, name, coffeeCache);
            redisTemplate.expire(CACHE_COLLECTION, 1, TimeUnit.MINUTES);
//            raw.ifPresent(c -> {
//                CoffeeCache coffeeCache = CoffeeCache.builder()
//                        .id(c.getId())
//                        .name(c.getName())
//                        .price(c.getPrice())
//                        .build();
//                log.info("Save Coffee {} to cache.", coffeeCache);
//                hashOperations.put(CACHE_COLLECTION, name, coffeeCache);
//                redisTemplate.expire(CACHE_COLLECTION, 1, TimeUnit.MINUTES);
//            });
        }
        return raw;
    }
}
