# 搭建环境：

Spring源码深度解析（第2版）：https://www.epubit.com/bookDetails?id=N39040
源码地址：https://github.com/spring-projects/spring-framework/tree/5.0.x



## gradle



gradle下载配置：

```
GRADLE_HOME	：D:\env\gradle\gradle-4.10

path添加%GRADLE_HOME%\bin

gradle -v测试
```



编译项目：

默认gradle包下载位置：C:\Users\UserName\\.gradle\caches\modules-2\files-2.1



## 代码

### 识别不了compileJava

spring-beans/spring-beans.gradle中如果编译报识别不了，可以注释报错位置的：

```
//compileGroovy.dependsOn = compileGroovy.taskDependencies.values - "compileJava"
```



### 缺少spring core的包

编译会发现缺少包，需要把spring-cglib-repack-3.2.0.jar和spring-objenesis-repack-2.1.jar放在

spring-core/libs(新建的路径)/下

并在spring-core/spring-core.gradle中添加

```
	......
	testCompile("javax.xml.bind:jaxb-api:2.3.0")
	testCompile("com.fasterxml.woodstox:woodstox-core:5.0.3") {
		exclude group: "stax", module: "stax-api"
	}
	//添加jar在gradle中生效
	compile fileTree(dir: 'libs',include : '*.jar')
}
```



### 找不到AspectJ相关的类

类似这样的类：

```java
public aspect AnnotationAsyncExecutionAspect extends AbstractAsyncExecutionAspect {
	......
}
```



需要下载安装AspectJ(需要jdk环境):

下载地址：https://www.eclipse.org/aspectj/downloads.php

得到

执行该jar包：

```
java -jar aso
```



进入安装程序，输入jre环境，如：

```
C:\Program Files\Java\jre1.8.0_221
```



输入安装环境：

```
D:\env\aspectj1.9
```



也可以吧路径添加到path中，说明如下：

>The automatic installation process is complete. We recommend you            complete the installation as follows:         
>
>1. ​             Add **D:\env\aspectj1.9\lib\aspectjrt.jar** to              your CLASSPATH. This small .jar file contains classes required by              any program compiled with the ajc compiler.           
>2. ​             Modify your PATH to include `**D:\env\aspectj1.9\bin**`.              This will make it easier to run ajc and ajbrowser.           
>
>​           These steps are described in more detail in `**D:\env\aspectj1.9\README-AspectJ.html**`.         



使用idea对Ajc的支持：

- 进入project structure
- add AspectJ  -> 分别选择 spring-aop_main,spring-aspects_main，并删除对应的

Kotlin Facets

- 修改编译器：

  - Settings-> java Compiler ->Use compiler ->Ajc
  - Path to Ajc compiler:选择到之前安装的路径到aspectjtools.jar：D:\env\aspectj1.9\lib\aspectjtools.jar
  - 选中Delegate to javac

  如图：

  ![](.\spring源码分析\idea配置Ajc.png)



其他的编译错误的测试类直接删除。



至此，spring 的源码环境就搭建完成。





# 容器基本实现

## 核心类

**DefaultListableBeanFactory**：



```java
public class DefaultListableBeanFactory extends AbstractAutowireCapableBeanFactory
		implements ConfigurableListableBeanFactory, BeanDefinitionRegistry, Serializable {
```

![](.\spring源码分析\DefaultListableBeanFactory.png)



**XmlBeanDefinitionReader**:



处理：

1．通过继承自AbstractBeanDefinitionReader中的方法，来使用ResourLoader将资源文件路径转换为对应的Resource文件。

2．通过DocumentLoader对Resource文件进行转换，将Resource文件转换为Document文件。

3．通过实现接口BeanDefinitionDocumentReader的DefaultBeanDefinitionDocumentReader类对Document进行解析，并使用BeanDefinitionParserDelegate对Element进行解析。





## 配置封装



**Resource**：定义了file、URL、classpath三种资源

```java
public interface Resource extends InputStreamSource {
方法：
	exists
	isReadable
	isOpen
	isFile
	getURL
	getURI
	getFile
	readableChannel
	contentLength
	lastModified
	createRelative
	getFilename
	getDescription
}
```





不同的resource对应有FileSystemResource、ClassPathResource等



其中ClassPathResource的getInputStream()直接使用getResourceAsStream实现。



**XmlBeanFactory**：

```java
BeanFactory    bf = new XmlBeanFactory(new ClassPathResource("beanFactoryTest.xml"));
```



实现：

```java
public XmlBeanFactory(Resource resource, BeanFactory parentBeanFactory) throws BeansException {
		super(parentBeanFactory);//1
		this.reader.loadBeanDefinitions(resource);//2
	}
```

1.在AbstractAutowireCapableBeanFactory中支付那个不初始化某些接口

```java
	public AbstractAutowireCapableBeanFactory() {
		super();
		ignoreDependencyInterface(BeanNameAware.class);
		ignoreDependencyInterface(BeanFactoryAware.class);
		ignoreDependencyInterface(BeanClassLoaderAware.class);
	}
```



