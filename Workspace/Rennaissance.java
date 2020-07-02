/*
 * Copyright (C) 2020 mijitr <MijitR.xyz>
 *
 * Dis How Ballers Do...
 */
package MijitGroup.Workspace;

import MijitGroup.Workspace.Functions.VectorizeMe;
import MijitGroup.Workspace.Math.Matrix;
import MijitGroup.Workspace.Math.MAJOR;
import MijitGroup.Workspace.Networks.FullyConnected.NillerNet.Activation;
import static MijitGroup.Workspace.Networks.FullyConnected.NillerNet.Activation.*;
import MijitGroup.Workspace.Networks.FullyConnected.NillerNet.NillerNet;
import com.zavtech.morpheus.array.Array;
import com.zavtech.morpheus.frame.DataFrame;
import com.zavtech.morpheus.frame.DataFrameColumn;
import com.zavtech.morpheus.frame.DataFrameColumns;
import com.zavtech.morpheus.frame.DataFrameFactory;
import com.zavtech.morpheus.frame.DataFrameRows;
import com.zavtech.morpheus.frame.DataFrameSource;
import com.zavtech.morpheus.index.Index;
import com.zavtech.morpheus.range.Range;
import com.zavtech.morpheus.util.Collect;
import com.zavtech.morpheus.viz.chart.Chart;
import com.zavtech.morpheus.yahoo.YahooField;
import com.zavtech.morpheus.yahoo.YahooQuoteHistorySource;
import com.zavtech.morpheus.yahoo.YahooReturnSource;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;

/**
 *
 * @author mijitr <MijitR.xyz>
 */
public class Rennaissance {
    
    private static final int NUM_INPUTS,  NUM_OUTPUTS;
    
    static {
        NUM_INPUTS = 169;
        NUM_OUTPUTS = 1;
        
        DataFrameSource.register(new YahooReturnSource());
        DataFrameSource.register(new YahooQuoteHistorySource());
    }
    
    public static final void main(final String[] args) {
        final Rennaissance me = new Rennaissance();
        //me.age();
        //me.golden();
        me.fullPlatinum();
    }
    
    private final String alphabet  = "abcdefghijklmnopqrstuvwxyz ";
    
    private final double[] lookupTable = new double[alphabet.length()];
    
    private final int[] sizes = new int[]{ 9 , 7 , NUM_OUTPUTS };
    private final Activation[] acts = new Activation[]
                { ARCTAN , ELU , IDENTITY }
    ;
    
    private final NillerNet myNet = new NillerNet(NUM_INPUTS, sizes , acts);
    
    private Rennaissance() {
        
    }
    
    final void age() {
        
        final String ticker = "AMD";
        
        final YahooQuoteHistorySource source = DataFrameSource.lookup(YahooQuoteHistorySource.class);
        
        final DataFrame<LocalDate, YahooField> frame = source.read(options -> {
                options.withStartDate(LocalDate.now().minusYears(10));
                options.withEndDate(LocalDate.now());
                options.withTicker(ticker);
                options.withDividendAdjusted(false);
            }
        ).cols().select(Array.of(YahooField.PX_CLOSE, YahooField.PX_OPEN));
        
        Chart.create().asSwing().withLinePlot(frame, chart -> {
            chart.plot().axes().domain().label().withText("Date");
            chart.plot().axes().range(0).label().withText("Close Price");
            chart.plot().axes().range(1).label().withText("Open Price");
            chart.title().withText(ticker + ": Prices (10 years)");
            chart.legend().off();
            chart.show();
        });
        
    }
    
