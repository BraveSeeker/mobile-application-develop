package com.example.point24;
import java.util.*;
import java.util.*;
public class ComputeCard24 {
private static final HashSet<String> ansSet = new HashSet<>();
    private static int num = 0;
    //插入排序
    public static int[] cr(int[] arr) {
        for (int i = 1; i < arr.length; i++) {
            int tmp = arr[i];
            int j = i - 1;
            while (tmp > arr[j]) {
                arr[j + 1] = arr[j];
                j--;
                if (j == -1) {
                    break;
                }
            }
            arr[j + 1] = tmp;
        }
        return arr;
    }

    //运算
    public static float opt(float a, float b, char optIcon) {
        float result;
        switch (optIcon) {
            case '+':
                result = a + b;
                break;
            case '-':
                result = a - b;
                break;
            case '*':
                result = a * b;
                break;
            case '/':
                result = a / b;
                break;
            default:
                result = 0.0f;
                break;
        }
        return result;
    }

    //计算24
    public static String compute(float a, float b, float c, float d) {
        char[] fh = {'+', '-', '*', '/'};
        String str = "";
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                for (int k = 0; k < 4; k++) {
                    if (ComputeCard24.opt(ComputeCard24.opt(ComputeCard24.opt(a, b, fh[i]), c, fh[j]), d, fh[k]) == 24) {
                        str = String.format("((%d %s %d) %s %d) %s %d = 24\n",  (int)a, fh[i], (int)b, fh[j], (int)c, fh[k], (int)d);
                        ansSet.add(str);
                    }
                }
            }
        }
        return str.equals("") ? "该组合无解" : str;
    }



    public static String getResult(float[] arr) {
        num = 0;
        ansSet.clear();
        StringBuilder result = new StringBuilder();
        String tmpstr = "";

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                for (int k = 0; k < 4; k++) {
                    for (int l = 0; l < 4; l++) {
                        if (i != j && i != k && i != l && j != k && j != l && k != l) {
                            tmpstr = ComputeCard24.compute(arr[i], arr[j], arr[k], arr[l]);
                            if (!Objects.equals(tmpstr, "该组合无解")) {
                                ansSet.add(tmpstr);
                            }
                        }
                    }
                }
            }
        }
        //输出迭代之后的值
        for (String s : ansSet)
            result.append(++num).append(". ").append(s);
        return result.toString().equals("") ? "该组合无解" : result.toString();
    }

    public static void main(String[] args) {
        float[] array = new float[]{8, 2, 3, 4};
        String str = ComputeCard24.getResult(array);
        System.out.print(str);
    }
}