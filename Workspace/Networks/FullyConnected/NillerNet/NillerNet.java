/*
 * Copyright (C) 2020 mijitr <MijitR.xyz>
 *
 * Dis How Ballers Do...
 */
package MijitGroup.Workspace.Networks.FullyConnected.NillerNet;

import MijitGroup.Workspace.Math.Matrix;
import MijitGroup.Workspace.Math.MAJOR;

/**
 *
 * @author mijitr <MijitR.xyz>
 */
public class NillerNet {
    
    private final Layer[] brain;
    
    private Matrix recentResults;
    
    private double recentError;
    
    public NillerNet(final int inputSize,
            final int[] layerSizes, final Activation[] activations)
    {
        brain = new Layer[Math.max(layerSizes.length, activations.length)];
        for(int i = 0; i < brain.length; i ++) {
            brain[i] = new Layer( (i == 0 ? inputSize:layerSizes[i-1]),
                layerSizes[i], activations[i]);
            brain[i].initRand();
        }
        recentError = Double.POSITIVE_INFINITY;
    }
    
    public final Matrix process(final double[][] inputBatch) {
        Matrix flowMat = new Matrix(MAJOR.ROW, inputBatch);
        for(int l = 0; l < brain.length; l ++) {
            flowMat = brain[l].transform(new BiasedMatrix(flowMat));
        } return (recentResults = flowMat).clone();
    }
    
    public final Matrix trainInstant(final double[][] targets) {
        if(recentResults == null) {
            return null;
        }
        recentResults.huberLose(new Matrix(MAJOR.ROW, targets));
        recentError = recentResults.sumSquared();
        recentResults = new BiasedMatrix(recentResults);
        for(int l = brain.length - 1; l >= 0; l --) {
            recentResults = brain[l].reform(recentResults.trimColumn());
            brain[l].updateWithMomentum();
        } return recentResults.trimColumn();
    }
    
    public final Matrix train(final double[][] targets) {
        if(recentResults == null) {
            return null;
        }
        recentResults.huberLose(new Matrix(MAJOR.ROW, targets));
        recentError = recentResults.sumSquared();
        recentResults = new BiasedMatrix(recentResults);
        for(int l = brain.length - 1; l >= 0; l --) {
            recentResults = brain[l].reform(recentResults.trimColumn());
        } return recentResults.trimColumn();
    }
    
    public final void update() {
        for(int l = 0; l < brain.length; l ++) {
            brain[l].update();
        }
    }
    
    public final void updateWithMomentum() {
        for(int l = 0; l < brain.length; l ++) {
            brain[l].updateWithMomentum();
        }
    }
    
    public final double recentError() {
        if(recentResults == null) {
            return Double.NaN;
        }
        final double err = recentError / recentResults.vectorCount();
        recentResults = null;
        return err;
    }
    
    public final double whatError(final double[][] targets) {
        if(recentResults == null) {
            return Double.NaN;
        }
        recentResults.lose(new Matrix(MAJOR.ROW, targets));
        final double theoryError = recentResults.sumSquared() / recentResults.vectorCount();
        recentResults = null;
        return theoryError;
    }
    
}
