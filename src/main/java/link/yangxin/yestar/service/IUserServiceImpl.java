package link.yangxin.yestar.service;

import link.yangxin.yestar.annotation.AutoWired;
import link.yangxin.yestar.annotation.Service;
import link.yangxin.yestar.dao.UserDao;

/**
 * @author yangxin
 * @date 2019/4/16
 */
@Service("iUserServiceImpl")
public class IUserServiceImpl implements IUserService {

    @AutoWired
    private UserDao userDao;

    @Override
    public void register() {
        System.out.println("register success,now auto login");
        userDao.login();
    }

    @Override
    public void say() {
        System.out.println("iUserServiceImpl sayed");
    }
}