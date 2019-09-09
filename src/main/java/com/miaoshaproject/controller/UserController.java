package com.miaoshaproject.controller;

import com.miaoshaproject.controller.viewobject.UserVO;
import com.miaoshaproject.error.BusinessException;
import com.miaoshaproject.error.EmBussinessError;
import com.miaoshaproject.response.CommonReturnType;
import com.miaoshaproject.service.UserService;
import com.miaoshaproject.service.model.UserModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

@Controller("user")
@RequestMapping("/user")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    //User login interface
    @RequestMapping(value = "/login",method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType login(@RequestParam(name="telphone") String telphone,
                                  @RequestParam(name="password") String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {

        if (StringUtils.isEmpty(telphone) || StringUtils.isEmpty(password)) {
            throw new BusinessException(EmBussinessError.PARAMETER_VALIDATION_ERROR);
        }

        UserModel userModel = userService.validateLogin(telphone, this.EncoderByMd5(password));

        this.httpServletRequest.getSession().setAttribute("IS_LOGIN", true);
        this.httpServletRequest.getSession().setAttribute("LOGIN_USER", userModel);

        return CommonReturnType.create(null);
    }


    //User register interface
    @RequestMapping(value = "/register",method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType register(@RequestParam(name="telphone") String telphone,
                                     @RequestParam(name="otpCode") String otpCode,
                                     @RequestParam(name="name") String name,
                                     @RequestParam(name="gender") Byte gender,
                                     @RequestParam(name="age") Integer age,
                                     @RequestParam(name="password") String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {

        // check the otpCode and the phone
        String inSeesionOtpCode = (String) this.httpServletRequest.getSession().getAttribute(telphone);
        if (!com.alibaba.druid.util.StringUtils.equals(otpCode,inSeesionOtpCode)) {
            throw new BusinessException(EmBussinessError.PARAMETER_VALIDATION_ERROR,"短信验证码不符合");
        }

        // User register
        UserModel userModel = new UserModel();
        userModel.setName(name);
        userModel.setGender(gender);
        userModel.setAge(age);
        userModel.setTelphone(telphone);
        userModel.setRegisterMode("byphone");
        userModel.setEncrptPassword(this.EncoderByMd5(password));

        userService.register(userModel);
        return CommonReturnType.create(null);
    }

    public String EncoderByMd5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        BASE64Encoder base64en = new BASE64Encoder();

        String newstr = base64en.encode(md5.digest(str.getBytes("utf-8")));
        return newstr;
    }

    //User get his own otp message
    @RequestMapping(value = "/getotp",method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType getOtp(@RequestParam(name="telphone") String telphone) {
        //需要按照一定的规则生成OTP验证码
        Random random = new Random();
        int randomInt = random.nextInt(99999);
        randomInt += 10000;
        String otpCode = String.valueOf(randomInt);

        //将OTP验证码同时对应用户的手机号关联
        httpServletRequest.getSession().setAttribute(telphone, otpCode);

        //将OTP验证码通过短信通道发给用户
        System.out.println("telphone = " + telphone + "& otpCode = " + otpCode);

        return CommonReturnType.create(null);
    }


    @RequestMapping("/get")
    @ResponseBody
    public CommonReturnType getUser(@RequestParam(name="id") Integer id) throws BusinessException {
        // Call service to acquire id--user object and return it to the front-end
        UserModel userModel = userService.getUserById(id);

        //Change the core field model user object into UI view object
        UserVO userVO = convertFromModel(userModel);

        // If user info not exist
        if (userModel == null) {
            throw new BusinessException(EmBussinessError.USER_NOT_EXIST);
        }

        // Return general object
        return CommonReturnType.create(userVO);
    }

    private UserVO convertFromModel(UserModel userModel) {
        if (userModel == null) {
            return null;
        } else {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(userModel, userVO);
            return userVO;
        }
    }
}