    public final void golden() {
        final String ticker = "AMD";
        final YahooReturnSource source = DataFrameSource.lookup(YahooReturnSource.class);
        final Array<Integer> halfLives = Array.of(0, 8);
        final LocalDate startDayte = LocalDate.now().minusYears(10);
        final LocalDate handlebarsDayte = LocalDate.now().minusYears(1);
        final DataFrame<LocalDate,String> frame = DataFrame.combineFirst(halfLives.map(halfLife -> {
            return source.read(options -> {
                options.withStartDate(startDayte);
                options.withEndDate(LocalDate.now());
                options.withTickers(ticker);
                options.withEmaHalfLife(halfLife.getInt());
                options.cumulative();
            }).cols().replaceKey(ticker, String.format("%s(%s)", ticker, halfLife.getInt()));
        }));
        
        final Range<LocalDate> blindDaytes = Range.of(handlebarsDayte, LocalDate.now());
        
        for(int epoc = 0; epoc < 100; epoc ++) {

            if(epoc % 3 == 0) {
                Chart.create().withLinePlot(frame.applyDoubles(v -> v.getDouble() * 100d), chart -> {
                    chart.title().withText(String.format("%s EMA Smoothed Returns With Various Half-Lives", ticker));
                    chart.plot().axes().domain().label().withText("Date");
                    chart.plot().axes().range(0).label().withText("Return");
                    chart.plot().axes().range(0).format().withPattern("##'%';-##'%'");
                    chart.legend().on().bottom();
                    chart.show();
                });
            }
            
        }
    
    }
    
    public final void fullPlatinum() {
        
        final int bonusDays = 500;
        
        final Index<LocalDate> dayteRange
                = Range.of(LocalDate.now().minusYears(50), LocalDate.now()).toIndex(LocalDate.class);
        final Index<LocalDate> blindDaytes
                = Range.of(LocalDate.now().minusMonths(18), LocalDate.now()).toIndex(LocalDate.class);
        final Index<LocalDate> dreamDaytes
                = Range.of(LocalDate.now(), LocalDate.now().plusDays(bonusDays)).toIndex(LocalDate.class);
        
        final String datPath = "C:/users/rwhil/downloads/AMDLONGTERMDAILY.csv";
        final Set<String> columnSets = Collect.asSet("Close", "Open", "High","Low");
        final DataFrame<LocalDate, String> shitStack
                = DataFrame.read().csv(
                        options -> {
                            options.setResource(datPath);
                            options.setHeader(true);
                            options.setColNamePredicate(columnSets::contains);
                            options.setRowPredicate(v -> { return dayteRange.contains(LocalDate.parse(v[0]));});
                            options.setRowKeyParser(LocalDate.class, row -> LocalDate.parse(row[0]));
                            options.setParallel(true);
                        });
        
        for(final LocalDate dayte : dreamDaytes) {
            shitStack.rows().add(dayte, v -> Double.POSITIVE_INFINITY);
        }
        shitStack.cols().add("targets", Double.class, v -> v.row().stats().mean());
        shitStack.cols().add("training guesses",Double.class);
        shitStack.cols().add("free guesses", Double.class);
        
        final DataFrameColumn<LocalDate, String> targets = shitStack.col("targets");
        final DataFrameColumn<LocalDate, String> guesses = shitStack.col("training guesses");
        final DataFrameColumn<LocalDate, String> freeGuesses = shitStack.col("free guesses");
        
        final int testDayte = shitStack.rows().ordinalOf(targets.rowKeys().filter(blindDaytes::contains).findFirst().get());
        
        final Chart c =Chart.create().asSwing().withLinePlot(shitStack.right(3), chart -> {
            chart.title().withText("STOCK STUFF");
            chart.subtitle().withText("Featuring MeanLine(R) - The Most Average Thing Out There(TM)");
            chart.plot().axes().domain().label().withText("Day");
            chart.plot().axes().range(0).label().withText("Prices DAY AVERAGE (Open, Close, High, Low)");
            chart.plot().axes().range(0).format().withPattern("###.##'$';###.##'%'");
            chart.legend().on().bottom();
            chart.show();
        });
        c.theme().dark();
        
        final double maxPrice = targets.stats().max() / 1.1d;
        
        final HashMap<List<Integer>, double[][][]> trainingDat = buildBatchSamplesNTargets
            (
                    targets, maxPrice, bonusDays
            );
        
        final double[][] testDat = buildBatchSamplesNInputs(targets, maxPrice, bonusDays);
        
        
        try {
            Thread.sleep(700);

            long epoc = 0l;
            do {
                //predict(guesses, targets, testDayte);
                final double avgErr
                       //improvedPredict(guesses, targets, testDayte, bonusDays);
                       //=  improvedBatchPredict(guesses, targets, testDayte, bonusDays+1);
                      = improvedBatchPredict(trainingDat, bonusDays, guesses, targets, maxPrice);
                
               this.test(testDat, bonusDays, freeGuesses, maxPrice);
                //if((epoc ++) % 1 == 0) {
                //    System.out.println(avgErr);
                //}
                /* if(epoc < 30) {
                Thread.sleep(10);
                }*/
            } while(epoc < 55000);
        } catch(final InterruptedException e) {
            
        }
        
    }
    
