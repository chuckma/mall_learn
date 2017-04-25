package com.mall.common;

/**
 * Created by Andy on 2017/4/23.
 */
public class Const {

    public static final String CURRENT_USER = "current_User";

    public static final String EMAIL = "email";

    public static final String USERNAME = "username";

    public interface Role{
        int ROLE_CUSTOMER = 0; // 普通用户
        int ROLE_ADMIN = 1; // 管理员
    }
}
