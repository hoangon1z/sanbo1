package com.bizzan.bitrade.util;

import com.bizzan.bitrade.entity.Member;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MemberUtil {
    public static List<Member> buildTree(List<Member> members) {
        System.out.println(members);
        Map<Long, List<Member>> pidListMap =
                members.stream().map(member -> {
                    if (member.getInviterIdNature() == null) {
                        member.setInviterId(0l);
                    }
                    return member;
                }).collect(Collectors.groupingBy(Member::getInviterIdNature));
        members.stream().forEach(item -> item.setChildren(pidListMap.get(item.getId())));
        //返回结果也改为返回顶层节点的list
        return pidListMap.get(0l);
    }
}
