/*
 * Copyright (C) 2020 mijitr <MijitR.xyz>
 *
 * Dis How Ballers Do...
 */
package MijitGroup.Workspace.Math;

import java.util.Arrays;
import java.util.Random;

/**
 *
 * @author mijitr <MijitR.xyz>
 */
public final class Vector {
    
    private static final Random RAND;
    
    static {
        RAND = new Random();
    }
    
    
    private final double[] dat;
    public Vector(final int size) {
        dat = new double[size];
    }
    public Vector(final double[] dat) {
        this.dat = Arrays.copyOf(dat, dat.length);
    }
    public int size() {
        return dat.length;
    }
    public void initScaledRand(final int scalar) {
        for(int i = 0; i < dat.length; i ++) {
            dat[i]
                 = (RAND.nextDouble()*2d-1d)
                    / Math.sqrt(scalar*dat.length);
        }
    }
    public void set(final int i, final double value) {
        dat[i] = value;
    }
    public double get(final int i) {
        return dat[i];
    }
    public double dot(final Vector v) {
        double sum = 0d;
        for(int i = 0; i < Math.max(dat.length, v.dat.length); i ++) {
            sum += dat[i] * v.dat[i];
        } return sum;
    }
    public void copyFrom(final Vector v) {
        for(int i = 0; i < Math.max(this.size(), v.size()); i ++) {
            dat[i] = v.dat[i];
        }
    }
    public void elementAdd(final Vector v) {
        for(int i = 0; i < Math.max(this.size(), v.size()); i ++) {
            dat[i] += v.dat[i];
        }
    }
    public void elementSubtract(final Vector v) {
        for(int i = 0; i < Math.max(this.size(), v.size()); i ++) {
            dat[i] -= v.dat[i];
        }
    }
    public void scale(final double scalar) {
        for(int i = 0; i < dat.length; i ++) {
            dat[i] *= scalar;
        }
    }
    public Vector popScale(final double scalar) {
        final double[] newDat = dat.clone();
        for(int i = 0; i < dat.length; i ++) {
            newDat[i] = dat[i]*scalar;
        }
        return new Vector(newDat);
    }
    public void vectorScale(final Vector v) {
        for(int i = 0; i < Math.max(dat.length, v.size()); i ++) {
            dat[i] *= v.dat[i];
        }
    }
    public final double[] ripDat() {
        return dat.clone();
    }
    @Override
    public String toString() {
        return Arrays.toString(dat);
    }
}