    private HashMap<List<Integer>, double[][][]> buildBatchSamplesNTargets(
            final DataFrameColumn<? extends LocalDate, ? extends String> targets,
                final double maxPrice, final int bonusDays) {
        
        final HashMap<List<Integer>, double[][][]> trainingDat
                = new HashMap<>();
        
        final int dataSize = targets.size() - bonusDays;
        
        //cannot change yet
        final int miniBatchCount = 2;
        
        final double[][][] indiSamples = new double[miniBatchCount][dataSize][NUM_INPUTS];
        final double[][][] indiTargets = new double[miniBatchCount][dataSize][NUM_OUTPUTS];
        
        final double[][][] workingInputs = new double[miniBatchCount][dataSize-NUM_INPUTS+1][NUM_INPUTS];
        final double[][][] workingTargets = new double[miniBatchCount][dataSize-NUM_INPUTS+1][NUM_OUTPUTS];
        
        final double lastDat = targets.getDouble(0) / maxPrice;
        
        final List<Integer>[] indices = new ArrayList[miniBatchCount];
       
        for(int mB = 0; mB < miniBatchCount; mB ++) {
            indices[mB] = new ArrayList<>();
            
            indiSamples[mB][0][indiSamples[0][0].length-1] = lastDat;
            indiTargets[mB][0][0] = targets.getDouble(1) / maxPrice;
            indices[mB].add(0);
            for(int s = 1; s < dataSize; s ++) {
                final double[] sample = indiSamples[mB][s];
                final double[] aims = indiTargets[mB][s];
                for(int i = 0; i < sample.length - 1; i ++) {
                    sample[i] = indiSamples[mB][s-1][i+1];
                } sample[NUM_INPUTS-1] = indiTargets[mB][s-1][0];
                aims[0] = targets.getDouble(s+1) / maxPrice;
                if(s >= NUM_INPUTS) {
                    indices[mB].add(s - NUM_INPUTS);
                    System.arraycopy(indiSamples[mB][s], 0, workingInputs[mB][s-NUM_INPUTS], 0, NUM_INPUTS);
                    System.arraycopy(indiTargets[mB][s], 0, workingTargets[mB][s-NUM_INPUTS], 0, NUM_OUTPUTS);
                }
            }
           
           /* if(mB == 0) {
                final double[][][] testingSamples = new double[bonusDays  + 1][1][NUM_INPUTS];
                testingSamples[0][0] = Arrays.copyOf(indiSamples[mB][indices[mB].get(indices[mB].size()-1)], NUM_INPUTS);
                for(int i = 0; i < NUM_INPUTS - 1; i ++) {
                    testingSamples[0][0][i] = testingSamples[0][0][i+1];
                } testingSamples[0][0][NUM_INPUTS-1] = indiTargets[mB][indices[mB].get(indices[mB].size()-1)][0];
                System.out.println(Arrays.toString(testingSamples[0][0]));
                trainingDat.put(new LinkedList<>(), testingSamples);
            }*/
            
            Collections.shuffle(indices[mB]);
            VectorizeMe.mixRows(indices[mB], workingInputs[mB]);
            VectorizeMe.mixRows(indices[mB], workingTargets[mB]);
            
        }
        
        for(int mB = 0; mB < miniBatchCount; mB ++) {
            trainingDat.put(indices[mB], new double[][][]{workingInputs[mB], workingTargets[mB]});
        }
        
        return trainingDat;
    }
    
