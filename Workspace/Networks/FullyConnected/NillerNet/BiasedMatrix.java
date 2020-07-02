/*
 * Copyright (C) 2020 mijitr <MijitR.xyz>
 *
 * Dis How Ballers Do...
 */
package MijitGroup.Workspace.Networks.FullyConnected.NillerNet;

import MijitGroup.Workspace.Math.Matrix;
import MijitGroup.Workspace.Math.MAJOR;
import java.util.Arrays;

/**
 *
 * @author mijitr <MijitR.xyz>
 */
public class BiasedMatrix extends Matrix {
    
    public BiasedMatrix(final int rows, final int cols) {
        super(MAJOR.ROW, rows, cols);        
    }
    
    public BiasedMatrix(final Matrix var) {
        super(MAJOR.ROW, var.vectorCount(), var.instance(0).size() + 1);
        
        for(int r = 0; r < vectorCount(); r ++) {
            super.instance(r).copyFrom(var.instance(r).appendBias());
        }
    }
    
    public BiasedMatrix(final double[][] dat) {
        this(dat.length, dat[0].length + 1);
        
        final double[][] bedDat = new double[dat.length][];
        for(int i = 0; i < dat.length; i ++) {
            bedDat[i] = Arrays.copyOf(dat[i], dat[i].length + 1);
            bedDat[i][bedDat[i].length-1] = + 1d;
        }
        
        super.setDat(bedDat);
    }
    
}
