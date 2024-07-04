package group.starRock;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

public class MarketController {
    @FXML
    ScrollPane scrollPane;
    @FXML
    Button editButton;
    @FXML
    TextField newPrice;
    @FXML
    TextField newVolume;
    @FXML
    LineChart<String, Number> mainlineChart;
    @FXML
    private NumberAxis mainYAxis;
    @FXML
    private Label noCompanies;
    @FXML
    private Label invalidPrice;
    @FXML
    private Label invalidVolume;
    @FXML
    private Label marketIsNotActive;

    private static String selectedCompany;

    public void initialize() {
        invalidPrice.setVisible(false);
        invalidVolume.setVisible(false);
        marketIsNotActive.setVisible(false);
        refresh();
    }

    public void selectCompany(String company) {
        mainlineChart.getData().clear();
        Charts.lineChartDay(mainlineChart, company, mainYAxis);
        selectedCompany = company;
    }

    public void refresh() {
        List<String> companies= CSVModifier.readCSVColumn("src/Stocks/Companies.CSV" , 0);
        if(!companies.isEmpty()){
            noCompanies.setVisible(false);
            mainlineChart.getData().clear();
            selectedCompany = companies.getFirst();
            Charts.lineChartDay(mainlineChart,selectedCompany,mainYAxis);
            VBox contentBox = new VBox();
            contentBox.setAlignment(Pos.CENTER);
            contentBox.paddingProperty().set(new Insets(10, 10, 10, 10));
            contentBox.setSpacing(15);
            List<String> listOfCompanies = CSVModifier.readCSVColumn("src/Stocks/Companies.CSV", 0);
            for (String company : listOfCompanies) {
                HBox itemBox = new HBox();
                itemBox.setMaxHeight(20); // Set the maximum height
                itemBox.setOnMouseEntered(_ -> itemBox.setCursor(Cursor.HAND));
                itemBox.setOnMouseExited(_ -> itemBox.setCursor(Cursor.DEFAULT));
                itemBox.setAlignment(Pos.CENTER); // Align children to left

                itemBox.setStyle("-fx-background-color: linear-gradient(to bottom right , #30478e, #1f2b4c, #182039);");
                Label companyLabel = new Label(company);
                companyLabel.paddingProperty().set(new Insets(10, 10, 10, 10));
                companyLabel.setStyle("-fx-font-weight: bold;");
                CategoryAxis xAxis = new CategoryAxis();
                NumberAxis yAxis = new NumberAxis();
                xAxis.setTickLabelsVisible(false);
                xAxis.setTickMarkVisible(false);
                yAxis.setTickLabelsVisible(false);
                yAxis.setTickMarkVisible(false);
                yAxis.setMinorTickVisible(false);
                LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
                Charts.lineChartDay(lineChart, company, yAxis);
                lineChart.setPrefSize(100, 20);
                itemBox.getChildren().addAll(companyLabel, lineChart);
                itemBox.setOnMouseClicked(_ -> selectCompany(company));
                contentBox.getChildren().add(itemBox);
        }
        scrollPane.setContent(contentBox);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        }
        else
        {noCompanies.setVisible(true);}
    }
    @FXML
    public void deleteStock(){
        Admin admin = new Admin();
        admin.removeStock(selectedCompany);
        refresh();
    }
    public void edit() throws IOException {
        boolean isMarketActive = CSVModifier.readCSVColumn("src/Stocks/Session.CSV", 0).size() > 1;
        if(isMarketActive){
            marketIsNotActive.setVisible(false);
            boolean errorType1 = false;
            boolean errorType2 = false;
            try{
           Double.parseDouble(newPrice.getText());
            }catch (Exception e){
                errorType1 = true;
            }
            try{
                Integer.parseInt(newVolume.getText());
            }catch (Exception e){
                errorType2 = true;
            }
            if(errorType1 && errorType2){
                invalidPrice.setVisible(true);
                invalidVolume.setVisible(true);
            } else if (errorType1 && !newPrice.getText().isEmpty()) {
                invalidPrice.setVisible(true);
                invalidVolume.setVisible(false);
            }
            else if (errorType2 && !newVolume.getText().isEmpty()) {
                invalidVolume.setVisible(true);
                invalidPrice.setVisible(false);
            }
            else {
                invalidVolume.setVisible(false);
                invalidPrice.setVisible(false);
            }
                if(!newVolume.getText().isEmpty()){
                    editVolume(Integer.parseInt(newVolume.getText()));
                }
                if(!newPrice.getText().isEmpty()){
                    editPrice(Double.parseDouble(newPrice.getText()));
                }

        }
        else{
            marketIsNotActive.setVisible(true);
        }
    }
    public void editPrice(double price) throws IOException {
        Admin admin = new Admin();
        admin.updateStockPrice(selectedCompany, price);
    }
    public void editVolume(int volume) throws IOException {
        Admin admin = new Admin();
        admin.updateStockVolume(selectedCompany, volume);
    }

}
