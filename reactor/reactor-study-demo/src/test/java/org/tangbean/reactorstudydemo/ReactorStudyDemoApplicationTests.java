package org.tangbean.reactorstudydemo;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;
import reactor.test.publisher.TestPublisher;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.Random;

/**
 * References: https://www.ibm.com/developerworks/cn/java/j-cn-with-reactor-response-encode/index.html (Awesome!)
 * Project Reactor Docs: https://projectreactor.io/docs/core/release/reference/index.html
 */
@SpringBootTest(classes = ReactorStudyDemoApplication.class)
@Slf4j
class ReactorStudyDemoApplicationTests {

	/* Flux & Mono */
	@Test
	void testFluxStatic() {
		Flux.just("Hello", "World").subscribe(System.out::println);

		Flux.fromArray(new Integer[]{1, 2, 3}).subscribe(System.out::println);

		Flux.empty().subscribe(System.out::println);

		Flux.range(1, 10).subscribe(System.out::println);

		// 创建一个包含了从 0 开始递增的 Long 对象的序列。其中包含的元素按照指定的间隔来发布。
		// 除了间隔时间之外，还可以指定起始元素发布之前的延迟时间。
		Flux.interval(Duration.of(10, ChronoUnit.SECONDS)).subscribe(System.out::println);
	}

	@Test
	void testFluxGenerate() {
		/*
		public static <T> Flux<T> generate(Consumer<SynchronousSink<T>> generator)
		generator: 消耗Reactor为每个Subscriber提供的SynchronousSink，消耗后会生成一个单独的信号
		*/
		log.info("one parameter generate:");
		Flux.generate(sink -> {
			sink.next("Hello");
			sink.complete();
		}).subscribe(System.out::println);

		/*
		public static <T, S> Flux<T> generate(Callable<S> stateSupplier, BiFunction<S, SynchronousSink<T>, S> generator)
		called for each incoming Subscriber to provide the initial state for the generator bifunction
		stateSupplier: 为每个即将到来的Subscriber的generate提供一个初始状态
		generator: 同单参数的generate

		next() 方法只能最多被调用一次。
		*/
		log.info("two parameter generate:");
		final Random random = new Random();
		Flux.generate(ArrayList::new, (list, sink) -> {
			int value = random.nextInt(100);
			list.add(value);
			sink.next(value + "\t" + list.toString());
			if (list.size() == 10) {
				sink.complete();
			}
			return list;
		}).subscribe(System.out::println);
	}

	@Test
	void testFluxCreate() {
		/*
		区别于 generate，create 使用的是 FluxSink，而不是 SynchronousSink，
		FluxSink 支持同步和异步的消息产生，并且可以在一次调用中产生多个元素，即 next() 可以被调用多次。
		public static <T> Flux<T> create(Consumer<? super FluxSink<T>> emitter)
		*/
		Flux.create(sink -> {
			for (int i = 0; i < 10; i++) {
				sink.next(i);
			}
			sink.complete();
		}).subscribe(System.out::println);
	}

	@Test
	void testMonoSpecialMethod() {
		Mono.fromSupplier(() -> "Hello").subscribe(System.out::println);

		Mono.justOrEmpty(Optional.of("Hello")).subscribe(System.out::println);
		Mono.justOrEmpty(Optional.empty()).subscribe(System.out::println);  // No Output, because of the empty Optional

		Mono.create(sink -> sink.success("Hello")).subscribe(System.out::println);
	}

	/* 操作符 */

