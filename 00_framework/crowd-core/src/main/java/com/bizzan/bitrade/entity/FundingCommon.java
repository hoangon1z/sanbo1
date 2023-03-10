package com.bizzan.bitrade.entity;


import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class FundingCommon {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(columnDefinition = "int(11)  comment '用户id'")
    private Long memberId;

    @Column(columnDefinition = "varchar(255)  comment '用户名'")
    private String memberName;


    @Column(nullable = false, columnDefinition = "dateTime  comment '评论时间'")
    private Long commonTime;

    @Column
    private String common;

    @Column(columnDefinition = "int(2)  comment '删除'")
    private int deleted;

    @Column
    private Long fundingId;

    @Column
    private int fundingType;

    @Column(columnDefinition = "int(2) default 0  comment '审核状态' ")
    private int status; //0审核中 1已通过 2未通过

}