    private double[][] buildBatchSamplesNInputs(final DataFrameColumn<LocalDate, String> targets,
            final double maxPrice, final int bonusDays) {
        
        final int dataSize = targets.size();
        final double[][] inputDat = new double[dataSize][NUM_INPUTS];
        inputDat[0][NUM_INPUTS-1] = targets.getDouble(0) / maxPrice;
        
        for(int s = 1; s < dataSize; s ++) {
            final double[] sample = inputDat[s];
            for(int i = 0; i < sample.length - 1; i ++) {
                sample[i] = inputDat[s-1][i+1];
            } sample[sample.length-1] = targets.getDouble(s) / maxPrice;
        }
        
        return inputDat;
    }
    
    public final void test(final double[][] testDat, final int bonusDays,
            final DataFrameColumn<LocalDate,String> column, final double maxPrice) {
        final int dataSize = testDat.length - bonusDays;
        double result = Double.NaN;
        for(int s = 0; s < dataSize; s ++) {
            final Matrix results = myNet.process(new double[][]{testDat[s]});
            result = results.instance(0).get(0);
            column.setDouble(s, result * maxPrice);
        }
        for(int s = dataSize; s < testDat.length; s ++) {
            testDat[s][NUM_INPUTS - 1] = result;
            for(int a = s + 1, i = 1; a < testDat.length && i < testDat[a].length; a ++, i ++) {
                testDat[a][NUM_INPUTS - 1 - i] = result;
            }
            result = myNet.process(new double[][]{testDat[s]}).instance(0).get(0);
            column.setDouble(s, result * maxPrice);
        }
    }
    
    private double improvedBatchPredict(
            final HashMap<List<Integer>, double[][][]> trainingDat, final int bonusDays,
                final DataFrameColumn<? extends LocalDate, ? extends String> guesses,
            final DataFrameColumn<? extends LocalDate, ? extends String> targets,
                final double regularizationConstant)
    {
        
        age ++;
        
        double errSum = 0d;
        
        
        trainingDat.keySet().stream().parallel().forEach(indexList ->
            {this.trainList(trainingDat.get(indexList), bonusDays, guesses, targets, regularizationConstant, indexList);}
        );
        
        /*int instance = 0;
        for(final List<Integer> indexList : trainingDat.keySet()) {
            switch (indexList.getClass().getName()) {
                case "java.util.LinkedList":
                    final double[][][] testingSamples = trainingDat.get(indexList);
                    for(int f =1; f < bonusDays + 1; f ++) {
                        final double answer;
                        for(int i = 0; i < NUM_INPUTS - 1; i ++) {
                            testingSamples[f][0][i] =
                                    testingSamples[f-1][0][i + 1];
                        } testingSamples[f][0][NUM_INPUTS-1] = 
                                answer = myNet.process(testingSamples[f-1]).instance(0).get(0);
                        targets.setDouble(dataSize+f-1, answer * regularizationConstant);
                        guesses.setDouble(dataSize+f-1, answer * regularizationConstant);
                    }
                    break;
                case "java.util.ArrayList":
                    final Matrix mBResults = myNet.process(trainingDat.get(indexList)[0]);
                    myNet.train(trainingDat.get(indexList)[1]);
                    final double err = myNet.recentError();
                    if((instance ++) % 20 == 0) {
                        System.out.println(instance + ": " + err);
                    }
                    errSum += err;
                    myNet.updateWithMomentum();
                    
                    for(int z = 0; z < mBResults.vectorCount(); z ++) {
                        guesses.setDouble(indexList.get(z) + NUM_INPUTS, mBResults.instance(z).get(0) * regularizationConstant);
                    }
                    break;
            } 
        }*/        
        //for()
        return errSum / (double) (trainingDat.size() - 1);
    }
    
