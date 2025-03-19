package com.twd.setting.module.projector.keystone;

import android.util.Log;

public class Vertex {
    public int x = 0;
    public int y = 0;
    public int point = 0;
    public int maxXStep =50;
    public int maxYStep=50;

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

    @Override
    public String toString() {
        return x + "," + (y/3);
    }

    public void doLeft(){
        switch (point){
            case 0:
            case 3:
                Log.i("yangxin0318", "doLeft: ----doLeft---03");
                x = x - 1;
                if(x<0){
                    x = 0;
                }
                break;
            case 1:
            case 2:
                Log.i("yangxin0318", "doLeft: ----doLeft---12");
                x = x + 1;
                if(x > maxXStep){
                    x = maxXStep;
                }
                break;
            default:
                break;
        }
    }
    public void doRight(){
        switch (point){
            case 0:
            case 3:
                Log.i("yangxin0318", "doRight: ----doRight---03");
                x = x + 1;
                if(x>maxXStep){
                    x = maxXStep;
                }
                break;
            case 1:
            case 2:
                Log.i("yangxin0318", "doRight: ----doRight---03");
                x = x - 1;
                if(x < 0){
                    x = 0;
                }
                break;
            default:
                break;
        }
    }
    public void doTop(){
        switch (point){
            case 2:
            case 3:
                Log.i("yangxin0318", "doTop: ---doTop--23");
                y = y + 3;
                if(y>maxYStep){
                    y = maxYStep;
                }
/*                y = y - 1;
                if(y<0){
                    y = 0;
                }*/
                break;
            case 0:
            case 1:
                Log.i("yangxin0318", "doTop: ---doTop--01");
/*                y = y + 1;
                if(y>maxYStep){
                    y = maxYStep;
                }*/
                y = y - 3;
                if(y<0){
                    y = 0;
                }
                break;
            default:
                break;
        }
    }
    public void doBottom(){
        switch (point){
            case 0:
            case 1:
                Log.i("yangxin0318", "doBottom: ---doBottom--10");
                y = y + 3;
                if(y>maxYStep){
                    y = maxYStep;
                }
                break;
            case 2:
            case 3:
                Log.i("yangxin0318", "doBottom: ---doBottom--23");
                y = y - 3;
                if(y<0){
                    y = 0;
                }
                break;
            default:
                break;
        }
    }


}
