package com.example.point24;

import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;


public class PlayGame extends AppCompatActivity {
    private static final String TAG = "XLC";
    ImageView card1, card2, card3, card4;
    Button card5, card6, card7;
    TextView usrAnsLine1, usrAnsLine2, usrAnsLine3, seeNum, seeOpt;
    Button addBtn, subBtn, mulBtn, divBtn, randomStartBtn, selfInitialBtn, showAnswerBtn, restartBtn;
    CheckBox isDeleteNoneSolution;
    public static int[] cardList52 = new int[]{0,
            R.mipmap.heart_01, R.mipmap.heart_02, R.mipmap.heart_03, R.mipmap.heart_04, R.mipmap.heart_05, R.mipmap.heart_06, R.mipmap.heart_07, R.mipmap.heart_08, R.mipmap.heart_09, R.mipmap.heart_10, R.mipmap.heart_11, R.mipmap.heart_12, R.mipmap.heart_13,
            R.mipmap.spade_01, R.mipmap.spade_02, R.mipmap.spade_03, R.mipmap.spade_04, R.mipmap.spade_05, R.mipmap.spade_06, R.mipmap.spade_07, R.mipmap.spade_08, R.mipmap.spade_09, R.mipmap.spade_10, R.mipmap.spade_11, R.mipmap.spade_12, R.mipmap.spade_13,
            R.mipmap.club_01, R.mipmap.club_02, R.mipmap.club_03, R.mipmap.club_04, R.mipmap.club_05, R.mipmap.club_06, R.mipmap.club_07, R.mipmap.club_08, R.mipmap.club_09, R.mipmap.club_10, R.mipmap.club_11, R.mipmap.club_12, R.mipmap.club_13,
            R.mipmap.diamond_01, R.mipmap.diamond_02, R.mipmap.diamond_03, R.mipmap.diamond_04, R.mipmap.diamond_05, R.mipmap.diamond_06, R.mipmap.diamond_07, R.mipmap.diamond_08, R.mipmap.diamond_09, R.mipmap.diamond_10, R.mipmap.diamond_11, R.mipmap.diamond_12, R.mipmap.diamond_13,
    };
    int[] idListFrom52;
    int[] idListOf7;
    ArrayList<Integer> cardIndex = new ArrayList<>(4);
    int current_selected_card_id = 0;
    int current_opt_id = 0;
    int current_selected_card_value = 0;
    int last_value = 0;
    LinearLayout tankTotal;
    int num1 = 0, num2 = 0, num3 = 0;
    String totalAns = "该组合无解";
    boolean is_init = false;
    HashMap<Integer, MyCard> myCardsMap = new HashMap<>();


    private static class MyCard {
        public MyCard(int imgSourceId, int value) {
            this.imgSourceId = imgSourceId;
            this.value = value;
            this.clicked = false;
        }

        boolean clicked;
        int value;
        int imgSourceId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 显示主页面


        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initView();
        randomStart();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    assert data != null;
                    cardIndex = data.getIntegerArrayListExtra("card4index");

