package com.forumdeitroll.test.persistence;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.forumdeitroll.persistence.IPersistence;

public class TestAllTests {

	@Test
	public void testAllTest() {
		@SuppressWarnings("unchecked")
		List<Class<? extends BaseTest>> allTestClasses = Arrays.asList(AdminTest.class, AuthorTest.class,
				BookmarkTest.class, MessagesTest.class, MiscTest.class, PollTest.class, PrivateMsgTest.class,
				QuoteTest.class, ThreadsTest.class);

		Set<String> testMethods = new HashSet<String>();
		for (Class<?> testClass : allTestClasses) {
			for (Method m : testClass.getMethods()) {
				if (null != m.getAnnotation(Test.class)) {
					testMethods.add(m.getName());
				}
			}
		}

		for (Method m : IPersistence.class.getMethods()) {
			String methodName = m.getName();
			if ("init".equals(methodName)) {
				continue;
			}
			String testMethod = "test_" + methodName;
			Assert.assertTrue("Nessun metodo di test " + testMethod + " disponibile !",
					testMethods.contains(testMethod));
		}

	}

}
