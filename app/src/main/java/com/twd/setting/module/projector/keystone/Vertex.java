package com.twd.setting.module.projector.keystone;

import android.util.Log;

import com.twd.setting.utils.SystemPropertiesUtils;

public class Vertex {
    public int x = 0;
    public int y = 0;
    public int point = 0;
    public int maxXStep =50;
    public int maxYStep=50;
    private static final String TAG = "Vertex";

    public void setX(int value){
        x = value;
    }
    public void setY(int value){
        y = value;
    }
    public void setMaxX(int value){
        maxXStep = value;
    }
    public void setMaxY(int value){
        maxYStep = value;
    }
    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
    public int getPoint(){return point;}
    private String ScreenReduction = null;
    private int ScreenOffset;
    int offset = 0;
    public Vertex(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public Vertex(int point,int x, int y) {
        this.point = point;
        this.x = x;
        this.y = y;
    }
    public Vertex(String strVertex) {
        String v[] = strVertex.trim().split(",");
        if (v != null) {
            //this.x = Integer.parseInt(v[0]);
            //this.y = Integer.parseInt(v[1]);
            this.x = (int)Float.parseFloat(v[0]);
            this.y = (int)Float.parseFloat(v[1]);
        }
    }
    public Vertex(int point,String strVertex) {
        this.point = point;
        String v[] = strVertex.trim().split(",");
        if (v != null) {
            //this.x = Integer.parseInt(v[0]);
            //this.y = Integer.parseInt(v[1]);
            this.x = (int)Float.parseFloat(v[0]);
            this.y = (int)Float.parseFloat(v[1]);
        }
    }

    private int getOffset(){
        ScreenReduction = SystemPropertiesUtils.readSystemProp("SCREEN_REDUCTION").trim();
        try {
            ScreenOffset = Integer.parseInt(SystemPropertiesUtils.readSystemProp("SCREEN_OFFSET").trim());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        if (ScreenReduction!=null && ScreenReduction.equals("true")){
            offset = (Math.max(ScreenOffset, 0));
        }
        return offset;
    }
    @Override
    public String toString() {
        Log.i(TAG, "toString: -------x = "+ x+",getOffset() = "+getOffset()+",y = "+y+",getOffset()");
        return (x-getOffset()) + "," + (y-getOffset());
    }

    public String toSaveString(){
        return x + "," + y;
    }

    public void doLeft(){
        switch (point){
            case 0:
                Log.d(TAG, "doLeft: case0");
                x = x - 1;
                if(x<getOffset()){
                    x = getOffset();
                }
                Log.i(TAG, "-------------doLeft: x = "+x);
                break;
            case 3:
                Log.d(TAG, "doLeft: case3");
                x = x - 1;
                if(x<getOffset()){
                    x = getOffset();
                }
                Log.i(TAG, "-------------doLeft: x = "+x);
                break;
            case 1:
                Log.d(TAG, "doLeft: case1");
                x = x + 1;
                if(x > maxXStep){
                    x = maxXStep;
                }
                Log.i(TAG, "-------------doLeft: x = "+x);
                break;
            case 2:
                Log.d(TAG, "doLeft: case2");
                x = x + 1;
                if(x > maxXStep){
                    x = maxXStep;
                }
                Log.i(TAG, "-------------doLeft: x = "+x);
                break;
            default:
                break;
        }
    }
    public void doRight(){
        switch (point){
            case 0:
                Log.d(TAG, "doRight: case0");
                Log.d(TAG, "doRight: case3");
                x = x + 1;
                if(x>maxXStep){
                    x = maxXStep;
                }
                Log.i(TAG, "-------------doRight: x = "+x);
                break;
            case 3:
                Log.d(TAG, "doRight: case3");
                x = x + 1;
                if(x>maxXStep){
                    x = maxXStep;
                }
                Log.i(TAG, "-------------doRight: x = "+x);
                break;
            case 1:
                Log.d(TAG, "doRight: case1");
                x = x - 1;
                if(x < getOffset()){
                    x = getOffset();
                }
                Log.i(TAG, "-------------doRight: x = "+x);
                break;
            case 2:
                Log.d(TAG, "doRight: case2");
                x = x - 1;
                if(x < getOffset()){
                    x = getOffset();
                }
                Log.i(TAG, "-------------doRight: x = "+x);
                break;
            default:
                break;
        }
    }
    public void doTop(){
        switch (point){
            case 0:
                Log.d(TAG, "doTop: case0");
                y = y - 1;
                if(y<getOffset()){
                    y = getOffset();
                }
                Log.i(TAG, "-------------doTop: y = "+y);
                break;
            case 1:
                Log.d(TAG, "doTop: case1");
                y = y - 1;
                if(y<getOffset()){
                    y = getOffset();
                }
                Log.i(TAG, "-------------doTop: y = "+y);
                break;
            case 2:
                Log.d(TAG, "doTop: case2");
                y = y + 1;
                if(y>maxYStep){
                    y = maxYStep;
                }
                Log.i(TAG, "-------------doTop: y = "+y);
                break;
            case 3:
                Log.d(TAG, "doTop: case3");
                y = y + 1;
                if(y>maxYStep){
                    y = maxYStep;
                }
                Log.i(TAG, "-------------doTop: y = "+y);
                break;
            default:
                break;
        }
    }
    public void doBottom(){
        switch (point){
            case 0:
                Log.d(TAG, "doBottom: case0");
                y = y + 1;
                if(y>maxYStep){
                    y = maxYStep;
                }
                Log.i(TAG, "-------------doBottom: y = "+y);
                break;
            case 1:
                Log.d(TAG, "doBottom: case1");
                y = y + 1;
                if(y>maxYStep){
                    y = maxYStep;
                }
                Log.i(TAG, "-------------doBottom: y = "+y);
                break;
            case 2:
                Log.d(TAG, "doBottom: case2");
                y = y - 1;
                if(y<getOffset()){
                    y =getOffset();
                }
                Log.i(TAG, "-------------doBottom: y = "+y);
                break;
            case 3:
                Log.d(TAG, "doBottom: case3");
                y = y - 1;
                if(y<getOffset()){
                    y = getOffset();
                }
                Log.i(TAG, "-------------doBottom: y = "+y);
                break;
            default:
                break;
        }
    }


}
