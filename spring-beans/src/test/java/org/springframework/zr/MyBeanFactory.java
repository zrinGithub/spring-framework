package org.springframework.zr;

import org.springframework.beans.factory.FactoryBean;

public class MyBeanFactory implements FactoryBean<MyBean> {
	@Override
	public Class<?> getObjectType() {
		return MyBean.class;
	}

	@Override
	public MyBean getObject() throws Exception {
		MyBean bean = new MyBean();
		bean.setTestStr("factoryBean");
		return bean;
	}
}
