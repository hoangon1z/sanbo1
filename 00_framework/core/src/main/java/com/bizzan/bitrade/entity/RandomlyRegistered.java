package com.bizzan.bitrade.entity;


import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table
public class RandomlyRegistered {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column
    private String telNum;


//    @JoinColumn(name = "member_level_id")
////    @ManyToOne
////    private MemberLevel memberLevelId;


    @Column
    private String memberLevelName;


}
