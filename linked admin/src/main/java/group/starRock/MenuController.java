package group.starRock;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;


public class MenuController
{

    private static boolean errorType1;

    private static boolean errorType2;

    @FXML
    private TextField balanceTextField;

    @FXML
    private NumberAxis mainYAxis;

    @FXML
    private LineChart<String, Number> mainlineChart;

    @FXML
    private  TextField desiredPrice;

    @FXML
    private  TextField quantity;

    @FXML
    private Label Welcome;

    @FXML
    private Label balanceLabel;

    @FXML
    private Label invalidPrice;

    @FXML
    private Label invalidQuantity;

    @FXML
    private Label noCompanies;

    @FXML
    private ScrollPane scrollPane;

    private static String selectedCompany;

    static String username;

    public static  User user;

    private List<String> usernames;
    private List<String> passwords;
    private List<Boolean> Admins;
    private List<Boolean> Premiums;

    public void setUser(User user) {MenuController.user = user;}

    public void initializeLists(List<String> usernames, List<String> passwords, List<Boolean> admins, List<Boolean> premiums) {
        this.usernames = usernames;
        this.passwords = passwords;
        this.Admins = admins;
        this.Premiums = premiums;
    }

    public void initialize()
    {   refresh();
        invalidPrice.setVisible(false);
        invalidQuantity.setVisible(false);
        noCompanies.setVisible(false);

    }

    public void setUsername(String username)
    {
        MenuController.username = username;
        user.setUsername(username);
    }

    public void updateName(String username) {Welcome.setText("Hello: " + username);}

    public void updateBalance(String balance) {balanceLabel.setText(balance);}

    private static void checkInputValidity(TextField desiredPrice, TextField quantity,Label invalidPrice , Label invalidQuantity ){
        errorType1 = false;
        errorType2 = false;
        try{
            Float.parseFloat(desiredPrice.getText());
        }catch (Exception e){
            errorType1 = true;
        }
        try{
            Integer.parseInt(quantity.getText());
        }
        catch (Exception e){
            errorType2 = true;
        }
        if(errorType1){
            invalidPrice.setVisible(true);
        }
        if (errorType2)
            invalidQuantity.setVisible(true);
    }



    public void Transactions()
    {
        try
        {


            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Transaction.fxml"));

            Stage stage = new Stage();
            stage.setTitle(username + "'s Transactions");
            stage.setScene(new Scene(fxmlLoader.load()));
            TransactionController transactionController=fxmlLoader.getController();
            transactionController.loadTransactions();
            stage.show();
        }
        catch (IOException e) {error("Error loading transaction window.");}
    }

    public void Deposit()
    {
        String input = balanceTextField.getText();
        try
        {
            float balance = Float.parseFloat(input);
            float currentBalance = Float.parseFloat(balanceLabel.getText());
            balanceLabel.setText(String.valueOf(currentBalance + balance));
            user.deposit(currentBalance + balance);
            UpdateCSV();
        }
        catch (Exception e) {error("Input is not a valid number.");}
    }

    public void withdraw()
    {

        String input = balanceTextField.getText();
        try
        {
            float amount = Float.parseFloat(input);
            float currentBalance = Float.parseFloat(balanceLabel.getText());
            if (currentBalance >= amount)
            {
                balanceLabel.setText(String.valueOf(currentBalance - amount));
                user.withdraw((currentBalance - amount));
                UpdateCSV();
            }
            else
                error("Insufficient balance");
        }
        catch (NumberFormatException e) {error("Input is not a valid number.");}
    }

    public void buy() {
        checkInputValidity(desiredPrice, quantity, invalidPrice, invalidQuantity);
        if (!errorType1 && !errorType2) {
            try {
                Stock stock = stock();
                int quantityInt = Integer.parseInt(quantity.getText());
                float totalPrice = (float) (Objects.requireNonNull(stock).getPrice() * quantityInt);
                if (user.getMoneyBalance() >= totalPrice) {
                    user.buy(stock, quantityInt);
                    float currentBalance = Float.parseFloat(balanceLabel.getText());
                    balanceLabel.setText(String.valueOf(currentBalance - totalPrice));
                    UpdateCSV();
                    StringBuilder data = new StringBuilder();
                    for (int i = 0; i <quantityInt; i++) {
                        data.append(stock.getName()).append("\n");
                    }
                    CSVModifier.writeDataToCSV("src/UserInfo/" + MenuController.username + "Stocks.csv", data.toString());

                    showAlert(Alert.AlertType.INFORMATION, "Success", "Purchase Completed", "Stock bought successfully.");
                } else {
                    showAlert(Alert.AlertType.WARNING, "Insufficient Balance", "Insufficient Balance", "You do not have enough balance to buy this stock.");
                }
            } catch (NumberFormatException e) {
                error("Input is not a valid number.");
            }
        }
    }


