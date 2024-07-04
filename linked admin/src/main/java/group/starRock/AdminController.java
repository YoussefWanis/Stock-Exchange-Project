package group.starRock;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

public class AdminController {
    private final Admin admin = new Admin();

    @FXML
    private Label chartCompany;

    @FXML
    private LineChart<String, Number> lineChart;

    @FXML
    private TableView<ObservableList<String>> userList;
    @FXML
    private Button openMarket;

    @FXML
    private Button closeMarket;

    @FXML
    private NumberAxis yAxis;

    private static int activeGraph;

    @FXML
    private Button createStockButton;

    @FXML
    private Button editStock;
    @FXML
    private Button cycleThroughCompanies;

    @FXML
    private Label marketStatues;

    @FXML
    private Label noCompanies;

    LocalDate today = LocalDate.now();

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    String formattedDate = today.format(formatter);

    @FXML
    public void expandStockMarket() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Market.fxml"));
        Stage newStage = new Stage();
        Scene scene = new Scene(loader.load());
        newStage.setScene(scene);
        newStage.setTitle("MarketView");
        newStage.show();
    }

    @FXML
    public void createStock() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("CreateStock.fxml"));
        Stage newStage = new Stage();
        Scene scene = new Scene(loader.load());
        newStage.setScene(scene);
        newStage.setTitle("Create Stock");
        newStage.show();
    }

    @FXML
    public void initialize() {
        checkMarket(marketStatues);
        initializeLineChart();
       refresh();
    }
    public void refresh(){
        try {
            readUser();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readUser() throws IOException {
        List<String> userData = CSVModifier.readCSV("src/UserInfo/data.csv");

        userList.getColumns().clear();

        if (!userData.isEmpty()) {
            String[] headers = userData.get(0).split(",");
            for (int i = 0; i < headers.length; i++) {
                final int index = i;
                TableColumn<ObservableList<String>, String> column = new TableColumn<>(headers[i]);
                column.setCellValueFactory(param -> {
                    ObservableList<String> row = param.getValue();
                    return new SimpleStringProperty(row.get(index));
                });
                userList.getColumns().add(column);
            }

            ObservableList<ObservableList<String>> tableData = FXCollections.observableArrayList();
            for (int i = 1; i < userData.size(); i++) {
                String[] rowData = userData.get(i).split(",");
                ObservableList<String> row = FXCollections.observableArrayList();
                for (String value : rowData) {
                    row.add(value);
                }
                tableData.add(row);
            }
            userList.setItems(tableData);
        }
    }

    @FXML
    public void openMarket() throws IOException {
        admin.startSession();
        marketStatues.setText("Active");
        marketStatues.setTextFill(Color.web("#0bbc52"));
    }

    @FXML
    public void closeSession() {
        admin.closeSession();
        marketStatues.setText("Inactive");
        marketStatues.setTextFill(Color.web("#db0404"));
    }

    private void checkMarket(Label marketStatues) {
        boolean isMarketActive = CSVModifier.readCSVColumn("src/Stocks/Session.CSV", 0).size() > 1;
        if (isMarketActive) {
            marketStatues.setText("Active");
            marketStatues.setTextFill(Color.web("#0bbc52"));
        } else {
            marketStatues.setText("Inactive");
            marketStatues.setTextFill(Color.web("#db0404"));
        }
    }

    @FXML
    private void initializeLineChart() {
        List<String> list = CSVModifier.readCSVColumn("src/Stocks/Companies.CSV", 0);
        if (!list.isEmpty()) {
            noCompanies.setVisible(false);
            int size = list.size();
            Random random = new Random();
            int randomNumber = random.nextInt(0, size);
            String company = list.get(randomNumber);
            Charts.lineChartDay(lineChart, company, yAxis);
            activeGraph = list.indexOf(company);
        } else {
            noCompanies.setVisible(true);
        }
    }

    @FXML
    public void cycleThroughCompanies() {
        List<String> list = CSVModifier.readCSVColumn("src/Stocks/Companies.CSV", 0);
        String company;
        if (activeGraph < list.size() - 1) {
            company = list.get(activeGraph + 1);
            activeGraph++;
        } else {
            company = list.get(0);
            activeGraph = 0;
        }
        lineChart.getData().clear();
        Charts.lineChartDay(lineChart, company, yAxis);
    }
    public void removeUser() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("AdminRemoveUser.fxml"));
        Stage newStage = new Stage();
        Scene scene = new Scene(loader.load());
        newStage.setScene(scene);
        newStage.setTitle("Remove User");
        newStage.show();
    }
    public void createUser() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("AdminCreateUser.fxml"));
        Stage newStage = new Stage();
        Scene scene = new Scene(loader.load());
        newStage.setScene(scene);
        newStage.setTitle("Create User");
        newStage.show();
    }
}
