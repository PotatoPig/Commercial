package com.miaoshaproject.service;

import com.miaoshaproject.error.BusinessException;
import com.miaoshaproject.service.model.UserModel;

public interface UserService {
    // Get user object by user--id
    UserModel getUserById(Integer id);
    void register(UserModel userModel) throws BusinessException;

    // telphone--user register phone, password--encrpt password
    UserModel validateLogin(String telphone, String encrptPassword) throws BusinessException;
}
