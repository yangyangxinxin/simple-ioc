package link.yangxin.yestar.controller;

import link.yangxin.yestar.annotation.AutoWired;
import link.yangxin.yestar.annotation.Controller;
import link.yangxin.yestar.annotation.Value;
import link.yangxin.yestar.service.IUserService;

/**
 * @author yangxin
 * @date 2019/4/16
 */
@Controller
public class IndexController {

    @AutoWired("testUserService")
    private IUserService iUserService;

    @Value("application.active.profile")
    private String profile;

    public void login() {
        iUserService.register();

        System.out.println("current profile is " + profile);
    }

}