2.使用XmlBeanDefinitionReader.loadBeanDefinitions进行xml文件中的资源加载

主要做了这几件事：

- 使用EncodedResource包装Resource（考虑编码） ->encodedResource
- resourcesCurrentlyBeingLoaded记录加载的资源   
- 使用InputSource包装输入流           ->inputSource
- 核心部分：

```java
return doLoadBeanDefinitions(inputSource, encodedResource.getResource());
```





doLoadBeanDefinitions主要两件事情：

```java
Document doc = doLoadDocument(inputSource, resource);//验证xml文件，获取document
return registerBeanDefinitions(doc, resource);//根据document注册bean信息
```



registerBeanDefinitions

```java
public int registerBeanDefinitions(Document doc, Resource resource) throws BeanDefinitionStoreException {
    //DefaultBeanDefinitionDocumentReader
	BeanDefinitionDocumentReader documentReader = createBeanDefinitionDocumentReader();
	int countBefore = getRegistry().getBeanDefinitionCount();
	documentReader.registerBeanDefinitions(doc, createReaderContext(resource));
	return getRegistry().getBeanDefinitionCount() - countBefore;
}
```



DefaultBeanDefinitionDocumentReader.**registerBeanDefinitions**

```java
@Override
public void registerBeanDefinitions(Document doc, XmlReaderContext readerContext) {
	this.readerContext = readerContext;
	logger.debug("Loading bean definitions");
	Element root = doc.getDocumentElement();
	doRegisterBeanDefinitions(root);
}
```



DefaultBeanDefinitionDocumentReader.**doRegisterBeanDefinitions**

1.解析profile，查看环境变量中是否包含

2.preProcessXml			子类预处理

**3.parseBeanDefinitions	关键步骤**

4.postProcessXml			子类后处理



DefaultBeanDefinitionDocumentReader.**parseBeanDefinitions**	

处理默认标签的解析：parseDefaultElement

处理自定义标签的解析：parseCustomElement



# 默认标签解析

DefaultBeanDefinitionDocumentReader.**parseDefaultElement**

根据不同的标签名称分别处理：import、alias、bean、beans

```java
private void parseDefaultElement(Element ele, BeanDefinitionParserDelegate delegate) {
	if (delegate.nodeNameEquals(ele, IMPORT_ELEMENT)) {
		importBeanDefinitionResource(ele);
	}
	else if (delegate.nodeNameEquals(ele, ALIAS_ELEMENT)) {
		processAliasRegistration(ele);
	}
	else if (delegate.nodeNameEquals(ele, BEAN_ELEMENT)) {
		processBeanDefinition(ele, delegate);
	}
	else if (delegate.nodeNameEquals(ele, NESTED_BEANS_ELEMENT)) {
		// recurse
		doRegisterBeanDefinitions(ele);
	}
}
```





## bean标签

DefaultBeanDefinitionDocumentReader.**processBeanDefinition**

```java
protected void processBeanDefinition(Element ele, BeanDefinitionParserDelegate delegate) {
	//1．首先委托BeanDefinitionDelegate类的parseBeanDefinitionElement方法进行元素解析，
	// 返回BeanDefinitionHolder类型的实例bdHolder，经过这个方法后，bdHolder实例已经包含我们配置文件中配置的各种属性了，
	// 例如class、name、id、alias之类的属性。
	BeanDefinitionHolder bdHolder = delegate.parseBeanDefinitionElement(ele);
	if (bdHolder != null) {
		//2．当返回的bdHolder不为空的情况下若存在默认标签的子节点下再有自定义属性，还需要再次对自定义标签进行解析。
		bdHolder = delegate.decorateBeanDefinitionIfRequired(ele, bdHolder);
		try {
			// Register the final decorated instance.
			//3．解析完成后，需要对解析后的bdHolder进行注册，同样，注册操作委托给了BeanDefinitionReaderUtils的registerBeanDefinition方法。
			BeanDefinitionReaderUtils.registerBeanDefinition(bdHolder, getReaderContext().getRegistry());
		}
		catch (BeanDefinitionStoreException ex) {
			getReaderContext().error("Failed to register bean definition with name '" +
					bdHolder.getBeanName() + "'", ele, ex);
		}
		// Send registration event.
		//4．最后发出响应事件，通知相关的监听器，这个bean已经加载完成了。
		getReaderContext().fireComponentRegistered(new BeanComponentDefinition(bdHolder));
	}
}
```



1.包装BeanDefinitionHolder

BeanDefinitionParserDelegate.**parseBeanDefinitionElement**()

取出id、name

使用GenericBeanDefinition解析其他属性统一封装



## 内部属性说明：



**singletonFactories**

用于存储在spring内部所使用的beanName->对象工厂的引用，一旦最终对象被创建(通过objectFactory.getObject())，此引用信息将删除



**earlySingletonObjects**

用于存储在创建Bean早期对创建的原始bean的一个引用，注意这里是原始bean，即使用工厂方法或构造方法创建出来的对象，一旦对象最终创建好，此引用信息将删除





# AOP

