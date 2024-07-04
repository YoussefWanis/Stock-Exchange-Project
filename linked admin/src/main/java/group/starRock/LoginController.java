package group.starRock;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LoginController
{
    @FXML
    private TextField Username;
    @FXML
    private TextField Password;

    Alert alert = new Alert(Alert.AlertType.ERROR);




    public void login(ActionEvent event) throws IOException
    {
        List<String> usernames = CSVModifier.readCSVColumn("src/UserInfo/data.csv", 1);
        List<String> passwords = CSVModifier.readCSVColumn("src/UserInfo/data.csv", 2);
        List<Boolean> Admins = CSVModifier.readCSVColumn("src/UserInfo/data.csv", 3).stream().map(Boolean::parseBoolean).toList();
        List<Boolean> Premiums = CSVModifier.readCSVColumn("src/UserInfo/data.csv", 4).stream().map(Boolean::parseBoolean).toList();
        List<String> balances = CSVModifier.readCSVColumn("src/UserInfo/data.csv", 5);
        List<String> emptyFields = checkEmptyFields();
        if (emptyFields.isEmpty())
        {
            if (usernames.contains(Username.getText()) && passwords.contains(Password.getText()))
            {
                int userID = usernames.indexOf(Username.getText());

                User user=new User(Float.parseFloat(balances.get(userID)));

                if (Admins.get(userID))
                    LoadScene(event, "AdminDashboard.fxml", AdminController.class, Username.getText(), balances.get(userID), user, usernames, passwords, Admins, Premiums);
                else if (Premiums.get(userID))
                    LoadScene(event, "Dashboard.fxml", PremiumController.class, Username.getText(), balances.get(userID), user, usernames, passwords, Admins, Premiums);
                else
                    LoadScene(event, "Dashboard.fxml", MenuController.class, Username.getText(), balances.get(userID), user, usernames, passwords, Admins, Premiums);
            }
            else
                showError("Invalid Username or Password");
        }
        else
            showError("You can't leave the following fields empty: " + emptyFields);
    }

    private void LoadScene(ActionEvent event, String fxmlPath, Class<?> controllerClass, String username, String balance, User user, List<String> usernames, List<String> passwords, List<Boolean> admins, List<Boolean> premiums) throws IOException    {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.close();

        Stage newStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath));
        Scene scene = new Scene(fxmlLoader.load());

        if (controllerClass == MenuController.class)
        {
            MenuController controller = fxmlLoader.getController();
            controller.setUser(user);
            controller.setUsername(username);
            controller.updateName(username);
            controller.updateBalance(balance);
            controller.initializeLists(usernames, passwords, admins, premiums); // Pass lists to MenuController

        }
        else if (controllerClass == AdminController.class)
        {
            AdminController controller = fxmlLoader.getController();
            controller.openMarket();
            controller.readUser();
        }
        else if (controllerClass == PremiumController.class)
        {
            MenuController controller = fxmlLoader.getController();
            controller.setUser(user);
            controller.setUsername(username);
            controller.updateName(username);
            controller.updateBalance(balance);
            controller.initializeLists(usernames, passwords, admins, premiums);
        }

        newStage.setScene(scene);
        newStage.setResizable(true);
        newStage.show();

    }

    private List<String> checkEmptyFields()
    {
        List<String> emptyFields = new ArrayList<>();
        if (Objects.requireNonNull(Username).getText().isEmpty()) emptyFields.add("Username");
        if (Objects.requireNonNull(Password).getText().isEmpty()) emptyFields.add("Password");
        return emptyFields;
    }

    public void register(ActionEvent event) throws IOException
    {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.close();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Register.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        Stage newStage = new Stage();
        newStage.setScene(scene);
        newStage.setMinWidth(800);
        newStage.setMinHeight(600);
        newStage.setResizable(true);
        newStage.show();
    }

    private void showError(String message)
    {
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}