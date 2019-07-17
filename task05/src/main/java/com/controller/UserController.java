package com.controller;


import com.encryption.DESUtil;
import com.encryption.MD5Util;
import com.pojo.User;
import com.service.UserMapperService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserMapperService userMapperService;
    Logger logger = Logger.getLogger(UserController.class);

    @RequestMapping(value = "/a/u/register", method = RequestMethod.GET)
    public String registered() {
        return "register";
    }

    @RequestMapping(value = "/a/u/register", method = RequestMethod.POST)
    public String insert(User user, String name) {
        logger.info("新用户的注册信息为：" + user);
        User users = userMapperService.selectByName(name);
        logger.info("查看数据库是否有数据：" + users);
        if (users != null) {
            logger.info("用户已存在");
            return "redirect:/a/u/login";
        } else {
            if (
                    user.getName() != null
                            && user.getName().length() > 0
                            && user.getPassword() != null
                            && user.getPassword().length() > 0) {
                logger.info("注册用户的信息：" + user);
                int row = userMapperService.insert(user);
                logger.info("插入成功：" + row);
                return "login";
            } else {
                return "redirect:/register";
            }
        }
    }


    @RequestMapping(value = "/a/u/login", method = RequestMethod.GET)
    public String login(User user, String name, String password, HttpServletRequest request, HttpServletResponse response, DESUtil desUtil, ModelAndView modelAndView) throws UnsupportedEncodingException {
                //将获取到的用户名与密码放入到用户查询接口中进行查询
                User user1 = userMapperService.selectByCondition(name, password);
//                System.out.println("用户是：" + user1.getName());
                if (user1 != null) {
                    System.out.println("登录成功");
                    Long id = user1.getId();
                    String token = id + "/" + System.currentTimeMillis();

                    DESUtil desUtil1 = null;
                    try {
                        desUtil1 = new DESUtil();
                        token = desUtil1.encrypt(token);
                        Cookie cookie = new Cookie("token", token);
                        cookie.setMaxAge(60 * 3);

                        cookie.setPath("/");
                        response.addCookie(cookie);
                        request.setAttribute("user", user);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return "redirect:/student";
                } else {
                    System.out.println("登录失败");
                    return "login";
                }
            }




    @RequestMapping(value = "/loginout",method = RequestMethod.GET)
    public String loginOut(HttpServletRequest request,HttpServletResponse response,HttpSession session){
        //清除保存的session
        session.invalidate();
        System.out.println("session已经注销");
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie:cookies){
            if ("token".equals(cookie.getName())){
                cookie.setPath("/");
                cookie.setMaxAge(0);
                cookie.setValue(null);
                response.addCookie(cookie);
            }
        }
        System.out.println("退出登录");
        return "redirect:/student";
    }

}
