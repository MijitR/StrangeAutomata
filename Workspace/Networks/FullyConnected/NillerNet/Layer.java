/*
 * Copyright (C) 2020 mijitr <MijitR.xyz>
 *
 * Dis How Ballers Do...
 */
package MijitGroup.Workspace.Networks.FullyConnected.NillerNet;

import MijitGroup.Workspace.Math.Matrix;
import MijitGroup.Workspace.Math.Vector;
import MijitGroup.Workspace.Math.MAJOR;

/**
 *
 * @author mijitr <MijitR.xyz>
 */
public class Layer extends Matrix {
    
    static final double BASE_ETA, BASE_MOMENTUM,
            GRADIENT_CLIP;
    
    static {
        BASE_ETA = 0.0001d;
        BASE_MOMENTUM = 0.9d;
    
        GRADIENT_CLIP = 1.0d;
    }
    
    private final Activation activation;
    
    private final Matrix weightVelocities;
    
    private Matrix input, activationGradients, weightGradients;
    
    public Layer(final int inputCount, final int neuronCount, final Activation activation) {
        super(MAJOR.COLUMN, inputCount + 1, neuronCount);
        this.activation = activation;
        this.weightVelocities = new Matrix(MAJOR.COLUMN, inputCount + 1, neuronCount);
    }
    
    public final void initRand() {
        for(int i = 0; i < super.vectorCount(); i ++) {
            super.instance(i).initScaledRand(super.vectorCount());
        }
    }
    
    public final Matrix transform(final BiasedMatrix inputField) {
        this.input = inputField;
        return this.activate(super.process(inputField));
    }
    
    private Matrix activate(final Matrix result) {
        activationGradients = new Matrix(MAJOR.ROW,
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
                    case IDENTITY:
                        gradient = 1d;
                        break;
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
                    case SWISH:
                        final double g = 1d/(1d+Math.exp(-value)),
                                dv = 1d, dg = g*(1d-g);
                        gradient = value*dg + g*dv;
                        value = value * g;
                        break;
                    case ARCTAN:
                        gradient = 1d/(value*value+1d);
                        value = Math.atan(value);
                        break;
                    case BENTIDENTITY:
                        gradient = (value / (2d*Math.sqrt(value*value+1d)))+1d;
                        value = (Math.sqrt(value*value+1d)-1d)/2d + value;
                        break;
                    case SOFTSIGN:
                        gradient = 1d / Math.pow(1d + Math.abs(value),2d);
                        value = value / (1d + Math.abs(value));
                        break;
                    case SINUSOID:
                        gradient = Math.cos(value);
                        value = Math.sin(value);
                        break;
                    case GAUSSIAN:
                        gradient = -2d*value*Math.exp(-1d*value*value);
                        value = Math.exp(-1d*value*value);
                        break;
                    case LEAKYRELU:
                        gradient = Double.compare(value, 0d) > 0d ? 1d : 0.03d;
                        value = Math.max(0.03d*value, value);
                        break;
                    case TWICEMOID:
                        value = 1d/(1d+Math.exp(-value));
                        gradient = 2d*value*(1d-value);
                        value *= 2d;
                        break;
                    case ELU:
                        value = Double.compare(value, 0d) >= 0 ? value :
                                Math.exp(value) - 1;
                        gradient = Double.compare(value, 0d) >= 0 ? 1d :
                                value + 1d;
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
    
    private Matrix interperate(final Matrix errSignals) {
        final Matrix nodeDelta = errSignals.matrixScale(activationGradients);
        nodeDelta.clip(GRADIENT_CLIP);
        weightGradients = nodeDelta.process(input.transpose()); 
        return nodeDelta;
    }
    
    public final void update() {
        if(weightGradients == null) {
            return;
        }
        age ++;
        
        weightGradients.scale(BASE_ETA);
        
        super.lose(weightGradients);
        
        weightGradients = null;
    }
    
    long age = 0l;
    public final void updateWithMomentum() {
        if(weightGradients == null) {
            return;
        }
        age ++;
        
        weightVelocities.scale((1d)*BASE_MOMENTUM);
        weightGradients.scale((1d-BASE_MOMENTUM));
        
        weightVelocities.lose(weightGradients);
        
        for(int c = 0; c < weightVelocities.vectorCount(); c ++) {
            super.instance(c).elementAdd(weightVelocities.instance(c).popScale(BASE_ETA));
        }
        
        weightGradients = null;
    }
    
}
