package link.yangxin.yestar;

import link.yangxin.yestar.controller.IndexController;

import java.io.IOException;

/**
 * @author yangxin
 * @date 2019/4/16
 */
public class Application {

    public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        ApplicationContext applicationContext = new ApplicationContext("application.properties");
        Object indexController = applicationContext.getBean("indexController");
        System.out.println(indexController);
        IndexController controller = (IndexController) indexController;
        controller.login();
    }

}