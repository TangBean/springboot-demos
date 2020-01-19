package org.tangbean.proxypatterndemo;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cglib.core.DebuggingClassWriter;
import org.springframework.cglib.proxy.Enhancer;
import org.tangbean.proxypatterndemo.dao.CglibSubjectObj;
import org.tangbean.proxypatterndemo.dao.SubjectProxy;
import org.tangbean.proxypatterndemo.dao.RealSubject;
import org.tangbean.proxypatterndemo.dao.Subject;
import org.tangbean.proxypatterndemo.handler.RequestHandler;
import org.tangbean.proxypatterndemo.interceptor.UserDaoInterceptor;
import sun.misc.ProxyGenerator;
import sun.nio.ch.IOUtil;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Proxy;
import java.util.Arrays;


@SpringBootTest(classes = ProxyPatternDemoApplication.class)
class SubjectProxyPatternDemoApplicationTests {

	@Test
	void staticProxyDemo() {
		Subject subject = new RealSubject();
		Subject subjectProxy = new SubjectProxy(subject);

		subjectProxy.request();
		subjectProxy.request1();
		subjectProxy.request2();
		subjectProxy.request3();
		subjectProxy.request4();
		subjectProxy.request5();
		subjectProxy.request6();

		subjectProxy.anotherNameMethod();

		/* 输出：
		---- Before request ----
		---- Requesting... ----
		---- After request ----
		*/
	}

	private static void saveProxyFile(Class<?> subject , String... filePath) {
		if (filePath.length == 0) {
			System.getProperties().put("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");
		} else {
			FileOutputStream out = null;
			try {
				byte[] classFile = ProxyGenerator.generateProxyClass("$Proxy0", subject.getInterfaces());
				out = new FileOutputStream(filePath[0] + "$Proxy0.class");
				out.write(classFile);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (out != null) {
						out.flush();
						out.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}


	@Test
	void dynamicProxyDemo() {

		Subject realSubject = new RealSubject();
		RequestHandler handler = new RequestHandler(realSubject);

		Subject proxyObj = (Subject) Proxy.newProxyInstance(
				this.getClass().getClassLoader(),
				new Class[]{Subject.class},
				handler);

		saveProxyFile(proxyObj.getClass(), "/Users/tangzhiyao/IdeaProjects/springboot-demos/aop/proxy-pattern-demo/src/");

		proxyObj.request();
		proxyObj.request1();
		proxyObj.request2();
		proxyObj.request3();
		proxyObj.request4();
		proxyObj.request5();
		proxyObj.request6();

		proxyObj.anotherNameMethod();

		/* 输出
		---- Invoke time: Mon Dec 30 21:45:54 CST 2019 ----
		---- Requesting... ----
		---- After invoke ----
		*/
	}

	@Test
	void cglibProxyDemo() {
		String location = SubjectProxyPatternDemoApplicationTests.class.getResource("").getPath() + "debugging/";
		System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY, location);
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(CglibSubjectObj.class);
		enhancer.setCallback(new UserDaoInterceptor());
		CglibSubjectObj proxyObj = (CglibSubjectObj) enhancer.create();

		proxyObj.request();
		proxyObj.request1();
		proxyObj.request2();
		proxyObj.request3();
		proxyObj.request4();
		proxyObj.request5();
		proxyObj.request6();
	}

}
