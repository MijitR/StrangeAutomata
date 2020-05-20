/*
 * Copyright (C) 2020 mijitr <MijitR.xyz>
 *
 * Dis How Ballers Do...
 */
package MijitGroup.Workspace;

import MijitGroup.Workspace.Math.Matrix;
import MijitGroup.Workspace.Math.ORIENT_MAJOR;
import MijitGroup.Workspace.Networks.FullyConnected.NillerNet.Activation;
import static MijitGroup.Workspace.Networks.FullyConnected.NillerNet.Activation.RELU;
import static MijitGroup.Workspace.Networks.FullyConnected.NillerNet.Activation.SHIFTSOFT;
import static MijitGroup.Workspace.Networks.FullyConnected.NillerNet.Activation.SOFTPLUS;
import static MijitGroup.Workspace.Networks.FullyConnected.NillerNet.Activation.TANH;
import MijitGroup.Workspace.Networks.FullyConnected.NillerNet.NillerNet;
import java.util.Random;

/**
 *
 * @author mijitr <MijitR.xyz>
 */
public class Middles {
    
    private static final Random RAND;
    
    private static final int NUM_INPUTS;
    
    static {
        RAND = new Random();
        
        NUM_INPUTS = 4;
    }
    
    public static final void main(final String[] args) {
        
        final int[] sizes = new int[]{ 15 , 17 , 33,  4 };
        final Activation[] acts = new Activation[]
            { TANH , SHIFTSOFT , TANH , TANH }
        ;
        
        
        final NillerNet myNet = new NillerNet(NUM_INPUTS, sizes, acts);
        
        int miniBatchSize = 5, miniBatchCount = 1500;
        
        final double[][][] inputs = new double[miniBatchCount][miniBatchSize][NUM_INPUTS];
        final double[][][] targets = new double[miniBatchCount][miniBatchSize][4];
        
        final double[][] tests = new double[9][NUM_INPUTS];
        final double[][] testAnswers = new double[9][4];
        
        for(int b = 0; b < miniBatchCount; b ++) {
            Middles.fill(inputs[b]);
            Middles.classify(inputs[b], targets[b]);
        }
        
        Middles.fill(tests);
        Middles.classify(tests, testAnswers);
        
        for(int epoc = 0; epoc < 300; epoc ++) {
            for(int b = 0; b < miniBatchCount; b ++) {
                final Matrix result = myNet.process(inputs[b]);
                if(b % 1499 == 0 && epoc % 5 == 0) {
                    result.print();
                    new Matrix(ORIENT_MAJOR.ROW, targets[b]).print();
                    System.out.println("\\\\\\\\\\\\");
                    System.out.println("\\\\\\\\\\\\");
                }
                myNet.trainInstant(targets[b]);
            }
        }
        
        System.out.println("Testing input minibatch::");
        System.out.println("Given an object whose features correspond\n"
                + "\tto the traits represented by (ie embedded as) each row vector\n\t"
                + "of independantly scalable values"
                + "\n\n\tImagine the first column is a normalized measurement of"
                + "\\tsomething like tire pressure, the next column perhaps could be\n\t"
                + "along the lines of ratio to maximum steering wheel size\n\nOf course, this example works slightly differently.\n");
        System.out.println("Read ObjXFeature as RowXCol");
        new Matrix(ORIENT_MAJOR.ROW, tests).print();
        System.out.println("Target: (row x col => obj x feature");
        //new Matrix(ORIENT_MAJOR.ROW, testAnswers).print();
        Middles.printChoices(testAnswers);
        
        System.out.println("Results per object queeried");
        Middles.printChoices(myNet.process(tests).ripDat());
        
    }
    
    public static final void fill(final double[][] traits) {
        for(int sample = 0; sample < traits.length; sample ++) {
            for(int feature = 0; feature < traits[sample].length; feature ++) {
                traits[sample][feature] = RAND.nextDouble();
            }
        }
    }
    
    public static final void classify(final double[][] traits, final double[][] targets) {
        boolean redvBlue,
                dogvCar,
                oldvAncient,
                slowvFast;
        
        for(int sample = 0; sample < traits.length; sample ++) {
            redvBlue = (Double.compare(traits[sample][0]*traits[sample][3], 0.25d) > 0);
            dogvCar = Double.compare(traits[sample][1]-traits[sample][3], 0d)  > 0;
            oldvAncient = Double.compare(traits[sample][2]+traits[sample][3], 1d) > 0;
            slowvFast = Double.compare(traits[sample][3], 0.5d) > 0;
            
            redvBlue = dogvCar & redvBlue || slowvFast;
            slowvFast = slowvFast & oldvAncient || dogvCar;
            
            targets[sample][0] = redvBlue ? 1d : -1d;
            targets[sample][1] = dogvCar ? 1d : -1d;
            targets[sample][2] = oldvAncient ? 1d : -1d;
            targets[sample][3] = slowvFast? 1d : -1d;
        }
    }
    
    public static final double[][] toChoices(final double[][] rawDat) {
        final double[][]  choices = (double[][]) rawDat.clone();
        for(int i = 0; i < rawDat.length; i ++) {
            for(int j = 0; j < rawDat[i].length; j ++) {
                choices[i][j] = Double.compare(rawDat[i][j], 0d) > 0 ? 1d : -1d;
            }
        }
        return choices;
    }
    
    public static final String[][] toStringChoices(final double [][] rawDat) {
        final double[][]  choices = Middles.toChoices(rawDat);
        
        boolean redvBlue, k1,
                dogvCar, k2,
                oldvAncient, k3,
                slowvFast, k4;
        
        final String[][] targets = new String[rawDat.length][4];
        
        for(int sample = 0; sample < choices.length; sample ++) {
            redvBlue = (Double.compare(choices[sample][0], 0.0d) > 0);
            dogvCar = Double.compare(choices[sample][1], 0.0d)  > 0;
            oldvAncient = Double.compare(choices[sample][2], 0.0d) > 0;
            slowvFast = Double.compare(choices[sample][3], 0.d) > 0d;
            
            targets[sample][0] = redvBlue ?  "  Red " : " Blue ";
            targets[sample][1] = oldvAncient ? "   Fresh " : " Ancient ";
            targets[sample][2] = slowvFast? " Deliberate  ": "       Fast  ";
            targets[sample][3] = dogvCar ? " Spyder " : "  Coupe ";
        }
        
        return targets;
    }
    
    public static final void printChoices(final double[][] rawOutput) {
        final StringBuilder builder = new StringBuilder("Choosing\n");
        final String[][] choices = Middles.toStringChoices(rawOutput);
        for(int sample = 0; sample < rawOutput.length; sample ++) {
            builder.append("[ :");
            for(int feature = 0; feature < rawOutput[sample].length; feature ++) {
                builder.append(choices[sample][feature]).append(feature < rawOutput[sample].length - 1 ? ":" : "]\n");
            }
        }
        System.out.println(builder.toString());
    }
    
}
    
