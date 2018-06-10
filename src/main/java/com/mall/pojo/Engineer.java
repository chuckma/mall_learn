package com.mall.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Engineer {
    private Integer id;

    private String name;

    private String password;

    private String sex;

    private String description;

    private String picUrl;

    private String company;

    private Date joinDate;

    private String remark;
}