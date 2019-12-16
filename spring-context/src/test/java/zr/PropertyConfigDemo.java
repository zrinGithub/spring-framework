package zr;

import org.junit.Test;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

public class PropertyConfigDemo {
	@Test
	public void testBeanPostProcessor(){
		ConfigurableListableBeanFactory bf=new XmlBeanFactory(new ClassPathResource("zr/BeanPoseProcessor.xml"));


		BeanFactoryPostProcessor bfpp=(BeanFactoryPostProcessor)bf.getBean("bfpp");
		bfpp.postProcessBeanFactory(bf);
		System.out.println(bf.getBean("simpleBean"));
	}
}
