/*
 * Copyright (C) 2020 mijitr <MijitR.xyz>
 *
 * Dis How Ballers Do...
 */
package MijitGroup.Workspace;

import MijitGroup.Workspace.Networks.FullyConnected.NillerNet.Activation;
import static MijitGroup.Workspace.Networks.FullyConnected.NillerNet.Activation.*;
import MijitGroup.Workspace.Networks.FullyConnected.NillerNet.NillerNet;
import com.zavtech.morpheus.frame.DataFrame;
import com.zavtech.morpheus.frame.DataFrameRow;
import com.zavtech.morpheus.frame.DataFrameSource;
import com.zavtech.morpheus.index.Index;
import com.zavtech.morpheus.index.IndexException;
import com.zavtech.morpheus.range.Range;
import com.zavtech.morpheus.util.Collect;
import com.zavtech.morpheus.yahoo.YahooQuoteHistorySource;
import com.zavtech.morpheus.yahoo.YahooReturnSource;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 *
 * @author mijitr <MijitR.xyz>
 */
public class NewBeginnings {
    
    private static final Random RAND;
    
    private static final int NUM_INPUTS,  NUM_OUTPUTS;
    
    static {
        RAND = new Random();
        
        NUM_INPUTS = 25;
        NUM_OUTPUTS = 3;
        
        DataFrameSource.register(new YahooReturnSource());
        DataFrameSource.register(new YahooQuoteHistorySource());
    }
    
    public static final void main(final String[] args) {
        final NewBeginnings me = new NewBeginnings();
        
        me.test();
    }
    
    private final int[] sizes = new int[]{ 7 , 13 , NUM_OUTPUTS };
    
    private final Activation[] acts = new Activation[]
        { ELU , ELU , TANH }
    ;
    
    private final NillerNet myNet = new NillerNet(NUM_INPUTS, sizes, acts);
    
    final void test() {
        
        /*final DataFrame dataFrame
                = build("TSLALONGTERMDAILY");
        
        final List<DataFrame<LocalDate, String>> frameGroup =
                splitLinearGrowing(dataFrame);
        */
        
        /*
        
        
        
        
        */
        
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
        
        final double[][][] testDats = new double[100][8][25];
        final double[][][] testTargets = new double[100][8][3];
        for(int in = 0; in < testDats.length; in ++) {
            for(int bat = 0; bat < testDats[in].length; bat ++) {
                for(int val = 0; val < testDats[in][bat].length; val ++) {
                    testDats[in][bat][val] = RAND.nextDouble();
                }
                for(int t = 0; t < testTargets[in][bat].length; t ++) {
                    testTargets[in][bat][t] = 2d*((t+bat+in)%2) - 1d;
                }
            }
        }
        
        this.trainEpocs(dats, targets, testDats, testTargets);
        
    }
    
    public final DataFrame<LocalDate, String> build(final String dTarget) {
        final Index<LocalDate> dayteRange = Range.of(LocalDate.now().minusYears(20), LocalDate.now())
                .toIndex(LocalDate.class);
        
        final String datPath = "C:/users/rwhil/downloads/".concat(dTarget).concat(".csv");
        final Set<String> columnSets = Collect.asSet("Close", "Open", "High","Low");
        final DataFrame<LocalDate, String> shitStack = DataFrame.read().csv(
                        options -> {
                            options.setResource(datPath);
                            options.setHeader(true);
                            options.setColNamePredicate(columnSets::contains);
                            options.setRowPredicate(v -> { return dayteRange.contains(LocalDate.parse(v[0]));});
                            options.setRowKeyParser(LocalDate.class, row -> LocalDate.parse(row[0]));
                            options.setParallel(true);
                        });
        shitStack.cols().add("targets", Double.class, v -> v.row().stats().mean());
        return shitStack;
    }
    
    public final List<DataFrame<LocalDate, String>> maintain(final DataFrame<LocalDate, String> dF) {
        final List<DataFrame<LocalDate,String>> trainGroup = new LinkedList<>();
        trainGroup.add(dF);
        return trainGroup;
    }
    
