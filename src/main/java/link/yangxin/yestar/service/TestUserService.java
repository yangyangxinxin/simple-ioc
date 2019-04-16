package link.yangxin.yestar.service;

import link.yangxin.yestar.annotation.AutoWired;
import link.yangxin.yestar.annotation.Service;
import link.yangxin.yestar.dao.UserDao;

/**
 * @author yangxin
 * @date 2019/4/16
 */
@Service("testUserService")
public class TestUserService implements IUserService {

    @AutoWired
    private UserDao userDao;

    @Override
    public void register() {
        System.out.println("test service register success");
        userDao.login();
    }

    @Override
    public void say() {
        System.out.println("testUserService sayed");
    }
}