                    reInit();
                    restart();
                    setCard4();
                    initCard();
                    is_init = true;
                    hasAns();
                }
            default:
        }
        Log.e(TAG, "onActivityResult: cardIndex=" + cardIndex.toString());
    }

    private void initView() {

        // 初始化按钮
        addBtn = findViewById(R.id.bt_add);
        subBtn = findViewById(R.id.bt_sub);
        mulBtn = findViewById(R.id.bt_mul);
        divBtn = findViewById(R.id.bt_div);
        // 初始化卡片
        card1 = findViewById(R.id.card1);
        card2 = findViewById(R.id.card2);
        card3 = findViewById(R.id.card3);
        card4 = findViewById(R.id.card4);
        card5 = findViewById(R.id.bt_num1);
        card6 = findViewById(R.id.bt_num2);
        card7 = findViewById(R.id.bt_num3);
        // 将卡片放入键值对
        myCardsMap.put(R.id.card1, new MyCard(R.id.card1, 0));
        myCardsMap.put(R.id.card2, new MyCard(R.id.card2, 0));
        myCardsMap.put(R.id.card3, new MyCard(R.id.card3, 0));
        myCardsMap.put(R.id.card4, new MyCard(R.id.card4, 0));
        myCardsMap.put(R.id.bt_num1, new MyCard(R.id.bt_num1, 0));
        myCardsMap.put(R.id.bt_num2, new MyCard(R.id.bt_num2, 0));
        myCardsMap.put(R.id.bt_num3, new MyCard(R.id.bt_num3, 0));
        // 初始化显示答案的3行
        usrAnsLine1 = findViewById(R.id.textViewStep1);
        usrAnsLine2 = findViewById(R.id.textViewStep2);
        usrAnsLine3 = findViewById(R.id.textViewStep3);
        seeNum = findViewById(R.id.seeNum);
        seeOpt = findViewById(R.id.seeOpt);
        // 初始化选项按钮
        randomStartBtn = findViewById(R.id.btn_randomStart);
        selfInitialBtn = findViewById(R.id.btn_selfInitial);
        showAnswerBtn = findViewById(R.id.btn_showAnswer);
        restartBtn = findViewById(R.id.btn_restart);
        // 是否选中去除无解
        isDeleteNoneSolution = findViewById(R.id.box_isDeleteNoneSolution);
        tankTotal = (LinearLayout) findViewById(R.id.tankTotal);

        idListFrom52 = new int[4];
        idListOf7 = new int[]{R.id.card1, R.id.card2, R.id.card3, R.id.card4, R.id.bt_num1, R.id.bt_num2, R.id.bt_num3};
        Log.e(TAG, "initView: cardList52=" + Arrays.toString(cardList52));
        Log.e(TAG, "\ninitView: " + "\nmyCardsMap:" + myCardsMap.keySet().toString());
    }


    // 点击运算符
    // 如果点击运算符之前，没有点击卡片，那么点击无效
    // 如果点击运算符之前，选中了卡片，那么将该运算符设为全局，并且再点击一张图片的时候，进行计算，并把计算结果显示在下面
    public void optClick(View view) {
        if (!is_init) return;
        if (current_selected_card_id == 0) return;
        String optIcon;
        if (view.getId() == R.id.bt_add) optIcon = "+";
        else if (view.getId() == R.id.bt_sub) optIcon = "-";
        else if (view.getId() == R.id.bt_mul) optIcon = "×";
        else optIcon = "÷";
        seeOpt.setText(optIcon);
        current_opt_id = view.getId();
        Log.e(TAG, "optClick: current_selected_card_id=" + current_selected_card_id + ", current_opt_id=" + current_opt_id);
    }

    // 点击卡片
    // 如果点击了卡片，没有点击运算符，那么将该卡片设为全局，等待计算，设置等待计算状态为true。如 2
    // 如果点击了卡片，也点击了运算符，那么进行运算，并把计算结果放在结果1位置。设置卡片不可点击，显示边框。 如之前点击了 2*，再点击4进行运算，结果为8，将8存入num1
    // 如果在击了卡片，进行连续运算，如点击了 2*4*， 再点击8， 那么进行连续运算，结果存在num1， 8设为不可点击，显示边框。
    // 如果点击了卡片，但是不是连续运算， 比如点击了2*4， 后来点击了 6*8， 将 6*8 的结果放在num2中

    public void cardClick(View view, int value) {
        if (!is_init) return;
        setClickedCard(view, value);
    }

    // 根据id设置当前卡片
    private void setCurCard(int id) {
        current_selected_card_id = id;
        current_selected_card_value = Objects.requireNonNull(myCardsMap.get(current_selected_card_id)).value;
        Log.e(TAG, "setCurCard: current_selected_card_id=" + current_selected_card_id + ", current_selected_card_value=" + current_selected_card_value);
        seeNum.setText("" + current_selected_card_value);
    }

    private void setClickedCard(View view, int value) {
        Log.e(TAG, "\nsetClickedCard: 1. view.id=" + view.getId());
        Log.e(TAG, "\nsetClickedCard: 2. current_selected_card_id=" + current_selected_card_id + ", current_opt_id=" + current_opt_id);
        if (!is_init) return; // 未初始化，直接返回
        if (current_selected_card_id == 0) {// 没选择卡片，则选中卡片
            setCurCard(view.getId());
        }
        if (current_selected_card_id != view.getId()) {// 1. 如果点击的卡片和上次的卡片不同
            if (current_opt_id != 0) {// 2. 并且点击了运算符
                // 3. 计算 card1+card1;card1+num1;num1+num2;
                int ans = calculate(getCardValue(current_selected_card_id), value);
                if (ans != -100) {// 3.1 正常结果
                    lockCard(current_selected_card_id);
                    lockCard(view.getId());
                    storeAns(ans);
                } else {// 3.2 除0错误
                    unLockCard(current_selected_card_id);
                    unLockCard(view.getId());
                }
            } else {// 2. 没有点击运算符
                setCurCard(view.getId());
            }
        } else {// 1. 如果点击的卡片和上次的卡片相同或者没点击卡片
            current_selected_card_id = view.getId();
            current_selected_card_value = Objects.requireNonNull(myCardsMap.get(current_selected_card_id)).value;
        }
        Log.e(TAG, "\nsetClickedCard: 3. current_selected_card_id=" + current_selected_card_id + ", current_opt_id=" + current_opt_id);

    }
    // 将答案显示在下面三张卡片上
    private void storeAns(int ans) {
        if (card5.getText() == "") {
            num1 = ans;
            Objects.requireNonNull(myCardsMap.get(card5.getId())).value = ans;
            card5.setText("" + ans);
            card5.setClickable(true);
            current_selected_card_id = card5.getId();
            current_selected_card_value = ans;
            seeNum.setText("" + ans);
        } else if (card6.getText() == "") {
            num2 = ans;
            card6.setClickable(true);
            Objects.requireNonNull(myCardsMap.get(card6.getId())).value = ans;
            card6.setText("" + ans);
            current_selected_card_id = card6.getId();
            current_selected_card_value = ans;
            seeNum.setText("" + ans);
        } else {
            num3 = ans;
            card7.setClickable(true);
            Objects.requireNonNull(myCardsMap.get(card7.getId())).value = ans;
            card7.setText("" + ans);
            current_selected_card_id = card7.getId();
            current_selected_card_value = ans;
            seeNum.setText("" + ans);
            String title = ans == 24 ? "组合成功" : "组合失败";
            String msg = ans == 24 ? "恭喜您计算24点成功！" : "未计算出24点，是否重新挑战？";
            new AlertDialog.Builder(PlayGame.this)
                    .setTitle(title)//设置对话框标题
                    .setMessage(msg)
                    .setPositiveButton("下一题", new DialogInterface.OnClickListener() {//添加确定按钮
                        @Override
                        public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件，点击事件没写，自己添加
                            randomStart();
                            dialog.cancel();
                        }
                    }).setNegativeButton("重新开始", new DialogInterface.OnClickListener() {//添加返回按钮
                        @Override
                        public void onClick(DialogInterface dialog, int which) {//响应事件，点击事件没写，自己添加
                            restart();
                            dialog.cancel();
                        }
                    }).setNeutralButton("返回", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    }).show();//在按键响应事件中显示此对话框


        }
    }
