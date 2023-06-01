package com.example.accountapp;

import ohos.aafwk.ability.AbilityPackage;


import ohos.aafwk.ability.Ability;
import ohos.aafwk.ability.AbilityPackage;

import java.util.ArrayList;
import java.util.List;

/**
 * 主应用程序类，一个应用只有一个应用程序类
 */
public class MyApplication extends AbilityPackage {
    private static List<Ability> abilities;
    @Override
    public void onInitialize() {
        super.onInitialize();
        abilities=new ArrayList<>();
    }

    /**
     * 将开启的界面添加至界面集合
     * @param ability
     */
    public void addAbility(Ability ability) {
        if (!abilities.contains(ability)) {
            abilities.add(ability);
        }
    }

    /**
     * 销毁所有的界面对象
     */
    public  void removeAllAbility() {
        for (Ability activity : abilities) {
            activity.terminateAbility();
        }
    }
}