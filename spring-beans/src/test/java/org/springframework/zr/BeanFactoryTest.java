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
		//在doCreateBean方法里面会对是否允许循环依赖进行判断，这里会报错并提示可能出现了循环依赖的情况
		((XmlBeanFactory) beanFactory).setAllowCircularReferences(false);
		MyBean bean = beanFactory.getBean("aliasName", MyBean.class);
		MyBean2 bean2 = beanFactory.getBean("bean2", MyBean2.class);
		MyBean fb = beanFactory.getBean("fb", MyBean.class);
		assertEquals("factoryBean", fb.getTestStr());
	}
}
