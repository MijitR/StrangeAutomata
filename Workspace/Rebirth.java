/*
 * Copyright (C) 2020 mijitr <MijitR.xyz>
 *
 * Dis How Ballers Do...
 */
package MijitGroup.Workspace;

import MijitGroup.Workspace.Math.Matrix;
import MijitGroup.Workspace.Math.MAJOR;
import MijitGroup.Workspace.Networks.FullyConnected.NillerNet.Activation;
import static MijitGroup.Workspace.Networks.FullyConnected.NillerNet.Activation.IDENTITY;
import static MijitGroup.Workspace.Networks.FullyConnected.NillerNet.Activation.RELU;
import static MijitGroup.Workspace.Networks.FullyConnected.NillerNet.Activation.SHIFTSOFT;
import static MijitGroup.Workspace.Networks.FullyConnected.NillerNet.Activation.SIGMOID;
import static MijitGroup.Workspace.Networks.FullyConnected.NillerNet.Activation.SOFTPLUS;
import static MijitGroup.Workspace.Networks.FullyConnected.NillerNet.Activation.TANH;
import MijitGroup.Workspace.Networks.FullyConnected.NillerNet.NillerNet;
import com.zavtech.morpheus.array.Array;
import com.zavtech.morpheus.frame.DataFrame;
import com.zavtech.morpheus.frame.DataFrameColumn;
import com.zavtech.morpheus.frame.DataFrameRow;
import com.zavtech.morpheus.frame.DataFrameValue;
import com.zavtech.morpheus.frame.DataFrameVector;
import com.zavtech.morpheus.range.Range;
import com.zavtech.morpheus.util.Collect;
import com.zavtech.morpheus.util.text.parser.Parser;
import com.zavtech.morpheus.viz.chart.Chart;
import com.zavtech.morpheus.viz.chart.ChartFactoryProxy;
import java.awt.Color;
import java.awt.Font;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.PrimitiveIterator.OfDouble;
import java.util.Set;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

/**
 *
 * @author mijitr <MijitR.xyz>
 */
public class Rebirth {
    private static final int NUM_INPUTS,  NUM_OUTPUTS;
    
    static {
        NUM_INPUTS = 5;
        NUM_OUTPUTS = 1;
    }
    
    private final String alphabet  = "abcdefghijklmnopqrstuvwxyz ";
    
    private final double[] lookupTable = new double[alphabet.length()];
    
    private final int[] sizes = new int[]{ 15 , 7 , 33 ,  NUM_OUTPUTS };
    private final Activation[] acts = new Activation[]
        { SHIFTSOFT , TANH , TANH , TANH }
    ;
    
    private final NillerNet myNet = new NillerNet(NUM_INPUTS, sizes, acts);
    
    public static final void main(final String[] args) {
        final Rebirth birther = new Rebirth();
        birther.runShit();
    }
    
    private Rebirth() {
        Chart.create().asHtml();
    }
    
