package com.example.accountapp.slice;

package com.example.bookkeepproject.slice;

import com.example.accountapp.MainAbility;
import com.example.bookkeepproject.MainAbility;
import com.example.bookkeepproject.utils.MyHelper;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.window.dialog.ToastDialog;
import ohos.data.rdb.RdbStore;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * 子界面的基类
 */
public class BaseAbilitySlice extends AbilitySlice {
    MainAbility ability;
    BaseAbilitySlice context;
    RdbStore rs;
    //    Preferences preferences;
    public Calendar calendar = null;
    public int year=Calendar.getInstance().get(Calendar.YEAR);
    public int month=Calendar.getInstance().get(Calendar.MONTH)+1;
    public int day=Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    public String[] months = { "1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月",
            "9月", "10月", "11月", "12月" };

    public String[] types={"支出","收入","转账","余额"};
    @Override
    protected void onStart(Intent intent) {
        super.onStart(intent);
        // 单例模式
        if (ability == null) {
            ability = (MainAbility) getAbility();
        }
        context = this;
//        addAbilitySlice();

        if (calendar == null) {
            calendar = Calendar.getInstance();
        }

        if (rs==null)
            rs= MyHelper.getInstance(getContext(),"BookKeep");
//        if (preferences==null)
//            preferences= MyHelper.getInstance("BKInfo",getContext());
//
//        // 向preferences实例注册观察者
//        PreferencesObserverImpl observer = new PreferencesObserverImpl();
//        preferences.registerObserver(observer);
    }

//    private class PreferencesObserverImpl implements Preferences.PreferencesObserver {
//
//        @Override
//        public void onChange(Preferences preferences, String key) {
//            if ("intKey".equals(key)) {
//                showToastDialogShort("添加了key重复了");
//            }
//        }
//    }

    /**
     * 获得实时的年月日时分秒
     * @return
     */
    public static String getDate() {
        Calendar ca = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        String date = sdf.format(ca.getTimeInMillis());
        return date;
    }

    /**
     * 获得实时的年月日
     * @return
     */
    public static String getDate1() {
        Calendar ca = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(ca.getTimeInMillis());
        return date;
    }

    public void addAbilitySlice() {
        ability.addAbilitySlice(context);
    }

    public void removeAllAbilitySlice() {
        ability.removeAllActivity();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public String getWeek() {
        String info = null;
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int count = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (count > 0) {
            info = month + "_" + (day - count + 1) + "_" + month + "_"
                    + (day + 7 - count);
        }
        return info;
    }

    public String getmonth() {
        String info = null;

        int month = calendar.get(Calendar.MONTH) + 1;
        // 计算是当月的第几天
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        System.out.printf("输入的日期是当月的第%d天\n ", dayOfMonth);
        // 计算当月的第一天
        calendar.add(Calendar.DATE, 1 - dayOfMonth);
        // 计算下月的第一天
        calendar.add(Calendar.MONTH, 1);
        // 计算当月的最后一天
        calendar.add(Calendar.DATE, -1);
        // 计算是当月一共几天
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        System.out.printf("当月一共%d天\n ", dayOfMonth);

        info = month + "月" + 1 + "日--" + month + "月" + dayOfMonth + "日";

        return info;
    }

    public String getyear() {
        String info = null;

        int month = calendar.get(Calendar.MONTH) + 1;
        // 计算是当月的第几天
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        System.out.printf("输入的日期是当月的第%d天\n ", dayOfMonth);
        // 计算当月的第一天
        calendar.add(Calendar.DATE, 1 - dayOfMonth);
        // 计算下月的第一天
        calendar.add(Calendar.MONTH, 1);
        // 计算当月的最后一天
        calendar.add(Calendar.DATE, -1);
        // 计算是当月一共几天
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        System.out.printf("当月一共%d天\n ", dayOfMonth);

        info = "1月1日--" + month + "月" + dayOfMonth + "日";

        return info;
    }

    /**
     * 查看当前月份
     * @param month
     * @return
     */
    public int select_month(int month) {

        return (month == 13) ? 1 : (month);
    }

    /**
     * 查看当前天数
     * @param month
     * @param day
     * @return
     */
    public String select_day(int month, int day) {
        if (day >= 32) {
            int months = select_month(month + 1);
            return months + "-" + (day % 31);
        } else {
            return month + "-" + day;
        }
    }

    public void showToastDialogShort(String info){
        new ToastDialog(context).setText(info).setDuration(1000).show();
    }

    public void showToastDialogLong(String info){
        new ToastDialog(context).setText(info).setDuration(2000).show();
    }
}