	/**
	 * 这两个操作符的作用是把当前流中的元素收集到集合中，并把集合对象作为流中的新元素。
	 */
	@Test
	void testBuffer() throws Exception {
		/* Flux<List<T>> buffer(int maxSize)，收集的元素个数大于 maxSize 就向流中输出一次 */
		Flux.range(1, 100).buffer(20).subscribe(System.out::println);

		Flux.range(1, 10).bufferUntil(i -> i % 2 == 0).subscribe(System.out::println);

		Flux.range(1, 10).bufferWhile(i -> i % 2 == 0).subscribe(System.out::println);

		/* Flux<List<T>> bufferTimeout(int maxSize, Duration maxTime)，除了 maxTime 的限制以外，还加上了 maxTime 的限制 */

		// 触发 maxSize 限制
		Flux.range(1, 100).bufferTimeout(30, Duration.of(1, ChronoUnit.SECONDS)).subscribe(System.out::println);

		// 触发 maxTime 限制
		Flux.generate(ArrayList::new, (list, sink) -> {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			sink.next(list.size());
			list.add(0);
			if (list.size() == 30) {
				sink.complete();
			}
			return list;
		}).bufferTimeout(30, Duration.of(10, ChronoUnit.SECONDS)).subscribe(System.out::println);
	}

	/**
	 * 对流中包含的元素进行过滤，只留下满足Predicate指定条件的元素。
	 */
	@Test
	void testFilter() {
		Flux.range(1, 10).filter(i -> i % 2 == 0).subscribe(System.out::println);
	}

	/**
	 * window 操作符的作用类似于 buffer，所不同的是 window 操作符是把当前流中的元素收集到另外的 Flux 序列中，因此返回值类型是 Flux<Flux>。
	 */
	@Test
	void testWindow() {
		// 会输出 5 个 UnicastProcessor，因为 window 操作符所产生的流中包含的是 UnicastProcessor 类的对象，
		// 而 UnicastProcessor 类的 toString 方法输出的就是 UnicastProcessor 字符。
		Flux.range(1, 100).window(20).subscribe(System.out::println);
	}

	/**
	 * zipWith 操作符把当前流中的元素与另外一个流中的元素按照一对一的方式进行合并。
	 */
	@Test
	void testZipWith() {
		// 不指定合并函数 combinator，则会将 "a" 和 "b" 放入一个 Tuple 中，然后将这个 Tuple 放入流中
		Flux.just("a", "b")
				.zipWith(Flux.just("c", "d"))
				.subscribe(System.out::println);
		// 指定了合并函数，则使用合并函数处理
		Flux.just("a", "b")
				.zipWith(Flux.just("c", "d"), (s1, s2) -> String.format("%s-%s", s1, s2))
				.subscribe(System.out::println);
	}

	/**
	 * take 系列操作符可以用来从当前流中花式提取元素。
	 */
	@Test
	void testTake() throws Exception {
		Flux.range(1, 1000).take(10).subscribe(System.out::println);

		Flux.range(1, 1000).takeLast(10).subscribe(System.out::println);

		Flux.range(1, 1000).takeWhile(i -> i < 10).subscribe(System.out::println);

		Flux.range(1, 1000).takeUntil(i -> i == 10).subscribe(System.out::println);

		Flux.range(1, 4).takeUntilOther(Flux.never()).subscribe(System.out::println);
	}

	/**
	 * reduce & reduceWith
	 * 对流中包含的所有元素进行累积操作，得到一个包含计算结果的 Mono 序列。累积操作是通过一个 BiFunction 来表示的。
	 * reduceWith 在操作时可以指定一个初始值。
	 */
	@Test
	void testReduce() {
		Flux.range(1, 100).reduce(Integer::sum).subscribe(System.out::println);  // 5050

		Flux.range(1, 100).reduceWith(() -> 100, Integer::sum).subscribe(System.out::println);  // 5150
	}

	/**
	 * merge & mergeSequential
	 * 把多个流合并成一个 Flux 序列。
	 */
	@Test
	void testMerge() {
		// merge 按照所有流中元素的实际产生顺序来合并，输出：0011223344
		Flux.merge(Flux.interval(Duration.of(0, ChronoUnit.MILLIS), Duration.of(100, ChronoUnit.MILLIS)).take(5),
				Flux.interval(Duration.of(50, ChronoUnit.MILLIS), Duration.of(100, ChronoUnit.MILLIS)).take(5))
				.toStream().forEach(System.out::print);
		// mergeSequential 按照所有流被订阅的顺序，以流为单位进行合并，输出：0123401234
		Flux.mergeSequential(Flux.interval(Duration.of(0, ChronoUnit.MILLIS), Duration.of(100, ChronoUnit.MILLIS)).take(5),
				Flux.interval(Duration.of(50, ChronoUnit.MILLIS), Duration.of(100, ChronoUnit.MILLIS)).take(5))
				.toStream().forEach(System.out::print);
		/*
		使用 toStream() 把 Flux 序列转换成 Java 8 中的 Stream 对象，再通过 forEach() 进行输出的原因：
		因为序列的生成是异步的，而转换成 Stream 对象可以保证主线程在序列生成完成之前不会退出，从而可以正确地输出序列中的所有元素。
		*/
	}