    public final void runShit() {
        
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Set<String> columnSet = Collect.asSet("Open", "Low", "High");
        String url = "C:/Users/rwhil/Downloads/TSLAFIVEYEARDAILY.csv";
        final Array<Integer> halfLives = Array.of(0, 5, 10, 30, 60);
        DataFrame<LocalDate,String> frame = DataFrame.read().csv(options -> {
            options.setResource(url);
            
            options.setColNamePredicate(columnSet::contains);
            options.setRowKeyParser(LocalDate.class, values -> LocalDate.parse(values[0], dateFormat));
            options.getFormats().setParser("Volume",  Parser.forLong(v -> v == null ? 0L : Long.parseLong(v)));
            options.setRowPredicate(row -> {
                LocalDate date = LocalDate.parse(row[0], dateFormat);
                return date.getDayOfWeek() == DayOfWeek.MONDAY;
            });
        });
        //Todo : make a column of price deltas per day
        frame.cols().add("Day Mean = AVG(Day open, Day low, Day high)", Double.class, v -> v.row().stats().mean());
        final DataFrameColumn<LocalDate, String> groundTruths = frame.col("Day Mean = AVG(Day open, Day low, Day high)");
        
        final LocalDate endDataDate = frame.tail(1).col("Day Mean = AVG(Day open, Day low, Day high)").rowKeys().findFirst().get();
        final Range<LocalDate> inferenceDates = Range.of(endDataDate, LocalDate.now());
        
        
        frame.cols().add("NetGuesses", Array.of(new Double[groundTruths.size() + (int)inferenceDates.estimateSize()]));
        
        final DataFrameColumn guesses = frame.col("NetGuesses");
        
        
        //final DataFrame<LocalDate, String> excess = DataFrame.ofDoubles(inferenceDates, )
                
        final ChartFactoryProxy proxy = Chart.create();
        
        /*final Chart charty = proxy.asHtml().withLinePlot(frame.right(2), chart -> {
        chart.title().withText("TTWO Stock Prices");
        chart.title().withFont(new Font("Arial", Font.BOLD, 14));
        chart.subtitle().withText("Featuring MeanLine(R) - The Most Average Thing Out There(TM)");
        chart.plot().axes().domain().label().withText("Day");
        chart.plot().axes().range(0).label().withText("Prices (Open, Close, Adj)");
        chart.plot().axes().range(0).format().withPattern("0.##'%';-0.##'%'");
        chart.plot().style("LONDON").withColor(Color.BLACK);
        chart.legend().on().bottom();
        chart.show();
    });*/
        
        double myErr = 2d, maxPrice;
        long epoc = 0l;
        do {
            
            //for(int i = 0; i < frame.rowCount(); i ++) {
           maxPrice = this.predict(guesses, groundTruths);
           
            //}
            
            
            if(epoc % 5 == 0) {
        //Create a plot, and display it
    proxy.withLinePlot(frame.right(2), chart -> {
        chart.title().withText("GM Stock Prices");
        chart.title().withFont(new Font("Arial", Font.BOLD, 14));
        chart.subtitle().withText("Featuring MeanLine(R) - The Most Average Thing Out There(TM)");
        chart.plot().axes().domain().label().withText("Day");
        chart.plot().axes().range(0).label().withText("Prices (Open, Close, Adj)");
        chart.plot().axes().range(0).format().withPattern("###.##'$';###.##'%'");
        chart.plot().style("LONDON").withColor(Color.BLACK);
        chart.legend().on().bottom();
        chart.show();
    });
            }
            
            epoc ++;
        
        } while(myErr >= 0.1d && epoc < 1001);
    
    }
    
    
    private double  predict(final DataFrameColumn<LocalDate, Double> guesses, final DataFrameColumn<LocalDate, String> targets) {
        final double[][] netInputs = new double[targets.size()][NUM_INPUTS];
        final double[][] targetField = new double[targets.size()][NUM_OUTPUTS];
        
        final double maxPrice = targets.stats().max();
        for(int t = 0; t < targets.size(); t ++) {
            for(int i = 0; i < netInputs[t].length-1; i ++) {
                netInputs[t][i] = t == 0 ? 0d : netInputs[t-1][i+1];
            }
            netInputs[t][netInputs[t].length-1] = 0.8382d * targets.getDouble(t) / maxPrice;
            targetField[t][0] = 0.8382d * targets.getDouble(t) / maxPrice;
        }
        
        final Matrix batchResults = myNet.process(netInputs);
        //batchResults.print();
        
        final double[][] rawDat = new double[batchResults.vectorCount()][NUM_OUTPUTS];
        
        for(int z = 0; z < batchResults.vectorCount(); z ++) {
            rawDat[z][0] = batchResults.instance(z).get(0);
            guesses.setValue(z, batchResults.instance(z).get(0) * maxPrice / 0.8382d);
        }
        
        myNet.trainInstant(targetField);
        
        //batchResults.print();
       // System.out.println("  == = = = = = =                                 = = = =====");
        
        
        //new Matrix(MAJOR.ROW, rawDat).print();
        
        //System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        
        return maxPrice;
    }
    
}
