package com.bizzan.bitrade.service;

import com.bizzan.bitrade.dao.RandomlyRegisteredDao;
import com.bizzan.bitrade.entity.Member;
import com.bizzan.bitrade.entity.MemberLevel;
import com.bizzan.bitrade.entity.RandomlyRegistered;
import com.bizzan.bitrade.service.Base.BaseService;
import com.bizzan.bitrade.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


@Service
public class RandomlyRegisteredService extends BaseService {

    @Autowired
    private RandomlyRegisteredDao randomlyRegisteredDao;


    /**
     * 随机选取数据库中10个数据(用于注册界面滚动数据)
     *
     * @return List<RandomlyRegistered>
     */
    public List<RandomlyRegistered> getRandomly() {
        List<RandomlyRegistered> newRegistereds = new ArrayList<>();
        List<RandomlyRegistered> registereds = randomlyRegisteredDao.findAll();
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            int n = random.nextInt(registereds.size());
            newRegistereds.add(registereds.get(n));
        }
        return newRegistereds;
    }


    //新用户注册时加入数据库
    public RandomlyRegistered addNewRegistered(String levelName, String telNum) {
        RandomlyRegistered randomlyRegistered = new RandomlyRegistered();
        String tel = StringUtil.changeTelnumBeRequest("+86" + " " + telNum);
        randomlyRegistered.setTelNum(tel);
        randomlyRegistered.setMemberLevelName(levelName);
        randomlyRegisteredDao.save(randomlyRegistered);
        return randomlyRegistered;
    }


}
