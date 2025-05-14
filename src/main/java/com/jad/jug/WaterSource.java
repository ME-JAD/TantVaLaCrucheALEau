package com.jad.jug;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

public final class WaterSource {
    static final int WATER_FILL_INTERVAL = 500;
    static final int MAX_WATER_CAPACITY = 10;

    private static WaterSource instance;

    private final BlockingQueue<Integer> reservoir = new LinkedBlockingQueue<>(WaterSource.MAX_WATER_CAPACITY);
    private final List<AbstractJug> activeJugs = new CopyOnWriteArrayList<>();

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final ReentrantLock lock = new ReentrantLock();

    private WaterSource() {
        try {
            for (int i = 0; i < WaterSource.MAX_WATER_CAPACITY - 1; i++) {
                this.reservoir.put(1);
            }
        } catch (InterruptedException exception) {
            System.err.println("Reservoir initialization interrupted: " + exception.getMessage());
        }

        this.scheduler.scheduleAtFixedRate(() -> {
            try {
                if (this.reservoir.remainingCapacity() > 0) {
                    this.reservoir.put(1);
                    System.out.println("Reservoir level: " + this.reservoir.size());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, 0, WaterSource.WATER_FILL_INTERVAL, TimeUnit.MILLISECONDS);
    }

    public static synchronized WaterSource getInstance() {
        if (WaterSource.instance == null) WaterSource.instance = new WaterSource();
        return WaterSource.instance;
    }

    public Water fetchWater(AbstractJug jug) {
        this.activeJugs.add(jug);

        this.lock.lock();
        try {
            if (this.activeJugs.size() > 1) {
                System.out.println("Too many jugs! All jugs shatter!");
                for (AbstractJug activeJug : this.activeJugs) {
                    activeJug.shatter();
                }
                this.activeJugs.clear();
                return null;
            }

            Integer waterUnit = this.reservoir.poll();
            System.out.println("Reservoir level: " + this.reservoir.size());
            this.activeJugs.clear();
            return (waterUnit != null) ? new Water(waterUnit) : null;
        } finally {
            this.lock.unlock();
        }
    }

    public void shutdown() {
        this.scheduler.shutdown();
        try {
            if (!this.scheduler.awaitTermination(2, TimeUnit.SECONDS)) {
                this.scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            this.scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
        System.out.println("Water source has stopped.");
    }
}
