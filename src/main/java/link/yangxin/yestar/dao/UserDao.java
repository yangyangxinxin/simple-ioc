package link.yangxin.yestar.dao;

import link.yangxin.yestar.annotation.AutoWired;
import link.yangxin.yestar.annotation.Repository;
import link.yangxin.yestar.annotation.Value;
import link.yangxin.yestar.service.IUserService;

/**
 * @author yangxin
 * @date 2019/4/16
 */
@Repository("userDao")
public class UserDao {

    @Value("server.port")
    private String port;

    @AutoWired("iUserServiceImpl")
    private IUserService iUserService;

    @AutoWired
    private IUserService testUserService;

    public void login() {
        System.out.println("login success,current port:" + port);

        iUserService.say();
        testUserService.say();
    }

}