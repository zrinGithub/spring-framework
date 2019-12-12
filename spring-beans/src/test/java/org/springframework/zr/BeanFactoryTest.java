package org.springframework.zr;


import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

import static org.junit.Assert.assertEquals;

public class BeanFactoryTest {
	@Test
	public void test() {
		BeanFactory beanFactory = new XmlBeanFactory(new ClassPathResource("org/springframework/zr/BeanFactoryTest.xml"));
		MyBean bean = beanFactory.getBean("aliasName", MyBean.class);
		MyBean2 bean2 = beanFactory.getBean("bean2", MyBean2.class);
		assertEquals("default Str", bean.getTestStr());
	}
}
