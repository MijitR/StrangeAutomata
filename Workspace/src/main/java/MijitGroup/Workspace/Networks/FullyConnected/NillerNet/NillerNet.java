/*
 * Copyright (C) 2020 mijitr <MijitR.xyz>
 *
 * Dis How Ballers Do...
 */
package MijitGroup.Workspace.Networks.FullyConnected.NillerNet;

import MijitGroup.Workspace.Math.Matrix;
import MijitGroup.Workspace.Math.VectorType;

/**
 *
 * @author mijitr <MijitR.xyz>
 */
public class NillerNet {
    
    public static final int MINI_BATCH_SIZE, FEATURE_COUNT;
    
    static {
        MINI_BATCH_SIZE = 1;
        FEATURE_COUNT = 8;
    }
    
    private final Layer[] brain;
    
    private Matrix recentResults;
    
    public NillerNet(final int inputSize,
            final int[] layerSizes, final Activation[] activations)
    {
        brain = new Layer[Math.max(layerSizes.length, activations.length)];
        for(int i = 0; i < brain.length; i ++) {
            brain[i] = new Layer(i==0?inputSize:layerSizes[i-1],
                layerSizes[i], activations[i]);
            brain[i].initRand();
        }
    }
    
    public final Matrix process(final double[][] inputBatch) {
        Matrix flowMat = new Matrix(VectorType.ROW, inputBatch);
        for(int l = 0; l < brain.length; l ++) {
            flowMat = brain[l].transform(flowMat);
        } return recentResults = flowMat;
    }
    
    public final Matrix trainInstant(final double[][] targets) {
        recentResults.lose(new Matrix(VectorType.ROW, targets));
        for(int l = brain.length - 1; l >= 0; l --) {
            recentResults = brain[l].reform(recentResults);
            brain[l].updateWithMomentum();
        } return recentResults;
    }
    
}