        public void sell()
        {
            checkInputValidity(desiredPrice, quantity, invalidPrice, invalidQuantity);
            if (!errorType1 && !errorType2)
            {
                try
                {
                    String companyName = mainlineChart.getData().getFirst().getName();
                    int quantityToSell = Integer.parseInt(quantity.getText());

                    String csvFilePath = "src/UserInfo/" + username + "Stocks.csv";
                    int stockCount = countStocks(csvFilePath, companyName);

                    if (stockCount >= quantityToSell) {
                        CSVModifier.removeSoldStocks(csvFilePath, companyName, quantityToSell);
                        showAlert(Alert.AlertType.INFORMATION, "Stocks Sold", "Stocks Sold Successfully", "You have successfully sold " + quantityToSell + " stocks of " + companyName + ".");
                        user.sell(companyName, quantityToSell);
                        float currentBalance = Float.parseFloat(balanceLabel.getText());
                        float totalPrice =(float) (Objects.requireNonNull(stock()).getPrice() * quantityToSell);
                        balanceLabel.setText(String.valueOf(currentBalance + totalPrice));
                        UpdateCSV();
                    }
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Insufficient Stocks", "Insufficient Stocks to Sell", "You don't own enough stocks to sell.");
                }
            }

                 else
                    showAlert(Alert.AlertType.ERROR, "Insufficient Stocks", "Insufficient Stocks to Sell", "You don't own enough stocks to sell.");
        }

        private int countStocks(String filePath, String companyName) throws IOException {
            List<String> lines = CSVModifier.readCSV(filePath);
            int count = 0;
            for (String line : lines) {
                if (line.trim().equalsIgnoreCase(companyName)) {
                    count++;
                }
            }
            return count;
        }
    private Stock stock() {
        String selectedCompany = mainlineChart.getData().getFirst().getName();
        List<String> companies = CSVModifier.readCSVColumn("src/Stocks/Session.CSV", 0);
        int index = companies.indexOf(selectedCompany);
        if (index != -1) {
            float price = Float.parseFloat(CSVModifier.readCSVColumn("src/Stocks/Session.CSV", 3).get(index));
            return new Stock(selectedCompany, price, true);
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Company Not Found", "Selected company not found in CSV file.");
            return null; // or handle the error in your application's logic
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }


    private void error(String message)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void logout(ActionEvent event)
    {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText("Are you sure you want to logout?");
        alert.setContentText("Press OK to logout, or Cancel to stay logged in.");

        alert.showAndWait().ifPresent(response ->
        {
            if (response == ButtonType.OK) {
                try {
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stage.close();

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
                    Stage stage1 = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    Scene scene = new Scene(loader.load());
                    stage1.setScene(scene);
                    stage1.show();
                } catch (IOException _) {}
            }
        });
    }

    public void refresh()
    {
        List<String> companies= CSVModifier.readCSVColumn("src/Stocks/Companies.CSV" , 0);
        if(!companies.isEmpty()){noCompanies.setVisible(false);
            mainlineChart.getData().clear();
            selectedCompany = companies.getFirst();
            Charts.lineChartDay(mainlineChart,selectedCompany,mainYAxis);
            VBox contentBox = new VBox();
            contentBox.setAlignment(Pos.CENTER);
            contentBox.paddingProperty().set(new Insets(40, 10, 10, 10));
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
                itemBox.getChildren().addAll(companyLabel);
                itemBox.setOnMouseClicked(_ -> selectCompany(company));
                contentBox.getChildren().add(itemBox);
            }
            scrollPane.setContent(contentBox);
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        }
        else
        {noCompanies.setVisible(true);}
    }

    public void selectCompany(String company)
    {
        mainlineChart.getData().clear();
        Charts.lineChartDay(mainlineChart, company, mainYAxis);
        selectedCompany = company;
    }

    private void UpdateCSV()
    {
        float newBalance = Float.parseFloat(balanceLabel.getText());
        int userID = usernames.indexOf(username);
        List<String> rowData = new ArrayList<>();
        rowData.add(String.valueOf(userID + 1));
        rowData.add(username);
        rowData.add(passwords.get(userID));
        rowData.add(String.valueOf(Admins.get(userID)));
        rowData.add(String.valueOf(Premiums.get(userID)));
        rowData.add(String.valueOf(newBalance));

        CSVModifier.editRow("src/UserInfo/data.csv", userID, rowData);
    }
}