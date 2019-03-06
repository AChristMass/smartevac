package com.buildinnov.smartevac.plugin.evacplans_generation.services;

import org.tinfour.common.IMonitorWithCancellation;

public class TINMonitor implements IMonitorWithCancellation {
    @Override
    public int getReportingIntervalInPercent() {
        return 0;
    }

    @Override
    public void reportProgress(int i) {
        System.out.println("Progess i  :"+i);

    }

    @Override
    public void reportDone() {

    }

    @Override
    public void postMessage(String s) {
        System.out.println("TIN Message "+s);
    }

    @Override
    public boolean isCanceled() {
        return false;
    }
}
