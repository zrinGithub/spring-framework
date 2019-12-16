package zr;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionVisitor;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.util.StringValueResolver;

import java.util.HashSet;
import java.util.Set;

public class ObscenityRemovingBeanFactoryPostProcessor implements
		BeanFactoryPostProcessor {
	private Set<String> obscenties;
	public ObscenityRemovingBeanFactoryPostProcessor(){
		this.obscenties=new HashSet<String>();
	}

	public void postProcessBeanFactory(
			ConfigurableListableBeanFactory beanFactory) throws BeansException {
		String[] beanNames=beanFactory.getBeanDefinitionNames();
		for(String beanName:beanNames){
			BeanDefinition bd=beanFactory.getBeanDefinition(beanName);
			StringValueResolver valueResover=new StringValueResolver() {
				public String resolveStringValue(String strVal) {
					if(isObscene(strVal)) return "*****";
					return strVal;
				}
			};
			BeanDefinitionVisitor visitor=new BeanDefinitionVisitor(valueResover);
			visitor.visitBeanDefinition(bd);
		}
	}

	public boolean isObscene(Object value){
		String potentialObscenity=value.toString().toUpperCase();
		return this.obscenties.contains(potentialObscenity);
	}

	public void setObscenties(Set<String> obscenties) {
		this.obscenties.clear();
		for(String obscenity:obscenties){
			this.obscenties.add(obscenity.toUpperCase());
		}
	}

}