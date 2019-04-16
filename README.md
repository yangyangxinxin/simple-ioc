## Simple IOC

> 模拟一个简单的SpringIOC容器。主要用到的技术是反射。

1. 初始化`AplicationContext`对象，该对象的创建需要传入配置文件的文件地址(`resources`文件夹下的`application.properties`文件).

2. 读取`application.properties`配置文件，并将配置放入到`Map`中，将他称其为`configContext`

3. 读取`configContext`配置中的`annotation.base.scan`，这个属性表示了需要扫描的包路径

4. 递归读取该路径的所有类，并将类名全路径(例如`link.yangxin.yestar.dao.UserDao.class`)放入到`classSet`集合中.

5. 遍历`classSet`集合，把集合中的元素进行类加载，获取到其`Class`对象，然后判断该对象是否有`Controller`、`Service`、`Repository`注解，如果有这些注解，那么实例化此对象，并将这个对象放入到`iocContext`中，`iocContext`的数据结构为`{beanName:beanInstance}`。

6. 遍历`iocContext`，并将其属性一一进行注入，`Value`注解也在此环节进行注入

7. 至此整个容器初始化完成，可以调用`ApplicationContext`的`getBean()`方法获取IOC容器中的bean，并完成业务操作。

