package com.jad.jug;

import lombok.Getter;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicLong;

public abstract class AbstractJug {
    private final static AtomicLong nextId = new AtomicLong(0);
    private final long id;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(); // Planification
    @Getter
    private final WaterSource waterSource;
    @Getter
    private Water filledWater;
    @Getter
    private boolean isShattered = false;

    public AbstractJug(final WaterSource waterSource) {
        this.id = AbstractJug.nextId.getAndIncrement();
        this.waterSource = waterSource;
    }

    public final void shatter() {
        System.out.println("Jug " + this.id + " has shattered into pieces!");
        this.isShattered = true;
    }

    public final void fillWithWater(Water water) {
        if (this.isShattered) {
            throw new IllegalStateException("Jug " + this.id + " is shattered and cannot be filled with water.");
        }
        this.filledWater = water;
        System.out.println("Jug " + this.id + " is filled with water.");
    }

    public final void empty() {
        if (this.isShattered) {
            throw new IllegalStateException("Jug " + this.id + " is shattered and cannot be emptied.");
        }
        this.filledWater = null;
        System.out.println("Jug " + this.id + " is empty.");
    }

    public abstract void fetchWater();

    public abstract void runLifecycle();
}
