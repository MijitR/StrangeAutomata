/*
 * Copyright (C) 2020 mijitr <MijitR.xyz>
 *
 * Dis How Ballers Do...
 */
package MijitGroup.Workspace;

import MijitGroup.Workspace.Networks.FullyConnected.NillerNet.Activation;
import static MijitGroup.Workspace.Networks.FullyConnected.NillerNet.Activation.SHIFTSOFT;
import static MijitGroup.Workspace.Networks.FullyConnected.NillerNet.Activation.TANH;
import MijitGroup.Workspace.Networks.FullyConnected.NillerNet.NillerNet;
import com.zavtech.morpheus.frame.DataFrame;
import com.zavtech.morpheus.array.Array;
import com.zavtech.morpheus.range.Range;
import com.zavtech.morpheus.util.text.parser.Parser;
import com.zavtech.morpheus.viz.chart.Chart;
import java.awt.Color;
import java.awt.Font;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.Year;
import java.util.ResourceBundle;

/**
 *
 * @author mijitr <MijitR.xyz>
 */
public class MidLife {
    
    private static final int NUM_INPUTS,  NUM_OUTPUTS;
    
    static {
        NUM_INPUTS = 3;
        NUM_OUTPUTS = 26;
    }
    
    private final String alphabet  = "abcdefghijklmnopqrstuvwxyz ";
    
    private final double[] lookupTable = new double[alphabet.length()];
    
    private final int[] sizes = new int[]{ 15 , 17 , 33 ,  NUM_OUTPUTS };
    private final Activation[] acts = new Activation[]
        { TANH , SHIFTSOFT , TANH , TANH }
    ;
    
    private final NillerNet myNet = new NillerNet(NUM_INPUTS, sizes, acts);
    
    private int miniBatchSize = 50, miniBatchCount = 150;
    
    public static final void main(final String[] args) {
        final MidLife lifer = new MidLife();
        lifer.runShit();
    }
    
    private MidLife() {
        Chart.create().swingMode();
    }
    
    public final void runShit() {
        //Create a data frame to capture the median prices of Apartments in the UK'a largest cities
DataFrame<Year,String> results = DataFrame.ofDoubles(
    Range.of(2011, 2015).map(Year::of),
    Array.of("LONDON", "BIRMINGHAM", "SHEFFIELD", "LEEDS", "LIVERPOOL", "MANCHESTER")
);

//Process yearly data in parallel to leverage all CPU cores
results.rows().keys().parallel().forEach(year -> {
    System.out.printf("Loading UK house prices for %s...\n", year);
    DataFrame<Integer,String> prices = loadHousePrices(year);
    prices.rows().select(row -> {
        //Filter rows to include only apartments in the relevant cities
        final String propType = row.getValue("PropertyType");
        final String city = row.getValue("City");
        final String cityUpperCase = city != null ? city.toUpperCase() : null;
        return propType != null && propType.equals("F") && results.cols().contains(cityUpperCase);
    }).rows().groupBy("City").forEach(0, (groupKey, group) -> {
        //Group row filtered frame so we can compute median prices in selected cities
        final String city = groupKey.item(0);
        final double priceStat = group.col("PricePaid").stats().median();
        results.data().setDouble(year, city, priceStat);
    });
});

//Map row keys to LocalDates, and map values to be percentage changes from start date
final DataFrame<LocalDate,String> plotFrame = results.mapToDoubles(v -> {
    final double firstValue = v.col().getDouble(0);
    final double currentValue = v.getDouble();
    return (currentValue / firstValue - 1d) * 100d;
}).rows().mapKeys(row -> {
    final Year year = row.key();
    return LocalDate.of(year.getValue(), 12, 31);
});
 
//Create a plot, and display it
Chart.create().withLinePlot(plotFrame, chart -> {
    chart.title().withText("Median Nominal House Price Changes");
    chart.title().withFont(new Font("Arial", Font.BOLD, 14));
    chart.subtitle().withText("Date Range: 1995 - 2014");
    chart.plot().axes().domain().label().withText("Year");
    chart.plot().axes().range(0).label().withText("Percent Change from 1995");
    chart.plot().axes().range(0).format().withPattern("0.##'%';-0.##'%'");
    chart.plot().style("LONDON").withColor(Color.BLACK);
    chart.legend().on().bottom();
    chart.show();
});
    }
    
    /**
 * Loads UK house price from the Land Registry stored in an Amazon S3 bucket
 * Note the data does not have a header, so columns will be named Column-0, Column-1 etc...
 * @param year      the year for which to load prices
 * @return          the resulting DataFrame, with some columns renamed
 */
private DataFrame<Integer,String> loadHousePrices(Year year) {
    String resource = "http://prod.publicdata.landregistry.gov.uk.s3-website-eu-west-1.amazonaws.com/pp-%s.csv";
    return DataFrame.read().csv(options -> {
        options.setResource(String.format(resource, year.getValue()));
        options.setHeader(false);
        options.setCharset(StandardCharsets.UTF_8);
        options.setIncludeColumnIndexes(1, 2, 4, 11);
        options.getFormats().setParser("TransactDate", Parser.ofLocalDate("yyyy-MM-dd HH:mm"));
        options.setColumnNameMapping((colName, colOrdinal) -> {
            switch (colOrdinal) {
                case 0:     return "PricePaid";
                case 1:     return "TransactDate";
                case 2:     return "PropertyType";
                case 3:     return "City";
                default:    return colName;
            }
        });
    });
}

}
