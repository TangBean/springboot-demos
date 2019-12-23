package org.tangbean.mybatisgeneratordemo;

import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.RowBounds;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.tangbean.mybatisgeneratordemo.mapper.CoffeeMapper;
import org.tangbean.mybatisgeneratordemo.model.Coffee;
import org.tangbean.mybatisgeneratordemo.model.CoffeeExample;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootApplication
@Slf4j
@MapperScan("org.tangbean.mybatisgeneratordemo.mapper")
public class MybatisGeneratorDemoApplication implements ApplicationRunner {
	@Autowired
	private CoffeeMapper coffeeMapper;

	public static void main(String[] args) {
		SpringApplication.run(MybatisGeneratorDemoApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
//		generateArtifacts();
//		testMybatisGenerator();
		testPageHelper();
	}

	private void generateArtifacts() throws Exception {
		List<String> warnings = new ArrayList<String>();
		boolean overwrite = true;
		ConfigurationParser cp = new ConfigurationParser(warnings);
		Configuration config = cp.parseConfiguration(this.getClass().getResourceAsStream("/generatorConfig.xml"));
		DefaultShellCallback callback = new DefaultShellCallback(overwrite);
		MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
		myBatisGenerator.generate(null);
	}

	private void testMybatisGenerator() throws Exception{
		Coffee coffee = Coffee.builder()
				.name("latte")
				.price(Money.of(CurrencyUnit.of("CNY"), 30.0))
				.createTime(new Date())
				.updateTime(new Date()).build();
		coffeeMapper.insert(coffee);

		coffee = Coffee.builder()
				.name("espresso")
				.price(Money.of(CurrencyUnit.of("CNY"), 20.0))
				.createTime(new Date())
				.updateTime(new Date()).build();
		coffeeMapper.insert(coffee);

		Coffee coffeeSearch = coffeeMapper.selectByPrimaryKey(1L);
		log.info(coffeeSearch.toString());

		CoffeeExample example = new CoffeeExample();
		example.createCriteria().andNameEqualTo("latte");
		List<Coffee> list = coffeeMapper.selectByExample(example);
		list.forEach(e -> log.info("selectByExample: {}", e));
	}

	private void testPageHelper() throws Exception{
		PageHelper.startPage(1, 3);
		CoffeeExample example = new CoffeeExample();
		example.createCriteria().andCreateTimeLessThan(new Date());
		List<Coffee> coffees = coffeeMapper.selectByExample(example);

		coffees.forEach(coffee -> log.info("Page(1) Coffee: {}", coffee));
	}
}
