package android.app;

public class TwdManager {
    private static final String TAG = "mTwdManager";

    private static TwdManager mTwdManager = null;
    public static TwdManager getInstance(){
        throw new RuntimeException("API not supported!");
    }
//    public VkeystoneManager(Context context) {
//        mService = IKeystoneService.Stub.asInterface(
//            ServiceManager.getService(Context.KEYSTONE_SERVICE));
//    }

    private static final int LCD_WIDTH = 1280;
    private static final int LCD_HEIGHT = 720;
    /**
     * 设置安装模式
     * type：安装模式的类型(0：桌面正投   1：桌面背投   2：吊装正投   3：吊装背投)
     */
    public void setScreenMirror(int type){
        throw new RuntimeException("API not supported!");
    }

    /**
     * 获取安装模式
     * 0：桌面正投   1：桌面背投   2：吊装正投   3：吊装背投
     */
    public int getScreenMirror(){
        throw new RuntimeException("API not supported!");
    }
    /**
     * 四点梯形矫正--设置左上角坐标
     */
    public void setLeftTopOffset(float x, float y){
        throw new RuntimeException("API not supported!");
    }

    /**
     * 四点梯形矫正--获取左上角坐标
     */
    public float[] getLeftTopOffset(){
        throw new RuntimeException("API not supported!");
    }

    /**
     * 四点梯形矫正--设置右上角坐标
     */
    public void setRightTopOffset(float x, float y){
        throw new RuntimeException("API not supported!");
    }

    /**
     * 四点梯形矫正--获取右上角坐标
     */
    public float[] getRightTopOffset(){
        throw new RuntimeException("API not supported!");
    }

    /**
     * 四点梯形矫正--设置左下角坐标
     */
    public void setLeftBottomOffset(float x, float y){
        throw new RuntimeException("API not supported!");
    }

    /**
     * 四点梯形矫正--获取左下角坐标
     */
    public float[] getLeftBottomOffset(){
        throw new RuntimeException("API not supported!");
    }

    /**
     * 四点梯形矫正--设置右下角坐标
     */
    public void setRightBottomOffset(float x, float y){
        throw new RuntimeException("API not supported!");
    }

    /**
     * 四点梯形矫正--获取右下角坐标
     */
    public float[] getRightBottomOffset(){
        throw new RuntimeException("API not supported!");
    }

    /**
     * 四点梯形矫正--复位
     */
    public void resetTrapezoid(){
        throw new RuntimeException("API not supported!");
    }
    /**
     * 水平与垂直梯形矫正--设置水平方向角度
     */
    public void setHorizontalDegree(float value){
        throw new RuntimeException("API not supported!");
    }
    /**
     * 水平与垂直梯形矫正--获取水平方向角度
     */
    public float getHorizontalDegree(){
        throw new RuntimeException("API not supported!");
    }
    /**
     * 水平与垂直梯形矫正--设置垂直方向角度
     */
    public void setVerticalDegree(float value){
        throw new RuntimeException("API not supported!");
    }
    /**
     * 水平与垂直梯形矫正--获取垂直方向角度
     */
    public float getVertivalDegree(){

        throw new RuntimeException("API not supported!");
    }

    /**
     * 水平与垂直梯形矫正--复位
     */
    public void resetDegree(){
        throw new RuntimeException("API not supported!");
    }

    /**
     * 缩放--设置缩放值
     */
    public void setZoomValue(int value){
        throw new RuntimeException("API not supported!");
    }

    /**
     * 缩放--获取缩放值
     */
    public int getZoomValue(){
        throw new RuntimeException("API not supported!");
    }

    /**
     * 缩放的最小值
     */
    public int getZoomMinValue(){

        throw new RuntimeException("API not supported!");

    }

    /**
     * 垂直水平校正时，水平方向上，可调整的最大角度
     */
    public int getHorizontalMaxDegree(){
        throw new RuntimeException("API not supported!");
    }

    /**
     * 垂直水平校正时，垂直方向上，可调整的最大角度
     */
    public int getVerticalMaxDegree(){
        throw new RuntimeException("API not supported!");
    }

    /**
     * 四点校正时，X坐标最大值
     */
    public float getFourPointXMaxValue(){
        throw new RuntimeException("API not supported!");
    }

