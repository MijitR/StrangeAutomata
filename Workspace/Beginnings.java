package MijitGroup.Workspace;

import MijitGroup.Workspace.BoxPopper.BoxPopper;
import MijitGroup.Workspace.Math.Matrix;
import MijitGroup.Workspace.Math.ORIENT_MAJOR;
import MijitGroup.Workspace.Networks.FullyConnected.NillerNet.Activation;
import static MijitGroup.Workspace.Networks.FullyConnected.NillerNet.Activation.*;
import MijitGroup.Workspace.Networks.FullyConnected.NillerNet.NillerNet;
import java.util.Random;

/**
 *
 * @author Mijitr <Mijitr.xyz>
 */
public class Beginnings {

    private static final Random RAND;
    
    static {
        RAND = new Random();
    }
    
    public static void main(String[] args) {
        final double[][] dat = new double[][]{
            new double[]{3,2,1},
            new double[]{5,6,7},
            new double[]{2,2,2},
            new double[]{7,9,1}
        };
        
        final double[][][] dats = new double[100][8][25];
        final double[][][] targets = new double[100][8][3];
        for(int in = 0; in < dats.length; in ++) {
            for(int bat = 0; bat < dats[in].length; bat ++) {
                for(int val = 0; val < dats[in][bat].length; val ++) {
                    dats[in][bat][val] = RAND.nextDouble();
                }
                for(int t = 0; t < targets[in][bat].length; t ++) {
                    targets[in][bat][t] = 2d*((t+bat+in)%2) - 1d;
                }
            }
        }
        
        final int[] layerSizes = new int[]{25, 25, 3};
        final Activation[] activations = new Activation[]
            {TANH, SOFTPLUS, TANH};
        final NillerNet network = new NillerNet(25, layerSizes, activations);
        
        for(int i = 0; i < 50000; i ++) {
            final Matrix results = network.process(dats[i%dats.length]);
            

            //System.out.println(results.instance(0));
            //System.out.println("\n\n");
            //results.transpose().print();
            //System.out.println(results.transpose().instance(0));
            final Matrix influences
                    = network.trainInstant(targets[i%dats.length]);
            //System.out.println("INFLUENCES");
            //influences.print();

        }
        
        System.out.println("Training Inputs:");
        new Matrix(ORIENT_MAJOR.ROW, dats[10]).print();
        System.out.println("RESULTS");
        network.process(dats[10]).print();
        System.out.println("TARGETS");
        new Matrix(ORIENT_MAJOR.ROW, targets[10]).print();
        
        
        double[][] test = new double[1][25];
        for(int r = 0; r < test.length; r ++) {
            for(int c = 0; c < test[r].length; c ++) {
                test[r][c] = RAND.nextDouble();
            }
        }
        System.out.println("Test Input:");
        new Matrix(ORIENT_MAJOR.ROW, test).print();
        System.out.println("Test Results:");
        network.process(test).print();
        
        /*final Matrix rowMaj = new Matrix(ORIENT_MAJOR.ROW, dat);
        rowMaj.print();
        System.out.println(rowMaj.instance(0));
        final Matrix colMaj = new Matrix(ORIENT_MAJOR.COLUMN, dat);
        colMaj.print();
        System.out.println(colMaj.instance(0));*/
        //rowMaj.transpose().process(rowMaj).print();
        /*final double[][] dat2 = new double[4][6];
        final double[][] dat3 = new double[6][2];
        for(int r = 0; r < dat2.length; r ++) {
            for(int c = 0; c < dat2[r].length; c ++) {
                dat2[r][c] = r * dat2[r].length + c;
            }
        }for(int r = 0; r < dat3.length; r ++) {
            for(int c = 0; c < dat3[r].length; c ++) {
                dat3[r][c] = 11 - r * dat3[r].length + c;
            }
        }
        
        
        //final Matrix rowMaj = new Matrix(ORIENT_MAJOR.ROW, dat);
        //final Matrix colMaj = new Matrix(ORIENT_MAJOR.COLUMN, dat);
       
        //rowMaj.print();
        //colMaj.print();
        
        final Matrix rowMaj2 = new Matrix(ORIENT_MAJOR.ROW, dat2);
        final Matrix colMaj2 = new Matrix(ORIENT_MAJOR.COLUMN, dat3);
        
        final Matrix rowMaj3 = new Matrix(ORIENT_MAJOR.ROW, 8, 4);
        final Matrix colMaj3 = new Matrix(ORIENT_MAJOR.COLUMN, 4, 8);
        
        rowMaj2.print();
        colMaj2.print();
        
        //rowMaj3.print();
        //colMaj3.print();
        
        colMaj2.process(rowMaj2).print();
        colMaj3.process(rowMaj3).print();*/
    }
    
}
