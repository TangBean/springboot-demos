package org.tangbean.reactorstudydemo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
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
		testCombineLatest();
	}

	/* Flux & Mono */
	private void testFluxStatic() {
		Flux.just("Hello", "World").subscribe(System.out::println);

		Flux.fromArray(new Integer[]{1, 2, 3}).subscribe(System.out::println);

		Flux.empty().subscribe(System.out::println);

		Flux.range(1, 10).subscribe(System.out::println);

		// 创建一个包含了从 0 开始递增的 Long 对象的序列。其中包含的元素按照指定的间隔来发布。
		// 除了间隔时间之外，还可以指定起始元素发布之前的延迟时间。
		Flux.interval(Duration.of(10, ChronoUnit.SECONDS)).subscribe(System.out::println);
	}

	private void testFluxGenerate() {
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

	private void testFluxCreate() {
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

	private void testMonoSpecialMethod() {
		Mono.fromSupplier(() -> "Hello").subscribe(System.out::println);

		Mono.justOrEmpty(Optional.of("Hello")).subscribe(System.out::println);
		Mono.justOrEmpty(Optional.empty()).subscribe(System.out::println);  // No Output, because of the empty Optional

		Mono.create(sink -> sink.success("Hello")).subscribe(System.out::println);
	}

	/* 操作符 */

	/**
	 * 这两个操作符的作用是把当前流中的元素收集到集合中，并把集合对象作为流中的新元素。
	 */
	private void testBuffer() throws Exception {
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
	private void testFilter() {
		Flux.range(1, 10).filter(i -> i % 2 == 0).subscribe(System.out::println);
	}

	/**
	 * window 操作符的作用类似于 buffer，所不同的是 window 操作符是把当前流中的元素收集到另外的 Flux 序列中，因此返回值类型是 Flux<Flux>。
	 */
	private void testWindow() {
		// 会输出 5 个 UnicastProcessor，因为 window 操作符所产生的流中包含的是 UnicastProcessor 类的对象，
		// 而 UnicastProcessor 类的 toString 方法输出的就是 UnicastProcessor 字符。
		Flux.range(1, 100).window(20).subscribe(System.out::println);
	}

	/**
	 * zipWith 操作符把当前流中的元素与另外一个流中的元素按照一对一的方式进行合并。
	 */
	private void testZipWith() {
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
	private void testTake() throws Exception {
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
	private void testReduce() {
		Flux.range(1, 100).reduce(Integer::sum).subscribe(System.out::println);  // 5050

		Flux.range(1, 100).reduceWith(() -> 100, Integer::sum).subscribe(System.out::println);  // 5150
	}

	/**
	 * merge & mergeSequential
	 * 把多个流合并成一个 Flux 序列。
	 */
	private void testMerge() {
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
	private void testFlatMap() {
		// 输出：0011223344
		Flux.just(5, 10).flatMap(x -> Flux.interval(
				Duration.of(x * 10, ChronoUnit.MILLIS), Duration.of(100, ChronoUnit.MILLIS)).take(5))
				.toStream().forEach(System.out::print);
	}

	/**
	 * concatMap
	 * 把流中的每个元素转换成一个流，再把所有流进行合并。
	 */
	private void testConcatMap() {
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
	private void testCombineLatest() {
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
}