    private void trainList(
            final double[][][] testingSamples, final int bonusDays,
                final DataFrameColumn<? extends LocalDate, ? extends String> guesses,
            final DataFrameColumn<? extends LocalDate, ? extends String> targets,
                final double regularizationConstant, final List<Integer> indexList)
    {
        final int dataSize = targets.size() - 1 - bonusDays;
        switch (indexList.getClass().getName()) {
                case "java.util.LinkedList":
                    //final double[][][] testingSamples = trainingDat.get(indexList);
                    /*for(int f =1; f < bonusDays + 1; f ++) {
                        final double answer;
                        for(int i = 0; i < NUM_INPUTS - 1; i ++) {
                            testingSamples[f][0][i] =
                                    testingSamples[f-1][0][i + 1];
                        }
                        answer = myNet.process(testingSamples[f-1]).instance(0).get(0);
                        testingSamples[f][0][NUM_INPUTS-1] = answer;
                        targets.setDouble(dataSize+f-1, answer * regularizationConstant);
                        guesses.setDouble(dataSize+f-1, answer * regularizationConstant);
                    }*/
                    break;
                case "java.util.ArrayList":
                    final Matrix mBResults = myNet.process(testingSamples[0]);
                    myNet.train(testingSamples[1]);
                    myNet.updateWithMomentum();
                    final double err = myNet.recentError();
                    //if((instance ++) % 20 == 0) {
                        System.out.println(": " + err);
                    //}
                    //errSum += err;
                    
                    for(int z = 0; z < mBResults.vectorCount(); z ++) {
                        guesses.setDouble(indexList.get(z) + NUM_INPUTS, mBResults.instance(z).get(0) * regularizationConstant);
                    }
                    break;
        }
    }

    
    private double improvedPredict(final DataFrameColumn<? extends LocalDate, ? extends String> guesses,
            final DataFrameColumn<? extends LocalDate, ? extends String> targets,
                final int testingStartIndex, final int bonusDays) {
        age ++;
        final int dataSize = targets.size() - 1 - bonusDays;
        
        final double[][][] indiSamples = new double[dataSize][1][NUM_INPUTS];
        final double[][][] indiTargets = new double[dataSize][1][NUM_OUTPUTS];
        
        final double maxPrice = targets.stats().max();
        
        double lastDat = targets.getDouble(0) / maxPrice;
        indiSamples[0][0][indiSamples[0][0].length-1] = lastDat;
        indiTargets[0][0][0] = targets.getDouble(1) / maxPrice;
        
        final List<Integer> indices = new ArrayList<>();
        //indices.add(0);
        
        for(int s =1; s < dataSize; s ++) {
            final double[] sample = indiSamples[s][0];
            final double[] aims = indiTargets[s][0];
            for(int i = 0; i < sample.length - 1; i ++) {
                sample[i] = indiSamples[s-1][0][i + 1];
            } sample[sample.length-1] = indiTargets[s-1][0][0];
            aims[0] = targets.getDouble(s + 1) / maxPrice;
            if(s < testingStartIndex && s >= NUM_INPUTS) {
                indices.add(s);
            }
        } Collections.shuffle(indices);
        
        //double errSum = 0d;
        for(final int index : indices) {
            final double result = myNet.process(indiSamples[index]).instance(0).get(0) * maxPrice;
            guesses.setDouble(index, result);
            myNet.train(indiTargets[index]);
            //errSum += myNet.recentError();
            myNet.updateWithMomentum();
        }
        
        double errSum = 0d, runningResult = 0d;
        for(int index = testingStartIndex; index < dataSize; index ++) {
            runningResult = myNet.process(indiSamples[index]).instance(0).get(0);
            guesses.setDouble(index, runningResult * maxPrice);
            errSum += myNet.whatError(indiTargets[index]);
            //for(int correctionDex = index + 1, p = NUM_INPUTS - 1; correctionDex < dataSize && p >= 0; correctionDex ++, p --) {
            //   indiSamples[correctionDex][0][p] = runningResult;
            //}
        }
        
        final double[][] predictionSample = new double[1][NUM_INPUTS];
        System.arraycopy(indiSamples[dataSize - 1][0], 0, predictionSample[0], 0, NUM_INPUTS);
        for(int index = dataSize; index < dataSize + bonusDays; index ++) {
            for(int i = 0; i < NUM_INPUTS - 1; i ++) {
                predictionSample[0][i] = predictionSample[0][i+1];
            } predictionSample[0][NUM_INPUTS-1] = runningResult;
            runningResult = myNet.process(predictionSample).instance(0).get(0);
            targets.setDouble(index, runningResult * maxPrice);
            guesses.setDouble(index, runningResult * maxPrice);
        }
        
        return errSum / (double) (dataSize - testingStartIndex);
    }
    