//    获取两张卡片的运算结果
    private int calculate(int value1, int value2) {
        Log.e(TAG, "\ncalculate: value1=" + value1 + ", value2=" + value2);
        Log.e(TAG, "\ncalculate: current_selected_card_id=" + current_selected_card_id + ", current_opt_id=" + current_opt_id);
        int ans = 0;
        String step = "";
        switch (current_opt_id) {
            case R.id.bt_add:
                ans = value1 + value2;
                step = "" + value1 + " + " + value2 + " = " + ans;
                break;
            case R.id.bt_sub:
                ans = value1 - value2;
                step = "" + value1 + " - " + value2 + " = " + ans;

                break;
            case R.id.bt_mul:
                ans = value1 * value2;
                step = "" + value1 + " × " + value2 + " = " + ans;

                break;
            case R.id.bt_div:
                if (value2 == 0) {// 除零错误
                    new AlertDialog.Builder(PlayGame.this)
                            .setTitle("非法运算")//设置对话框标题
                            .setMessage("除数为0: "+value1+"÷"+value2+"!")
                            .setPositiveButton("返回", new DialogInterface.OnClickListener() {//添加确定按钮
                                @Override
                                public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件，点击事件没写，自己添加
                                    dialog.cancel();
                                }
                            }).show();//在按键响应事件中显示此对话框
                    current_opt_id = 0;
                    seeOpt.setText("");
                    return -100;
                }else if(value1 / value2 * value2 != value1){// 无法整除错误
                    new AlertDialog.Builder(PlayGame.this)
                            .setTitle("非法运算")//设置对话框标题
                            .setMessage("无法整除: "+value1+"÷"+value2+"结果为小数!")
                            .setPositiveButton("返回", new DialogInterface.OnClickListener() {//添加确定按钮
                                @Override
                                public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件，点击事件没写，自己添加
                                    dialog.cancel();
                                }
                            }).show();//在按键响应事件中显示此对话框
                    current_opt_id = 0;
                    seeOpt.setText("");
                    return -100;
                }
                else
                    ans = value1 / value2;
                step = "" + value1 + " ÷ " + value2 + " = " + ans;
                break;
        }
        showStep(step);
        current_opt_id = 0;
        seeOpt.setText("");
        return ans;
    }
    // 显示运算过程
    private void showStep(String s) {
        if (usrAnsLine1.getText() == "") {
            usrAnsLine1.setText(s);
        } else if (usrAnsLine2.getText() == "") {
            usrAnsLine2.setText(s);
        } else {
            usrAnsLine3.setText(s);
        }
    }
    // 卡片锁定
    private void lockCard(int cardId) {
        findViewById(cardId).setClickable(false);

    }
    // 卡片解锁
    private void unLockCard(int cardId) {
        findViewById(cardId).setClickable(true);
    }
    // 获取卡片值
    private int getCardValue(int id) {
        Log.e(TAG, "\ngetCardValue: id=" + id + ", value=" + Objects.requireNonNull(myCardsMap.get(id)).value);
        return Objects.requireNonNull(myCardsMap.get(id)).value;
    }

    // 随机开始
    // card1, card2, card3, card4随机分配一张图片
    // 其他状态都初始化
    private void setRandomValue() {
        boolean hasSame = false;
        cardIndex.clear();
        while (cardIndex.size() < 4) {
            hasSame = false;
            int x = (int) (Math.random() * 1000) % 52 + 1;
            if (cardIndex.size() == 0) {
                cardIndex.add(x);
            } else {
                for (int i = 0; i < cardIndex.size(); i++)
                    if (cardIndex.get(i) == x)
                        hasSame = true;
                if (!hasSame)
                    cardIndex.add(x);
            }
        }
        Log.e(TAG, "setRandomValue:  cardIndex=" + cardIndex.toString());
    }

    private void setCard4() {
        myCardsMap.clear();
        for (int i = 0; i < 4; i++) {

            int x = cardIndex.get(i);
            int value = x % 13 == 0 ? 13 : x % 13;
            idListFrom52[i] = cardList52[x];
            myCardsMap.put(idListOf7[i], new MyCard(cardList52[x], value));
        }
        myCardsMap.put(idListOf7[4], new MyCard(card5.getId(), 0));
        myCardsMap.put(idListOf7[5], new MyCard(card6.getId(), 0));
        myCardsMap.put(idListOf7[6], new MyCard(card7.getId(), 0));
        Log.e(TAG, "setCard4: ");
    }

    private void reInit() {
        Log.e(TAG, "reInit: in");
        is_init = false;
        myCardsMap.clear();
        current_selected_card_id = 0;
        current_opt_id = 0;
        last_value = 0;
        num1 = 0;
        num2 = 0;
        num3 = 0;
        totalAns = "该组合无解";
//        Arrays.fill(selected, false);
        Log.e(TAG, "reInit: out");
    }

    private void initCard() {
        card1.setImageResource(idListFrom52[0]);
        card2.setImageResource(idListFrom52[1]);
        card3.setImageResource(idListFrom52[2]);
        card4.setImageResource(idListFrom52[3]);
        card5.setText("");
        card6.setText("");
        card7.setText("");
        seeOpt.setText("");
        seeNum.setText("");
        usrAnsLine1.setText("");
        usrAnsLine2.setText("");
        usrAnsLine3.setText("");
        card1.setClickable(true);
        card2.setClickable(true);
        card3.setClickable(true);
        card4.setClickable(true);
        card5.setClickable(false);
        card6.setClickable(false);
        card7.setClickable(false);

        Log.e(TAG, "\ninitCard: \nid: card1=" + card1.getId() + ", card2=" + card2.getId() + ", card3=" + card3.getId() + ", card4=" + card4.getId() + ", card5=" + card5.getId() + ", card6=" + card6.getId() + ", card7=" + card7.getId());
    }

    public void randomStart() {
        reInit();
        restart();
        do {
            setRandomValue();
            setCard4();
            initCard();
            is_init = true;
//                if (hasAns()) break;
        } while (!hasAns() && isDeleteNoneSolution.isChecked());

        Log.e(TAG, "\nrandomStart: out, \nmyCardsMap:" + myCardsMap.keySet().toString() + "\nidListFrom52:" + Arrays.toString(idListFrom52));
        Log.e(TAG, "\n id: card1=" + card1.getId() + ", card2=" + card2.getId() + ", card3=" + card3.getId() + ", card4=" + card4.getId() + ", card5=" + card5.getId() + ", card6=" + card6.getId() + ", card7=" + card7.getId());

    }

    // 自定义卡片
    // 点击卡片时，跳出选择框
    public void setCardByUser(View view) {
        Intent intent = new Intent(this, ChooseCard.class);
        intent.setFlags(FLAG_ACTIVITY_SINGLE_TOP);
        startActivityForResult(intent, 0);

    }

    private Boolean hasAns() {
        int n1, n2, n3, n4;
        n1 = Objects.requireNonNull(myCardsMap.get(idListOf7[0])).value;
        n2 = Objects.requireNonNull(myCardsMap.get(idListOf7[1])).value;
        n3 = Objects.requireNonNull(myCardsMap.get(idListOf7[2])).value;
        n4 = Objects.requireNonNull(myCardsMap.get(idListOf7[3])).value;
        totalAns = ComputeCard24.getResult(new float[]{n1, n2, n3, n4});
        return !Objects.equals(totalAns, "该组合无解");
    }

    // 查看答案
    public void showAnswer(View view) {
        if (!is_init) return;
        new AlertDialog.Builder(PlayGame.this)
                .setTitle("参考答案")//设置对话框标题
                .setMessage(totalAns)
                .setPositiveButton("返回", new DialogInterface.OnClickListener() {//添加确定按钮
                    @Override
                    public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件，点击事件没写，自己添加
                        dialog.cancel();
                    }
                }).show();//在按键响应事件中显示此对话框


    }

    // 重新开始操作
    public void restart() {
        if (!is_init) return;
        initCard();
        current_opt_id = 0;
        current_selected_card_id = 0;
        current_selected_card_value = 0;
        num1 = 0;
        num2 = 0;
        num3 = 0;
        Log.e(TAG, "restart: finish");
    }


    public void usrClick(View view) {
        int x = view.getId();
        switch (x) {
            case R.id.card1:
            case R.id.card4:
            case R.id.card3:
            case R.id.card2:
                cardClick(view, getCardValue(x));
                break;
            case R.id.bt_add:
            case R.id.bt_sub:
            case R.id.bt_mul:
            case R.id.bt_div:
                optClick(view);
                break;
            case R.id.bt_num1:
                cardClick(view, num1);
                break;
            case R.id.bt_num2:
                cardClick(view, num2);
                break;
            case R.id.bt_num3:
                cardClick(view, num3);
                break;
            case R.id.btn_selfInitial:
                setCardByUser(view);
                break;
            case R.id.btn_showAnswer:
                showAnswer(view);
                break;
            case R.id.btn_restart:
                restart();
                break;
            case R.id.btn_randomStart:
                randomStart();
                break;
        }
    }
}