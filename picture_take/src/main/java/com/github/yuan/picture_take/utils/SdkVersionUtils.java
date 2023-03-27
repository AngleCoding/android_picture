package com.github.yuan.picture_take.utils;

import android.os.Build;

/**
 * @author：luck
 * @date：2019-07-17 15:12
 * @describe：Android Sdk版本判断
 */
public class SdkVersionUtils {

    public static final int R = 30;

    public static final int TIRAMISU = 33;

    /**
     * 判断是否是低于Android LOLLIPOP版本
     */
    public static boolean isMinM() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M;
    }

    /**
     * 判断是否是Android O版本
     */
    public static boolean isO() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }


    /**
     * 判断是否是Android N版本
     */
    public static boolean isMaxN() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
    }


    /**
     * 判断是否是Android N版本
     */
    public static boolean isN() {
        return Build.VERSION.SDK_INT == Build.VERSION_CODES.N;
    }

    /**
     * 判断是否是Android P版本
     */
    public static boolean isP() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.P;
    }

    /**
     * 判断是否是Android Q版本
     */
    public static boolean isQ() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
    }

    /**
     * 判断是否是Android R版本
     */
    public static boolean isR() {
        return Build.VERSION.SDK_INT >= R;
    }

    /**
     * 判断是否是Android TIRAMISU版本
     */
    public static boolean isTIRAMISU() {
        return Build.VERSION.SDK_INT >= TIRAMISU;
    }
}
