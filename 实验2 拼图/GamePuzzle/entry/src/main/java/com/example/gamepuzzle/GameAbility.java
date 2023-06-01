package com.example.gamepuzzle;

import com.example.gamepuzzle.slice.GameAbilitySlice;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;

public class GameAbility extends Ability {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(GameAbilitySlice.class.getName());
    }
}
