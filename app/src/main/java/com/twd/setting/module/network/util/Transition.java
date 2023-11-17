package com.twd.setting.module.network.util;

public class Transition {
    public State destination;
    public int event;
    public State source;

    public Transition(State paramState1, int paramInt, State paramState2) {
        source = paramState1;
        event = paramInt;
        destination = paramState2;
    }
}