    /**
     * 四点校正时，Y坐标最大值
     */
    public float getFourPointYMaxValue(){
        throw new RuntimeException("API not supported!");
    }


    /**
     * 设置 自动梯形矫正 开启或者关闭
     * 如使用康佳梯形矫正算法，此处无需填写
     * enable：true代表需要开启自动梯形矫正   false代表需要关闭自动梯形矫正
     */
    public void setTrapezoidCorrectEnable(boolean enable){
        throw new RuntimeException("API not supported!");

    }

    /**
     * 获取 自动梯形矫正 开启或者关闭 的状态
     * 如使用康佳梯形矫正算法，此处无需填写
     * status: true代表当前开启了自动梯形矫正   false代表当前关闭了自动梯形矫正
     */
    public boolean getTrapezoidCorrectStatus(){
        throw new RuntimeException("API not supported!");
    }

    /**
     * 设置 自动水平矫正 开启或者关闭
     * 如使用康佳梯形矫正算法，此处无需填写
     * enable：true代表需要开启自动水平矫正   false代表需要关闭自动水平矫正
     */
    public void setHorizontalCorrectEnable(boolean enable){
        throw new RuntimeException("API not supported!");
    }

    /**
     * 获取 自动水平矫正 开启或者关闭 的状态
     * 如使用康佳梯形矫正算法，此处无需填写
     * status: true代表当前开启了自动水平矫正   false代表当前关闭了自动水平矫正
     */
    public boolean getHorizontalCorrectStatus(){
        throw new RuntimeException("API not supported!");
    }

    /**
     * 设置 自动垂直矫正 开启或者关闭
     * 如使用康佳梯形矫正算法，此处无需填写
     * enable：true代表需要开启自动垂直矫正   false代表需要关闭自动垂直矫正
     */
    public void setVerticalCorrectEnable(boolean enable){
        throw new RuntimeException("API not supported!");
    }

    /**
     * 获取 自动垂直矫正 开启或者关闭 的状态
     * 如使用康佳梯形矫正算法，此处无需填写
     * status: true代表当前开启了自动垂直矫正   false代表当前关闭了自动垂直矫正
     */
    public boolean getVerticalCorrectStatus(){
        throw new RuntimeException("API not supported!");
    }

    /**
     * 设置 自动对焦 开启或者关闭
     * enable：true代表需要开启自动对焦   false代表需要关闭自动对焦
     */
    public void setAutoFocusEnable(boolean enable){
        throw new RuntimeException("API not supported!");
    }

    /**
     * 获取 自动对焦 开启或者关闭 的状态
     * 如使用康佳梯形矫正算法，此处无需填写
     * status: true代表当前开启了自动对焦   false代表当前关闭了自动对焦
     */
    public boolean getAutoFocusStatus(){
        throw new RuntimeException("API not supported!");
    }

    /**
     * 设置 开机自动对焦 开启或者关闭
     * 如使用康佳梯形矫正算法，此处无需填写
     * enable：true代表需要开启开机自动对焦   false代表需要关闭开机自动对焦
     */
    public void setPowerOnAutoFocusEnable(boolean enable){
        throw new RuntimeException("API not supported!");
    }

    /**
     * 获取 开机自动对焦 开启或者关闭 的状态
     * 如使用康佳梯形矫正算法，此处无需填写
     * status: true代表当前开启了开机自动对焦   false代表当前关闭了开机自动对焦
     */
    public boolean getPowerOnAutoFocusStatus(){
        throw new RuntimeException("API not supported!");
    }

    /**
     * 设置 自动避障 开启或者关闭
     * 如使用康佳梯形矫正算法，此处无需填写
     * enable：true代表需要开启自动避障   false代表需要关闭自动避障
     */
    public void setAutoObstacleAvoidanceEnable(boolean enable){
        throw new RuntimeException("API not supported!");
    }

    /**
     * 获取 自动避障 开启或者关闭 的状态
     * 如使用康佳梯形矫正算法，此处无需填写
     * status: true代表当前开启了自动避障   false代表当前关闭了自动避障
     */
    public boolean getAutoObstacleAvoidanceStatus(){
        throw new RuntimeException("API not supported!");
    }

