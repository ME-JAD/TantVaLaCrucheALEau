package com.jad;

import com.jad.jug.AbstractJug;
import com.jad.jug.Jug;
import com.jad.jug.WaterSource;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public enum Main {
    ;
    static final int SIMULATION_TIMEOUT = 30;
    private static final int NB_JUGS = 100;

    public static void main(String[] args) {
        WaterSource waterSource = WaterSource.getInstance();

        ExecutorService executor = Executors.newFixedThreadPool(Main.NB_JUGS);

        for (int i = 1; i <= Main.NB_JUGS; i++) {
            AbstractJug jug = new Jug(waterSource);
            executor.submit(jug::fetchWater);
        }

        try {
            TimeUnit.SECONDS.sleep(Main.SIMULATION_TIMEOUT);
        } catch (InterruptedException ignored) {
            throw new RuntimeException("Simulation interrupted");
        } finally {
            waterSource.shutdown();
            executor.shutdown();
        }
    }
}
