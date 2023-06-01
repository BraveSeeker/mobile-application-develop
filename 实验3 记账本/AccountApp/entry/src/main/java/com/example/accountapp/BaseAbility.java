package com.example.accountapp;

import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;

/**
 * 界面基类
 */
public class BaseAbility extends Ability {
    MyApplication application;
    BaseAbility context;
    @Override
    protected void onStart(Intent intent) {
        super.onStart(intent);
        // 单例模式
        if (application == null) {
            application = (MyApplication) getAbilityPackage();
        }
        context = this;
        addAbility();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void addAbility() {
        application.addAbility(context);
    }

    public void removeAllActivity() {
        application.removeAllAbility();
    }
}