    /**
     * 设置 自动入慕 开启或者关闭
     * 如使用康佳梯形矫正算法，此处无需填写
     * enable：true代表需要开启自动入慕   false代表需要关闭自动入慕
     */
    public void setAutoComeAdmireEnable(boolean enable){
        throw new RuntimeException("API not supported!");
    }

    /**
     * 获取 自动入慕 开启或者关闭 的状态
     * 如使用康佳梯形矫正算法，此处无需填写
     * status: true代表当前开启了自动入慕   false代表当前关闭了自动入慕
     */
    public boolean getAutoComeAdmireStatus(){
        throw new RuntimeException("API not supported!");
    }


    /**
     * 设置 全向自动校正 开启或者关闭
     * 如使用康佳梯形矫正算法，此处无需填写
     * enable：true代表 需要开启全向自动校正   false代表 需要关闭全向自动校正
     */
    public void setAllDirAutoCorrectEnable(boolean enable){
        throw new RuntimeException("API not supported!");
    }

    /**
     * 获取 全向自动校正 开启或者关闭 的状态
     * 如使用康佳梯形矫正算法，此处无需填写
     * status: true代表 当前开启了全向自动校正   false代表 当前关闭了全向自动校正
     */
    public boolean getAllDirAutoCorrectStatus(){
        throw new RuntimeException("API not supported!");
    }

    /**
     * 系统是否需要展示 手动四点梯形矫正
     * result: true代表 系统需要展示四点梯形矫正   false代表 系统不需要展示四点梯形矫正
     */
    public boolean isShowFourPointTra(){
        throw new RuntimeException("API not supported!");
    }

    /**
     * 系统是否需要展示 水平与垂直梯形矫正
     * result: true代表 系统需要展示水平与垂直梯形矫正   false代表 系统不需要展示水平与垂直梯形矫正
     */
    public boolean isShowHorizontalVerticalTra(){
        throw new RuntimeException("API not supported!");
    }

    /**
     * 系统是否需要展示 缩放
     * result: true代表 系统需要展示缩放   false代表 系统不需要展示缩放
     */
    public boolean isShowZoom(){
        throw new RuntimeException("API not supported!");
    }

    /**
     * 系统是否需要展示 自动梯形矫正开关
     * result: true代表 系统需要展示自动梯形矫正   false代表 系统不需要展示自动梯形矫正
     */
    public boolean isShowAutoTrapezoidCorrect(){
        throw new RuntimeException("API not supported!");
    }

    /**
     * 系统是否需要展示 全向自动校正开关
     * result: true代表 系统需要展示全向自动校正   false代表 系统不需要展示全向自动校正
     */
    public boolean isShowAllDirAutoCorrect(){
        throw new RuntimeException("API not supported!");
    }

    /**
     * 系统是否需要展示 自动水平校正开关
     * result: true代表 系统需要展示自动水平校正   false代表 系统不需要展示自动水平校正
     */
    public boolean isShowHorizontalAutoCorrect(){
        throw new RuntimeException("API not supported!");
    }

    /**
     * 系统是否需要展示 自动垂直校正开关
     * result: true代表 系统需要展示自动垂直校正   false代表 系统不需要展示自动垂直校正
     */
    public boolean isShowVerticalAutoCorrect(){
        throw new RuntimeException("API not supported!");
    }

    /**
     * 系统是否需要展示 自动对焦开关
     * result: true代表 系统需要展示自动对焦   false代表 系统不需要展示自动对焦
     */
    public boolean isShowAutoFocus(){
        throw new RuntimeException("API not supported!");
    }

    /**
     * 系统是否需要展示 开机自动对焦
     * result: true代表 系统需要展示开机自动对焦   false代表 系统不需要展示开机自动对焦
     */
    public boolean isShowPowerOnAutoFocus(){
        throw new RuntimeException("API not supported!");
    }

    /**
     * 系统是否需要展示 自动避障
     * result: true代表 系统需要展示自动避障  false代表 系统不需要展示自动避障
     */
    public boolean isShowAutoObstacleAvoidance(){
        throw new RuntimeException("API not supported!");
    }

    /**
     * 系统是否需要展示 自动入慕
     * result: true代表 系统需要展示自动入慕  false代表 系统不需要展示自动入慕
     */
    public boolean isShowAutoComeAdmire(){
        throw new RuntimeException("API not supported!");
    }
}
