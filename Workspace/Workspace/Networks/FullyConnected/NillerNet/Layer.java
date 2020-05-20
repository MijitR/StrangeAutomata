/*
 * Copyright (C) 2020 mijitr <MijitR.xyz>
 *
 * Dis How Ballers Do...
 */
package MijitGroup.Workspace.Networks.FullyConnected.NillerNet;

import MijitGroup.Workspace.Math.Matrix;
import MijitGroup.Workspace.Math.Vector;
import MijitGroup.Workspace.Math.ORIENT_MAJOR;

/**
 *
 * @author mijitr <MijitR.xyz>
 */
public class Layer extends Matrix {
    
    static final double BASE_ETA, BASE_MOMENTUM;
    
    static {
        BASE_ETA = 0.001d;
        BASE_MOMENTUM = 0.9d;
    }
    
    private final Activation activation;
    
    private final Matrix weightVelocities;
    
    private Matrix input, activationGradients, weightGradients;
    
    public Layer(final int inputCount, final int neuronCount, final Activation activation) {
        super(ORIENT_MAJOR.COLUMN, inputCount, neuronCount);
        this.activation = activation;
        this.weightVelocities = new Matrix(ORIENT_MAJOR.COLUMN, inputCount, neuronCount);
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
        activationGradients = new Matrix(ORIENT_MAJOR.ROW,
            result.vectorCount(), result.instance(0).size());
        for(int i = 0; i < result.vectorCount(); i ++) {
            final Vector row = result.instance(i);
            final int numRows = row.size();
            for(int r = 0; r < numRows; r ++) {
                double value = row.get(r);
                double gradient;
                final double temp;
                switch(activation) {
                    default:
                        System.out.println("Usupported activation :"
                                + activation.name() + ": defaulting tanh");
                    case TANH:
                        value = Math.tanh(value);
                        gradient = 1d - value*value;
                        break;
                    case RELU:
                        value = Math.max(0d, value);
                        gradient = value > 0d ? 1d : 0d;
                        break;
                    case SIGMOID:
                        value = 1d/(1d + Math.exp(-value));
                        gradient = value * (1d - value);
                        break;
                    case SOFTPLUS:
                        temp = Math.exp(value);
                        value = Math.log1p(temp);
                        gradient = temp / (temp + 1d);
                        break;
                    case SHIFTSOFT:
                        temp = Math.exp(value);
                        value = Math.log1p(temp) - 1d;
                        gradient = (temp) / (temp + 1d);
                        break;
                }
                row.set(r, value);
                activationGradients.instance(i).set(r, gradient);
            }
        }
        return result;
    }
    
    public final Matrix reform(final Matrix errSignals) {
        return super.transpose().process(interperate(errSignals));
    }
    
    private Matrix interperate(final Matrix errSignals) {
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
        
        weightGradients = null;
    }
    
}
