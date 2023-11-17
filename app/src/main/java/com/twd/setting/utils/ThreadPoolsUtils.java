package com.twd.setting.utils;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
import java.util.concurrent.TimeUnit;

public final class ThreadPoolsUtils {
    public static void commitTask(Runnable paramRunnable) {
        Holder.executor.submit(paramRunnable);
    }

    public static Executor getExecutor() {
        return Holder.executor;
    }

    public static ExecutorService getSingleThread() {
        return Holder.singleThread;
    }

    private static class Holder {
        private static ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 8, 3000L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue(256), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
        private static ExecutorService singleThread = new ThreadPoolExecutor(1, 1, 600000L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue(), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
    }
}