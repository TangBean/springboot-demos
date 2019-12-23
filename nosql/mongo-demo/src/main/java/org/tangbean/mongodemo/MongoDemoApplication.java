package org.tangbean.mongodemo;

import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.tangbean.mongodemo.converter.MoneyReadConverter;
import org.tangbean.mongodemo.model.Coffee;
import org.tangbean.mongodemo.repository.CoffeeRepository;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@SpringBootApplication
@Slf4j
@EnableMongoRepositories
public class MongoDemoApplication implements ApplicationRunner {
	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private CoffeeRepository coffeeRepository;

	public static void main(String[] args) {
		SpringApplication.run(MongoDemoApplication.class, args);
	}

	@Bean
	public MongoCustomConversions mongoCustomConversions() {
		return new MongoCustomConversions(Arrays.asList(new MoneyReadConverter()));
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
//		templateDemo();
//		repositoryDemo();
//		templateCrudCudDemo();
		insertDataForCrudR();
//		templateCrudRDemo();
	}

	private void templateDemo() throws InterruptedException {
		Coffee espresso = Coffee.builder()
				.name("espresso")
				.price(Money.of(CurrencyUnit.of("CNY"), 20.0))
				.createTime(new Date())
				.updateTime(new Date()).build();
		Coffee savedCoffee = mongoTemplate.save(espresso);
		log.info("Coffee: {}", savedCoffee);

		List<Coffee> coffees = mongoTemplate.find(Query.query(Criteria.where("name").is("espresso")), Coffee.class);
		log.info("Coffees size: {}", coffees.size());
		coffees.forEach(c -> log.info("Coffee: {}", c));

		Thread.sleep(1000);

		UpdateResult result = mongoTemplate.updateFirst(
				Query.query(Criteria.where("name").is("espresso")),
				new Update().set("price", Money.ofMajor(CurrencyUnit.of("CNY"), 30)).currentDate("updateDate"),
				Coffee.class);
		log.info("Update Result: {}", result.getModifiedCount());
		Coffee updateOne = mongoTemplate.findById(savedCoffee.getId(), Coffee.class);
		log.info("Update Result: {}", updateOne);

		log.info("DB size: {}", mongoTemplate.count(Query.query(Criteria.where("name").is("espresso")), Coffee.class));
		mongoTemplate.remove(updateOne);
		log.info("DB size: {}", mongoTemplate.count(Query.query(Criteria.where("name").is("espresso")), Coffee.class));
	}

	private void templateCrudCudDemo() throws Exception {
		// 增
		Coffee espresso = Coffee.builder()
				.name("espresso")
				.price(Money.of(CurrencyUnit.of("CNY"), 20.0))
				.createTime(new Date())
				.updateTime(new Date()).build();
		Coffee savedCoffee = mongoTemplate.save(espresso);
		log.info("Coffee: {}", savedCoffee);

		// 改
		Query query = new Query(Criteria.where("_id").is(savedCoffee.getId()));
		mongoTemplate.find(query, Coffee.class).forEach(c -> log.info("before update: {}", c));

		Update update = new Update();
		update.set("price", Money.ofMajor(CurrencyUnit.of("CNY"), 30));
		update.currentDate("updateDate");
		UpdateResult updateResult = mongoTemplate.updateFirst(query, update, Coffee.class);

		mongoTemplate.find(query, Coffee.class).forEach(c -> log.info("after update: {}", c));

		// 删
		log.info("DB size: {}", mongoTemplate.count(Query.query(Criteria.where("name").is("espresso")), Coffee.class));
		mongoTemplate.remove(query, Coffee.class);
		log.info("DB size: {}", mongoTemplate.count(Query.query(Criteria.where("name").is("espresso")), Coffee.class));
	}

	private void insertDataForCrudR() {
		for (int i = 0; i < 5; i++) {
			Coffee espresso = Coffee.builder()
					.name("espresso-" + i)
					.price(Money.of(CurrencyUnit.of("CNY"), 20.0))
					.createTime(new Date())
					.updateTime(new Date()).build();
			mongoTemplate.save(espresso);

			Coffee latte = Coffee.builder()
					.name("latte-" + i)
					.price(Money.of(CurrencyUnit.of("CNY"), 30.0))
					.createTime(new Date())
					.updateTime(new Date()).build();
			mongoTemplate.save(latte);
		}
	}

	private void templateCrudRDemo() {
		// 单条件查询
		Query query = new Query(Criteria.where("name").is("latte"));
		List<Coffee> coffees = mongoTemplate.find(query, Coffee.class);
		coffees.forEach(c -> log.info("Coffee: {}", c));

		// 复合条件查询 & 排序 (使用 Criteria)
		Criteria criteria = new Criteria();
		criteria.orOperator(Criteria.where("name").is("latte"),
				Criteria.where("price").is(Money.ofMajor(CurrencyUnit.of("CNY"), 20)));
		Query query1 = new Query(criteria);
		Sort sort = Sort.by(Sort.Direction.ASC.DESC, "id");
		query1.with(sort);
		List<Coffee> coffees1 = mongoTemplate.find(query1, Coffee.class);
		coffees1.forEach(c -> log.info("Coffee: {}", c));

		// 正则表达式查询 & 分页条件查询
		Query query2 = new Query(Criteria.where("name").regex("latte"));
		query2.skip(1).limit(2);  // 分页，相当于 LIMIT 1 2
		List<Coffee> coffees2 = mongoTemplate.find(query2, Coffee.class);
		coffees2.forEach(c -> log.info("Coffee: {}", c));
	}

	private void repositoryDemo() throws InterruptedException {
		Coffee espresso = Coffee.builder()
				.name("espresso")
				.price(Money.of(CurrencyUnit.of("CNY"), 20.0))
				.createTime(new Date())
				.updateTime(new Date()).build();
		Coffee latte = Coffee.builder()
				.name("latte")
				.price(Money.of(CurrencyUnit.of("CNY"), 30.0))
				.createTime(new Date())
				.updateTime(new Date()).build();

		coffeeRepository.insert(Arrays.asList(espresso, latte));
		coffeeRepository.findAll(Sort.by("name")).forEach(c -> log.info("Coffee: {}", c));

		Thread.sleep(1000);
		latte.setPrice(Money.of(CurrencyUnit.of("CNY"), 35));
		latte.setUpdateTime(new Date());
		coffeeRepository.save(latte);
		coffeeRepository.findByName("latte").forEach(c -> log.info("Coffee: {}", c));

		log.info("db size: {}", coffeeRepository.count());
		coffeeRepository.deleteAll();
		log.info("db size: {}", coffeeRepository.count());
	}
}
