package org.tangbean.springbucks;

import io.lettuce.core.ReadFrom;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.convert.RedisCustomConversions;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.tangbean.springbucks.converter.BytesToMoneyConverter;
import org.tangbean.springbucks.converter.MoneyReadConverter;
import org.tangbean.springbucks.converter.MoneyToBytesConverter;
import org.tangbean.springbucks.model.Coffee;
import org.tangbean.springbucks.model.CoffeeCache;
import org.tangbean.springbucks.service.CoffeeService;

import java.util.Arrays;
import java.util.Optional;

@SpringBootApplication
@Slf4j
@EnableTransactionManagement(mode = AdviceMode.PROXY)
@EnableMongoRepositories
public class SpringbucksApplication implements ApplicationRunner {
	@Autowired
	private CoffeeService coffeeService;

	public static void main(String[] args) {
		SpringApplication.run(SpringbucksApplication.class, args);
	}

	@Bean
	public MongoCustomConversions mongoCustomConversions() {
		return new MongoCustomConversions(Arrays.asList(new MoneyReadConverter()));
	}

	@Bean
	public RedisTemplate<String, CoffeeCache> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<String, CoffeeCache> template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory);
		return template;
	}

	@Bean
	public LettuceClientConfigurationBuilderCustomizer customizer() {
		return builder -> builder.readFrom(ReadFrom.MASTER_PREFERRED);
	}

	@Bean
	public RedisCustomConversions redisCustomConversions() {
		return new RedisCustomConversions(
				Arrays.asList(new MoneyToBytesConverter(), new BytesToMoneyConverter()));
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
//		testMongo();
		testRedis();
	}

	private void testRedis() {
		Optional<Coffee> c = coffeeService.findSimpleCoffeeFromCache("latte-3");
		log.info("Coffee {}", c);

		for (int i = 0; i < 5; i++) {
			c = coffeeService.findSimpleCoffeeFromCache("latte-3");
			log.info("Value from Redis: {}", c);
		}
	}

	private void testMongo() {
		Optional<Coffee> coffee = coffeeService.findOneCoffee("latte-3");
		log.info("Coffee {}", coffee);
	}
}