	/**
	 * flatMap & flatMapSequential
	 * 把流中的每个元素转换成一个流，再把所有流中的元素进行合并。
	 * flatMapSequential 和 flatMap 之间的区别与 mergeSequential 和 merge 之间的区别是一样的。
	 */
	@Test
	void testFlatMap() {
		// 输出：0011223344
		Flux.just(5, 10).flatMap(x -> Flux.interval(
				Duration.of(x * 10, ChronoUnit.MILLIS), Duration.of(100, ChronoUnit.MILLIS)).take(5))
				.toStream().forEach(System.out::print);
	}

	/**
	 * concatMap
	 * 把流中的每个元素转换成一个流，再把所有流进行合并。
	 */
	@Test
	void testConcatMap() {
		// 输出：012340123456789
		// 与 flatMap 不同的是，concatMap 会根据原始流中的元素顺序依次把转换之后的流进行合并；
		// 与 flatMapSequential 不同的是，concatMap 对转换之后的流的订阅是动态进行的，而 flatMapSequential 在合并之前就已经订阅了所有的流。
		Flux.just(5, 10).concatMap(x -> Flux.interval(
				Duration.of(x * 10, ChronoUnit.MILLIS), Duration.of(100, ChronoUnit.MILLIS)).take(x))
				.toStream().forEach(System.out::print);
	}

	/**
	 * combineLatest
	 * 把所有流中的最新产生的元素合并成一个新的元素，作为返回结果流中的元素。
	 * 只要其中任何一个流中产生了新的元素，合并操作就会被执行一次，结果流中就会产生新的元素。
	 */
	@Test
	void testCombineLatest() {
		// 输出：
		// [0, 0]
		// [0, 1]
		// [1, 1]
		// [1, 2]
		// [2, 2]
		// [2, 3]
		// [3, 3]
		// [3, 4]
		// [4, 4]
		// 除了第一次输出需要等待两个流中都有元素以外，其他的只要有一个流产生了新元素，就会执行一次输出，所以一共有 9 次，而不是 10 次
		Flux.combineLatest(
				Arrays::toString,
				Flux.interval(Duration.of(100, ChronoUnit.MILLIS)).take(5),
				Flux.interval(Duration.of(50, ChronoUnit.MILLIS), Duration.of(100, ChronoUnit.MILLIS)).take(5)
		).toStream().forEach(System.out::println);
	}

	/* 消息处理 */

