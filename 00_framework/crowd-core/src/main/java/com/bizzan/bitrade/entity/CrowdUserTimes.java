package com.bizzan.bitrade.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class CrowdUserTimes {


    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column
    private Long memberId;


    @Column
    private Long levelId;

    @Column
    private Integer type;
    //0位众筹  1为线下公益


}
