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
public class Layer extends Matrix {
    
    static final double BASE_ETA, BASE_MOMENTUM;
    
    static {
        BASE_ETA = 0.01d;
        BASE_MOMENTUM = 0.9d;
    }
    
    private final Activation activation;
    
    private final Matrix weightVelocities;
    
    private Matrix input, activationGradients, weightGradients;
    
    public Layer(final int inputCount, final int neuronCount, final Activation activation) {
        super(VectorType.COLUMN, inputCount, neuronCount);
        this.activation = activation;
        this.weightVelocities = new Matrix(VectorType.COLUMN, inputCount, neuronCount);
    }
    
    public final void initRand() {
        for(int i = 0; i < super.vectorCount(); i ++) {
            super.instance(i).initScaledRand(super.vectorCount());
        }
    }
    
    public final Matrix transform(final Matrix inputField) {
        this.input = inputField;
        return this.activate(super.process(inputField));
    }
    
    private final Matrix activate(final Matrix result) {
        activationGradients = new Matrix(VectorType.ROW,
            result.vectorCount(), result.instance(0).size());
        for(int i = 0; i < result.vectorCount(); i ++) {
            final int numRows = result.instance(i).size();
            for(int r = 0; r < numRows; r ++) {
                double value = result.instance(i).get(r);
                double gradient;
                switch(activation) {
                    default:
                    case TANH:
                        value = Math.tanh(value);
                        gradient = 1d - value*value;
                        break;
                    case RELU:
                        value = Math.max(0d, value);
                        gradient = value > 0d ? 1d : 0d;
                        break;
                }
                result.instance(i).set(r, value);
                activationGradients.instance(i).set(r, gradient);
            }
        }
        return result;
    }
    
    public final Matrix reform(final Matrix errSignals) {
        return super.transpose().process(interperate(errSignals));
    }
    
    private final Matrix interperate(final Matrix errSignals) {
        final Matrix nodeDelta = errSignals.matrixScale(activationGradients);
        weightGradients = nodeDelta.process(input.transpose()); 
        return nodeDelta;
    }
    
    public final void update() {
        weightGradients.scale(BASE_ETA);
        
        super.lose(weightGradients);
        
        weightGradients = null;
    }
    
    public final void updateWithMomentum() {
        weightVelocities.scale(BASE_MOMENTUM);
        weightGradients.scale(1d-BASE_MOMENTUM);
        
        weightVelocities.lose(weightGradients);
        
        for(int c = 0; c < weightVelocities.vectorCount(); c ++) {
            super.instance(c).elementAdd(weightVelocities.instance(c).popScale(BASE_ETA));
        }
    }
    
}