	@Test
	void testMessageHandler() {
		/*
		subscribe 方法有 4 种调用方法，即可以传入 0、1、2、3 个参数，即正常消息处理器、错误消息处理器和完成消息处理器，它们分别如下：
		- subscribe()
		- subscribe(Consumer<? super T> consumer)
		- subscribe(@Nullable Consumer<? super T> consumer, Consumer<? super Throwable> errorConsumer)
		- subscribe(
			@Nullable Consumer<? super T> consumer,
			@Nullable Consumer<? super Throwable> errorConsumer,
			@Nullable Runnable completeConsumer)

		其实它们本质上都是调用了 3 个参数的 subscribe 方法，只不过没传的参数传入了 null 而已，即
		subscribe(consumer);
		等价于调用
		subscribe(consumer, null, null);
		*/
		Flux.just(1, 2)
				.concatWith(Mono.error(new IllegalStateException()))
				.subscribe(System.out::println, System.err::println);

		/* 其他处理错误的方法 */
		Flux.just(1, 2)
				.concatWith(Mono.error(new IllegalStateException()))
				.onErrorReturn(0)
				.subscribe(System.out::println);

		/*
		onErrorResume & doOnError
		- onErrorResume: Gives a fallback stream when some exception occurs happens in the upstream.
		- doOnError: Side-effect operator. Suppose you want to log what error happens in the upstream.

		当然，如果你想在 onErrorResume 中打 log 也没人拦你。
		*/
		Flux.just(1, 2)
				.concatWith(Mono.error(new IllegalStateException()))
				.doOnError(err -> log.error("Some error occurred while making the POST call", err))
				.onErrorResume(e -> {
					if (e instanceof IllegalStateException) {
						return Mono.just(-1);
					} else if (e instanceof IllegalArgumentException) {
						return Mono.just(0);
					}
					return Mono.empty();
				})
				.subscribe(System.out::println);

		// 也可以重试，这部分测试不通过就是这部分的原因，因为这部分会抛出 IllegalStateException 异常
		Flux.just(1, 2)
				.concatWith(Mono.error(new IllegalStateException()))
				.retry(3)
				.subscribe(System.out::println);
	}

	/* 调度器 */

	/**
	 * 通过调度器（Scheduler）可以指定这些操作执行的方式和所在的线程。
	 */
	@Test
	void testSchedules() {
		/*
		当前线程，通过 Schedulers.immediate()方法来创建。
		单一的可复用的线程，通过 Schedulers.single()方法来创建。
		使用弹性的线程池，通过 Schedulers.elastic()方法来创建。线程池中的线程是可以复用的。当所需要时，新的线程会被创建。如果一个线程闲置太长时间，则会被销毁。该调度器适用于 I/O 操作相关的流的处理。
		使用对并行操作优化的线程池，通过 Schedulers.parallel()方法来创建。其中的线程数量取决于 CPU 的核的数量。该调度器适用于计算密集型的流的处理。
		使用支持任务调度的调度器，通过 Schedulers.timer()方法来创建。
		从已有的 ExecutorService 对象中创建调度器，通过 Schedulers.fromExecutorService()方法来创建。
		*/

		/*
		如何切换执行操作的调度器？
		通过 publishOn() 和 subscribeOn() 方法。
		- publishOn()：切换操作符的执行方式
		- subscribeOn()：切换产生流中元素时的执行方式
		*/

		/*
		map() 函数的用途：
		通过一个同步的函数，改变每个从 Flux 中发射出的元素。
		*/

		// 输出：[elastic-2] [single-1] parallel-1
		Flux.create(sink -> {
			sink.next(Thread.currentThread().getName());
			sink.complete();
		})
				// 下面两对 publishOn 和 map 的作用：先切换执行时的调度器，再把当前的线程名称作为前缀添加
				.publishOn(Schedulers.single())
				.map(x -> String.format("[%s] %s", Thread.currentThread().getName(), x))
				.publishOn(Schedulers.elastic())
				.map(x -> String.format("[%s] %s", Thread.currentThread().getName(), x))
				.subscribeOn(Schedulers.parallel()) // 改变流产生时的执行方式
				.toStream()
				.forEach(System.out::println);
	}

	/* 测试 */

	/**
	 * 对使用 Reactor 的代码进行测试时，需要用到 io.projectreactor.addons:reactor-test 库。
	 */
	@Test
	void testTest() {
		/*
		StepVerifier
		*/
		StepVerifier.create(Flux.just("a", "b"))
				.expectNext("a")
				.expectNext("b")
				.verifyComplete();

		/*
		操作测试时间
		*/
		StepVerifier.withVirtualTime(() -> Flux.interval(Duration.ofHours(4), Duration.ofDays(1)).take(2))
				.expectSubscription()
				.expectNoEvent(Duration.ofHours(4))
				.expectNext(0L)
				.thenAwait(Duration.ofDays(1))
				.expectNext(1L)
				.verifyComplete();

		/*
		TestPublisher
		*/
		final TestPublisher<String> testPublisher = TestPublisher.create();
		testPublisher.next("a");
		testPublisher.next("b");
		testPublisher.complete();

		StepVerifier.create(testPublisher)
				.expectNext("a")
				.expectNext("b")
				.expectComplete();
	}

