package com.bizzan.bitrade.entity;


import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class CrowdPic {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column
    private String picUrl;

    @Column
    private Integer type;//众筹或者公益类别 0众筹 1公益


    @Column
    private Long fundingId;


}
