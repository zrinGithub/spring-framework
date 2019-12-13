package zr;


import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.junit.Assert.assertEquals;


public class ApplicationContextTest {
	@Test
	public void test() {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("org/springframework/zr/BeanFactoryTest.xml");
		MyBean bean = applicationContext.getBean("alias", MyBean.class);
		assertEquals("factoryBean", bean.getTestStr());
	}
}
