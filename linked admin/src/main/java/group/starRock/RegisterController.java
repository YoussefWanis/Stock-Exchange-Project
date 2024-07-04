package group.starRock;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class RegisterController {

    @FXML
    private TextField userRemove;

    @FXML
    private TextField UsernameField;

    @FXML
    private TextField PasswordField;

    @FXML
    private TextField BalanceField;

    @FXML
    private CheckBox PremiumCheck;

    public void Submit(ActionEvent event) throws IOException {
        String username = UsernameField.getText();
        String password = PasswordField.getText();
        String balance = BalanceField.getText();
        boolean check = PremiumCheck.isSelected();

        if (!validateRegistration(username, password, balance))
            return;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/UserInfo/data.csv", true))) {
            writer.write(generateUserData(username, password, check, Double.parseDouble(balance)) + "\n");
        } catch (Exception _) {
        }

        Back(event);
    }

    public void Back(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Login.fxml"));
        Stage stage1 = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(fxmlLoader.load());
        stage1.setScene(scene);
        stage1.show();
    }

    @FXML
    public void RemoveUser(ActionEvent event) throws IOException {
        String userId = userRemove.getText();

        if (userId.isEmpty() || !isValidNumber(userId)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a valid User ID.");
            alert.showAndWait();
            return;
        }

        boolean removed = removeUserById(Integer.parseInt(userId));

        if (removed) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("User removed successfully.");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("User ID not found.");
            alert.showAndWait();
        }

        Back(event);
    }

    public boolean removeUserById(int userId) {
        File inputFile = new File("src/UserInfo/data.csv");
        File tempFile = new File("src/UserInfo/temp_data.csv");
        boolean found = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length > 0 && !parts[0].equals(String.valueOf(userId))) {
                    writer.write(line + System.lineSeparator());
                } else {
                    found = true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!inputFile.delete()) {
            System.out.println("Could not delete file");
            return false;
        }

        if (!tempFile.renameTo(inputFile)) {
            System.out.println("Could not rename file");
            return false;
        }

        return found;
    }

    private List<String> findEmptyFields(String username, String password, String balance) {
        List<String> emptyFields = new ArrayList<>();

        if (username.isEmpty())
            emptyFields.add("Username");

        if (password.isEmpty())
            emptyFields.add("Password");

        if (balance.isEmpty())
            emptyFields.add("Balance");

        return emptyFields;
    }

    private boolean validateRegistration(String username, String password, String balance) {
        List<String> emptyFields = findEmptyFields(username, password, balance);

        if (!emptyFields.isEmpty()) {
            String emptyFieldsStr = String.join(", ", emptyFields);

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("You can't leave the following fields empty: " + emptyFieldsStr);
            alert.showAndWait();

            return false;
        }

        if (isUsernameTaken(username)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("This username is already taken.");
            alert.showAndWait();

            return false;
        }

        if (!isValidNumber(balance)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a valid number for the balance field.");
            alert.showAndWait();

            return false;
        }

        return true;
    }

    private boolean isUsernameTaken(String usernameToCheck) {
        try (BufferedReader reader = new BufferedReader(new FileReader("src/UserInfo/data.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2 && parts[1].equals(usernameToCheck))
                    return true;
            }
        } catch (Exception _) {
        }

        return false;
    }

    private boolean isValidNumber(String value) {
        try {
            Double.parseDouble(value);

            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static int getNextId() {
        List<String> list = new ArrayList<>();
        list = CSVModifier.readCSVColumn("src/UserInfo/data.csv" , 0);
        return Integer.parseInt(list.getLast()) + 1;
    }

    private static String generateUserData(String username, String password, boolean isPremium, double money) {
        int id = getNextId();
        String adminStr = "FALSE";
        String premiumStr = isPremium ? "TRUE" : "FALSE";

        return String.format("%d,%s,%s,%s,%s,%.2f", id, username, password, adminStr, premiumStr, money);
    }
}