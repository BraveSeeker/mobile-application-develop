package com.example.point24;

import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;
import java.util.Arrays;

public class ChooseCard extends Activity {
    private static final String TAG = "XLC";
    private ImageView[] imgViews;
    Button button;
    public static ArrayList<Integer> chooseCardId = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate2: ");
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_stack_choose_card);
        initView();
    }

    private void initView() {
        imgViews = new ImageView[54];
        for (int i = 1; i <= 52; i++) {
            imgViews[i] = findViewById(PlayGame.cardList52[i]);
        }
        button = findViewById(R.id.completeChoose);
        chooseCardId.clear();
    }

    public void chooseCards(View view) {
        Log.e(TAG, "chooseCards: choose card, id=" + view.getId() + ", getTop=" + view.getTop() + ", sourceId=" + view.getSourceLayoutResId());
        ImageView imageView = (ImageView) findViewById(view.getId());
        if (imageView.getTop() == 50) {// 如果未选中，那么点击选中
            if (chooseCardId.size() < 4) {
                imageView.setTop(0);
                chooseCardId.add(Integer.parseInt(imageView.getTag().toString()));
            }
        } else {
            for (int i = 0; i < chooseCardId.size(); i++) {
                if (chooseCardId.get(i) == Integer.parseInt(imageView.getTag().toString()))
                    chooseCardId.remove(i);
            }
            imageView.setTop(50);// 如果选中， 那么点击取消选中
        }
        Log.e(TAG, "chooseCards: chooseCardId=" + chooseCardId.toString());
    }

    public void submit(View view) {
        if (chooseCardId.size() == 4) {
            Intent intent = new Intent();
            intent.putExtra("card4index", chooseCardId);
            setResult(RESULT_OK, intent);//使用setResult()方法向上一个活动返回数据，第一个参数为处理结果，第二个参数为带有数据的Intent
            finish();
        } else if (chooseCardId.size() < 4) {
            new AlertDialog.Builder(this)
                    .setTitle("提示")//设置对话框标题
                    .setMessage("请选择4张卡片")
                    .setPositiveButton("重新选择", new DialogInterface.OnClickListener() {//添加确定按钮
                        @Override
                        public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件，点击事件没写，自己添加
                            dialog.cancel();
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {//添加返回按钮
                        @Override
                        public void onClick(DialogInterface dialog, int which) {//响应事件，点击事件没写，自己添加
                            finish();
                            dialog.cancel();
                        }
                    }).show();//在按键响应事件中显示此对话框
        }
    }
}