    public final List<DataFrame<LocalDate, String>> splitLinearGrowing(final DataFrame<LocalDate, String> dF) {
        final List<DataFrame<LocalDate,String>> trainGroup = new LinkedList<>();
        
        final List<int[]> indexGroups = generateInputIndices(dF.rowCount());
        
        for(final int[] group : indexGroups) {
            final DataFrame<LocalDate, String>
                    groupedFrame = DataFrame.of(LocalDate.class, String.class);
            
            groupedFrame.cols().add("targets", Double.class);
            for(final int row : group) {
                if(row < dF.rowCount()) {
                    final DataFrameRow<LocalDate, String> frameRow
                            = dF.rowAt(dF.rowCount() - row);
                    groupedFrame.rows().add(frameRow.key(), v -> Double.parseDouble(frameRow.getValue("targets").toString()));
                }
            }
            trainGroup.add(groupedFrame);
        }
        
        return trainGroup; 
    }
    
    /*public final List<DataFrame<LocalDate, String>> sLG(final DataFrame<LocalDate, String> dF) {
        final List<DataFrame<LocalDate,String>> trainGroup = new LinkedList<>();
        
        generateIndices(
                dF.rowCount()
        ).stream()
        .forEach(
                group -> {
                    Arrays.stream(
                            group
                    ).forEach(row -> {
                            groupedFrame.rows().add(dF.rowAt(dF.rowCount() - row).key(), v-> Double.parseDouble(dF.rowAt(dF.rowCount()-row).getValue("targets").toString()));
                        }
                    );
                }
        );
        
        return trainGroup; 
    }*/
    
    public final ArrayList<int[]> generateIndices(final int rowCount) {
        final ArrayList<int[]> indexGroups = new ArrayList<>();
        int lastIndex = 0;
        for(int i = 1; lastIndex + 1 < rowCount; i ++) {
            final int[] indexGroup = new int[i+1];
            for(int r = lastIndex + 1, p = 0; r <= lastIndex+1+i; r ++, p ++) {
                indexGroup[p] = r;
            }
            indexGroups.add(indexGroup);
            lastIndex = lastIndex + 1 + i;
        }
        return indexGroups;
    }
    
    public final ArrayList<int[]> generateSpacedIndices(final int rowCount) {
        final ArrayList<int[]> indexGroups = new ArrayList<>();
        int lastIndex = 0;
        for(int i = 0; lastIndex + 1 < rowCount; i ++) {
            final int[] indexGroup = new int[NUM_INPUTS*(i+1)];
            for(int r = (lastIndex), p = 0; p < indexGroup.length; r ++, p ++) {
                indexGroup[p] = r;
            }
            indexGroups.add(indexGroup);
            lastIndex += NUM_INPUTS*(i+1);
        }
        return indexGroups;
    }
    
    public final ArrayList<int[]> generateInputIndices(final int rowCount) {
        final ArrayList<int[]> indexGroups = new ArrayList<>();
        int lastIndex = 0;
        for(int i = 0; lastIndex + 1 < rowCount; i ++) {
            final int[] indexGroup = new int[i+1+NUM_INPUTS];
            for(int r = lastIndex, p = 0; p < indexGroup.length; r ++, p ++) {
                indexGroup[p] = r;
            }
            indexGroups.add(indexGroup);
            lastIndex = lastIndex + 1 + i + NUM_INPUTS;
        }
        return indexGroups;
    }
    
    public final void trainEpocs(final double[][][] trainData, final double[][][] trainTargets,
            final double[][][] testData, final double[][][] testTargets)
    {
        double err = Double.MAX_VALUE;
        for(int epoc = 0; err > 0.001d; epoc ++) {
            final double trainErr
                    = this.trainNet(trainData, trainTargets);
            final double testErr
                    = this.testNet(testData, testTargets);
            
            err = testErr;
            if(epoc % 2 == 0) {
                System.out.println(String.format("%f train -***- %f test", trainErr, testErr));
            }
        }
    }
    
    public final double trainNet(final double[][][] dat, final double[][][] targets) {
        double runningErr = 0d;
        final List<Integer> indexList = new ArrayList<>();
        
        for(int instance = 0; instance < dat.length; instance ++) {
            indexList.add(instance);
        } Collections.shuffle(indexList);
        for(final int instance : indexList) {
            myNet.process(dat[instance]);
            myNet.train(targets[instance]);
            runningErr += myNet.recentError();
            myNet.updateWithMomentum();
        }
        return runningErr / (double) dat.length;
    }
    
    public final double testNet(final double[][][] dat, final double[][][] targets) {
        double runningErr = 0d;
        for(int instance = 0; instance < dat.length; instance ++) {
            myNet.process(dat[instance]);
            runningErr += myNet.whatError(targets[instance]);
        }
        return runningErr / (double) dat.length;
    }
    
    //
        ///
            ////
}               /////
