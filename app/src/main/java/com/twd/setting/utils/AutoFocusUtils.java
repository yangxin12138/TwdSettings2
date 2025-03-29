package com.twd.setting.utils;

/**
 * @Author:Yangxin
 * @Description:
 * @time: Create in 下午2:32 28/3/2025
 */
public class AutoFocusUtils {
    public AutoFocusUtils() {
    }

    /**
     * 设置 自动梯形矫正 开启或者关闭
     * 如使用康佳梯形矫正算法，此处无需填写
     * @param enable：true代表需要开启自动梯形矫正   false代表需要关闭自动梯形矫正
     */
    public void setTrapezoidCorrectEnable(boolean enable){
        if (enable) {
            SystemPropertiesUtils.setProperty("persist.sys.trapezoid", "1");
        }else {
            SystemPropertiesUtils.setProperty("persist.sys.trapezoid", "0");
        }
    }

    /**
     * 获取 自动梯形矫正 开启或者关闭 的状态
     * 如使用康佳梯形矫正算法，此处无需填写
     * @return status: true代表当前开启了自动梯形矫正   false代表当前关闭了自动梯形矫正
     */
    public String getTrapezoidCorrectStatus(){
        return SystemPropertiesUtils.getProperty("persist.sys.trapezoid","0");
    }

    /**
     * 设置 自动对焦 开启或者关闭
     * @param enable：true代表需要开启自动对焦   false代表需要关闭自动对焦
     */
    public void setAutoFocusEnable(boolean enable){
        if (enable) {
            SystemPropertiesUtils.setProperty("persist.sys.autofocus", "1");
        }else {
            SystemPropertiesUtils.setProperty("persist.sys.autofocus", "0");
        }
    }

    /**
     * 获取 自动对焦 开启或者关闭 的状态
     * 如使用康佳梯形矫正算法，此处无需填写
     * @return status: true代表当前开启了自动对焦   false代表当前关闭了自动对焦
     */
    public String getAutoFocusStatus(){
        return SystemPropertiesUtils.getProperty("persist.sys.autofocus","0");
    }

    /**
     * 设置 开机自动对焦 开启或者关闭
     * 如使用康佳梯形矫正算法，此处无需填写
     * @param enable：true代表需要开启开机自动对焦   false代表需要关闭开机自动对焦
     */
    public void setPowerOnAutoFocusEnable(boolean enable){
        if (enable) {
            SystemPropertiesUtils.setProperty("persist.sys.poweronFocus", "1");
        }else {
            SystemPropertiesUtils.setProperty("persist.sys.poweronFocus", "0");
        }
    }

    /**
     * 获取 开机自动对焦 开启或者关闭 的状态
     * 如使用康佳梯形矫正算法，此处无需填写
     * @return status: true代表当前开启了开机自动对焦   false代表当前关闭了开机自动对焦
     */
    public String getPowerOnAutoFocusStatus(){
        //return true;
        return SystemPropertiesUtils.getProperty("persist.sys.poweronFocus", "0");
    }

    /**
     * 设置 自动避障 开启或者关闭
     * 如使用康佳梯形矫正算法，此处无需填写
     * @param enable：true代表需要开启自动避障   false代表需要关闭自动避障
     */
    public void setAutoObstacleAvoidanceEnable(boolean enable){
        if (enable) {
            SystemPropertiesUtils.setProperty("persist.sys.AutoObstacle", "1");
        }else {
            SystemPropertiesUtils.setProperty("persist.sys.AutoObstacle", "0");
        }
    }

    /**
     * 获取 自动避障 开启或者关闭 的状态
     * 如使用康佳梯形矫正算法，此处无需填写
     * @return status: true代表当前开启了自动避障   false代表当前关闭了自动避障
     */
    public String getAutoObstacleAvoidanceStatus(){
        return SystemPropertiesUtils.getProperty("persist.sys.AutoObstacle", "0");
    }

    /**
     * 设置 自动入慕 开启或者关闭
     * 如使用康佳梯形矫正算法，此处无需填写
     * @param enable：true代表需要开启自动入慕   false代表需要关闭自动入慕
     */
    public void setAutoComeAdmireEnable(boolean enable){
        if (enable) {
            SystemPropertiesUtils.setProperty("persist.sys.AutoComeAdmire", "1");
        }else {
            SystemPropertiesUtils.setProperty("persist.sys.AutoComeAdmire", "0");
        }
    }

    /**
     * 获取 自动入慕 开启或者关闭 的状态
     * 如使用康佳梯形矫正算法，此处无需填写
     * @return status: true代表当前开启了自动入慕   false代表当前关闭了自动入慕
     */
    public String getAutoComeAdmireStatus(){
        return SystemPropertiesUtils.getProperty("persist.sys.AutoComeAdmire", "0");
    }
}
