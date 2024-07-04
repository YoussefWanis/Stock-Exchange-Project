package group.starRock;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

public class Charts {
    public static void lineChartDay(LineChart<String, Number> lineChart, String company, NumberAxis yAxis) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(company);

        // Read the x-axis (dates) data
        List<String> dateList = CSVModifier.readCSVColumn("src/Stocks/" + company + ".CSV", 0);
        dateList.removeFirst(); // Remove header row
        List<String> xAxisList = new ArrayList<>();

        // Determine the actual number of points to plot
        int dataPoints = Math.min(dateList.size(), 10);
        int start = dateList.size() - dataPoints;

        for (int i = start; i < dateList.size(); i++) {
            xAxisList.add(dateList.get(i));
        }

        // Read the y-axis (stock prices) data
        List<String> priceList = CSVModifier.readCSVColumn("src/Stocks/" + company + ".CSV", 1);
        priceList.remove(0); // Remove header row
        List<Double> yAxisList = new ArrayList<>();
        start = priceList.size() - dataPoints;

        for (int i = start; i < priceList.size(); i++) {
            yAxisList.add(Double.parseDouble(priceList.get(i)));
        }

        // Adjust the y-axis bounds
        double upperBound = Collections.max(yAxisList) + 3;
        double lowerBound = Collections.min(yAxisList) - 3;
        yAxis.setAutoRanging(false);
        yAxis.setUpperBound(upperBound);
        yAxis.setLowerBound(lowerBound);
        yAxis.setTickUnit((upperBound - lowerBound) / 10);

        // Populate the series with data
        for (int i = 0; i < dataPoints; i++) {
            series.getData().add(new XYChart.Data<>(xAxisList.get(i), yAxisList.get(i)));
        }

        // Add the series to the chart
        lineChart.getData().add(series);
    }
}
