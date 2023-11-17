package com.twd.setting.module.network.util;

import android.app.Activity;
import android.util.Log;

import androidx.lifecycle.ViewModel;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class StateMachine
        extends ViewModel {
    private static final String TAG = "StateMachine";

    public static final int ADD_START = 0;
    public static final int CANCEL = 1;
    public static final int CONTINUE = 2;
    public static final int FAIL = 3;
    public static final int EARLY_EXIT = 4;
    public static final int CONNECT = 5;
    public static final int SELECT_WIFI = 6;
    public static final int PASSWORD = 7;
    public static final int OTHER_NETWORK = 8;
    public static final int KNOWN_NETWORK = 9;
    public static final int RESULT_REJECTED_BY_AP = 10;
    public static final int RESULT_UNKNOWN_ERROR = 11;
    public static final int RESULT_TIMEOUT = 12;
    public static final int RESULT_BAD_AUTH = 13;
    public static final int RESULT_SUCCESS = 14;
    public static final int RESULT_FAILURE = 15;
    public static final int TRY_AGAIN = 16;
    public static final int ADD_PAGE_BASED_ON_NETWORK_CHOICE = 17;
    public static final int OPTIONS_OR_CONNECT = 18;
    public static final int IP_SETTINGS = 19;
    public static final int IP_SETTINGS_INVALID = 20;
    public static final int PROXY_HOSTNAME = 21;
    public static final int PROXY_SETTINGS_INVALID = 22;
    public static final int ADVANCED_FLOW_COMPLETE = 23;
    public static final int ENTER_ADVANCED_FLOW = 24;
    public static final int EXIT_ADVANCED_FLOW = 25;
    public static final int SOFTAP_NAME_INVALID = 26;
    public static final int SOFTAP_PASSWORD_INVALID = 27;
    public static final int PPPOE_SETUP = 28;
    public static final int PPPOE_SETUP_INVALID = 29;

    private Callback mCallback;
    private State.StateCompleteListener mCompletionListener = this::updateState;
 /*   private State.StateCompleteListener mCompletionListener = new State.StateCompleteListener() {
        @Override
        public void onComplete(int paramInt) {
            Log.d(TAG,"State.StateCompleteListener onComplete");
            updateState(1);
        }
    };
  */
    private LinkedList<State> mStatesList = new LinkedList<>();
    private Map<State, List<Transition>> mTransitionMap = new HashMap();
    public @interface Event {
    }
    public StateMachine() {
    }

    public StateMachine(Callback callback) {
        mCallback = callback;
    }

    public void setCallback(Callback paramCallback) {
        mCallback = paramCallback;
    }

    public void addState(State state, @Event int event, State destination) {
        if (!mTransitionMap.containsKey(state)) {
            mTransitionMap.put(state, new ArrayList<>());
        }
        mTransitionMap.get(state).add(new Transition(state, event, destination));
    }


    public void addTerminalState(State state) {
        mTransitionMap.put(state, new ArrayList<>());
    }

    public interface Callback {
        void onFinish(int result);
    }

    public void setStartState(State startState) {
        mStatesList.addLast(startState);
    }

    public void start(boolean movingForward) {
        if (mStatesList.isEmpty()) {
            throw new IllegalArgumentException("Start state not set");
        }
        State currentState = getCurrentState();
        if (movingForward) {
            currentState.processForward();
        } else {
            currentState.processBackward();
        }
    }

    public void reset() {
        mStatesList = new LinkedList<>();
    }

    public void back() {
        updateState(CANCEL);
    }

    public State getCurrentState() {
        if (!mStatesList.isEmpty()) {
            return mStatesList.getLast();
        } else {
            return null;
        }
    }

    public void finish(int result) {
        this.mCallback.onFinish(result);
    }

    private void updateState(@Event int event) {
        // Handle early exits first.
        if (event == EARLY_EXIT) {
            finish(Activity.RESULT_OK);
            return;
        } else if (event == FAIL) {
            finish(Activity.RESULT_CANCELED);
            return;
        }

        // Handle Event.CANCEL, it happens when the back button is pressed.
        if (event == CANCEL) {
            if (mStatesList.size() < 2) {
                mCallback.onFinish(Activity.RESULT_CANCELED);
            } else {
                mStatesList.removeLast();
                State prev = mStatesList.getLast();
                prev.processBackward();
            }
            return;
        }

        State next = null;
        State currentState = getCurrentState();

        List<Transition> list = mTransitionMap.get(currentState);
        if (list != null) {
            for (Transition transition : mTransitionMap.get(currentState)) {
                if (transition.event == event) {
                    next = transition.destination;
                }
            }
        }

        if (next == null) {
            if (event == CONTINUE) {
                mCallback.onFinish(Activity.RESULT_OK);
                return;
            }
            throw new IllegalArgumentException(
                    getCurrentState().getClass() + "Invalid transition " + event);
        }

        addToStack(next);
        next.processForward();
    }
    private void addToStack(State state) {
        for (int i = mStatesList.size() - 1; i >= 0; i--) {
            if (equal(state, mStatesList.get(i))) {
                for (int j = mStatesList.size() - 1; j >= i; j--) {
                    mStatesList.removeLast();
                }
            }
        }
        mStatesList.addLast(state);
    }

    private boolean equal(State s1, State s2) {
        if (!s1.getClass().equals(s2.getClass())) {
            return false;
        }
        return true;
    }

    public State.StateCompleteListener getListener() {
        return mCompletionListener;
    }


}

