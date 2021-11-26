package com.justsoft.speedtyper.util.concurrent;

import java.util.concurrent.ThreadFactory;

public class DaemonThreadFactory implements ThreadFactory {

    @Override
    public Thread newThread(Runnable r) {
        final Thread daemonThread = new Thread(r);
        daemonThread.setDaemon(true);
        return daemonThread;
    }
}
