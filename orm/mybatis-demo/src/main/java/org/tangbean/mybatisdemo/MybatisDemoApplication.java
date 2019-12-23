package org.tangbean.mybatisdemo;

import lombok.extern.slf4j.Slf4j;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.tangbean.mybatisdemo.mapper.CoffeeMapper;
import org.tangbean.mybatisdemo.model.Coffee;

@SpringBootApplication
@Slf4j
@MapperScan("org.tangbean.mybatisdemo.mapper")
public class MybatisDemoApplication implements CommandLineRunner {
	@Autowired
	private CoffeeMapper coffeeMapper;

	public static void main(String[] args) {
		SpringApplication.run(MybatisDemoApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Coffee c = Coffee.builder().name("espresso")
				.price(Money.of(CurrencyUnit.of("CNY"), 20.0)).build();
		int count = coffeeMapper.save(c);
		log.info("Save {} Coffee: {}", count, c);

		c = Coffee.builder().name("latte")
				.price(Money.of(CurrencyUnit.of("CNY"), 25.0)).build();
		count = coffeeMapper.save(c);
		log.info("Save {} Coffee: {}", count, c);

		log.info("id: {}", c.getId());
		c = coffeeMapper.findById(c.getId());
		log.info("Find Coffee: {}", c);
	}
}
