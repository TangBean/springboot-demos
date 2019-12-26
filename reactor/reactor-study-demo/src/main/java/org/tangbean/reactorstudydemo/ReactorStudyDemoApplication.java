package org.tangbean.reactorstudydemo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;

@SpringBootApplication
@Slf4j
public class ReactorStudyDemoApplication implements ApplicationRunner {

	public static void main(String[] args) {
		SpringApplication.run(ReactorStudyDemoApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
//		testFluxStatic();
//		testFluxGenerate();
//		testFluxCreate();
//		testMonoSpecialMethod();
//		testBuffer();
//		testFilter();
//		testWindow();
//		testZipWith();
//		testTake();
//		testReduce();
//		testMerge();
//		testFlatMap();
//		testConcatMap();
//		testCombineLatest();

//		testMessageHandler();
//		testSchedules();
//		testDebug();
//		testColdAndWarmSequence();
	}
}