    private double lastMax = Double.NEGATIVE_INFINITY;
    private long age = 0l;
    private double  predict(final DataFrameColumn<? extends LocalDate, ? extends String> guesses,
            final DataFrameColumn<? extends LocalDate, ? extends String> targets,
                final int testingStartIndex)
    {
        age ++;
        
        final double[][] netInputs = new double[testingStartIndex][NUM_INPUTS];
        final double[][] targetField = new double[testingStartIndex][NUM_OUTPUTS];
        
        final List<Integer> inputIndices = new ArrayList<>();
        final double maxPrice = targets.stats().max();
        for(int t = 0; t < testingStartIndex; t ++) {
            inputIndices.add(t);
            for(int i = 0; i < netInputs[t].length-1; i ++) {
                netInputs[t][i] = t == 0 ? 0d : netInputs[t-1][i+1];
            }
            netInputs[t][netInputs[t].length-1] = 0.8382d * targets.getDouble(t) / maxPrice;
            targetField[t][0] = 0.8382d * targets.getDouble(t+1) / maxPrice;
        }
        Collections.shuffle(inputIndices);
        
        VectorizeMe.mixRows(inputIndices, netInputs);
        VectorizeMe.mixRows(inputIndices, targetField);
        
        final Matrix batchResults = myNet.process(netInputs);
        myNet.train(targetField);
        myNet.updateWithMomentum();
        
        
        for(int z = 0; z < batchResults.vectorCount(); z ++) {
            guesses.setValue(inputIndices.get(z), batchResults.instance(z).get(0) * maxPrice / 0.8382d);
        }
        
        double errSum = 0d;
        final double[][] lastDat = new double[][]{netInputs[netInputs.length-1]};
        double runningAns = targetField[targetField.length-1][0] * 0.8382d / maxPrice;
        inputIndices.clear();
        
        for(int n = testingStartIndex; n < targets.size() - 1; n ++) {
            for(int i = 0; i < lastDat[0].length - 1; i ++) {
                lastDat[0][i] = lastDat[0][i + 1];
            }
            lastDat[0][lastDat[0].length - 1] = runningAns;
            runningAns = myNet.process(lastDat).instance(0).get(0);
            guesses.setValue(n, runningAns * maxPrice / 0.8382d);
            
            errSum += Math.pow(runningAns - 0.832d * targets.getDouble(n+1) / maxPrice, 2d);
            //runningAns = targets.getDouble(n+1) * 0.8382d / maxPrice;
            //if(r < targets.size() - 1 - 60) {
            //myNet.train(new double[][]{new double[]{targets.getDouble(n + 1) * 0.8382 / maxPrice}});
            //myNet.updateWithMomentum();
            //}
        }
        
        errSum /= targets.size() - testingStartIndex;
        
        if(age % 2 == 0) {
            System.out.println(errSum);
        }
        //batchResults.print();
       // System.out.println("  == = = = = = =                                 = = = =====");
        
        
        //new Matrix(MAJOR.ROW, rawDat).print();
        
        //System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        
        return errSum;
    }
    
    private Array maxValue(final double[][] arr) {
        double rowMax = Double.NEGATIVE_INFINITY;
        int majorRow = -1;
        int majorCol = -1;
        for(int row = 0; row < arr.length; row ++) {
            double colMax = Double.NEGATIVE_INFINITY;
            for(int c = 0; c < arr[row].length; c ++) {
                if(arr[row][c] > colMax) {
                    colMax = arr[row][c];
                    majorCol = c;
                }
            }
            if(colMax > rowMax) {
                rowMax = colMax;
                majorRow = row;
            }
        }
        return Array.of(new Point(majorCol, majorRow), rowMax);
    }
        
    class UpdateTracker implements MouseListener {
        
        final DataFrameColumn<? extends LocalDate, ? extends String> guesses;
        final DataFrameColumn<? extends LocalDate, ? extends String> targets;
        
        private long epoc;
        
        UpdateTracker(final DataFrameColumn<LocalDate, String> guesses, final DataFrameColumn<LocalDate, String> targets) {
            this.guesses = guesses;
            this.targets = targets;
        }


        @Override
        public void mouseClicked(MouseEvent e) {
            epoc ++;
            System.out.println("Update : " + epoc);
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }
        
    }
    

}