	/* 调试 */

	/**
	 * 由于反应式编程范式与传统编程范式的差异性，使用 Reactor 编写的代码在出现问题时比较难进行调试。
	 * 为了更好的帮助开发人员进行调试，Reactor 提供了相应的辅助功能。
	 *
	 * Note：这部分测试最好 3 部分分别运行，第一部分会抛异常，测试会 Failed。
	 */
	@Test
	void testDebug() {
		/*
		启用调试模式：
		在程序开始的地方添加：`Hooks.onOperatorDebug();`。
		之后，所有的操作符在执行时都会保存额外的与执行链相关的信息。当出现错误时，这些信息会被作为异常堆栈信息的一部分输出。
		通过这些信息可以分析出具体是在哪个操作符的执行中出现了问题。

		一般只有在出现了错误之后，再考虑启用调试模式，毕竟记录了这么多一堆额外信息看起来还是很烦的...
		*/
		Hooks.onOperatorDebug();  // 启用调试模式
		Flux.just(1, 2)
				.concatWith(Mono.error(new IllegalStateException()))
				.subscribe(System.out::println, System.err::println);
		Hooks.resetOnOperatorDebug();  // 关闭调试模式

		/*
		使用检查点：
		通过 checkpoint(String description) 操作符来对特定的流处理链来启用调试模式。
		代码清单 25 中，在 map 操作符之后添加了一个名为 test 的检查点。当出现错误时，检查点名称会出现在异常堆栈信息中。对于程序中重要或者复杂的流处理链，可以在关键的位置上启用检查点来帮助定位可能存在的问题。
		*/
		Flux.just(1, 0).map(x -> 1 / x).checkpoint("my test").subscribe(System.out::println);
		/*
		输出：
		Error has been observed at the following site(s):
			|_ checkpoint ⇢ my test
		Stack trace:
				at org.tangbean.reactorstudydemo.ReactorStudyDemoApplication.lambda$testDebug$20(ReactorStudyDemoApplication.java:407) [classes/:na]
				...
		*/

		/*
		日志记录
		*/
		Flux.range(1, 2).log("Range").subscribe(System.out::println);
		/*
		输出：
		2019-12-26 11:54:55.390  INFO 41397 --- [main] Range : | onSubscribe([Synchronous Fuseable] FluxRange.RangeSubscription)
		2019-12-26 11:54:55.393  INFO 41397 --- [main] Range : | request(unbounded)
		2019-12-26 11:54:55.393  INFO 41397 --- [main] Range : | onNext(1)
		1
		2019-12-26 11:54:55.393  INFO 41397 --- [main] Range : | onNext(2)
		2
		2019-12-26 11:54:55.393  INFO 41397 --- [main] Range : | onComplete()
		*/
	}

	/* “冷”与“热”序列 */

	/**
	 * 冷序列：无论订阅者在何时订阅该序列，总是能收到序列中产生的全部消息。
	 * 热序列：持续不断地产生消息，订阅者只能获取到在其订阅之后产生的消息。
	 */
	@Test
	void testColdAndWarmSequence() throws Exception {
		final Flux<Long> source = Flux.interval(Duration.of(1000, ChronoUnit.MILLIS))
				.take(10)
				.publish()  // publish() 方法把一个 Flux 对象转换成 ConnectableFlux 对象
				.autoConnect();  // autoConnect()的作用是当 ConnectableFlux 对象有一个订阅者时就开始产生消息
		source.subscribe(System.out::print);  // 订阅者 1 出现（Cold）
		Thread.sleep(5000);
		System.out.println("\n订阅者 2 出现...");
		source.toStream().forEach(System.out::print);  // 订阅者 2 出现（Warm）
		/*
		输出：
		01234
		订阅者 2 出现...
		5566778899
		*/
	}

}
