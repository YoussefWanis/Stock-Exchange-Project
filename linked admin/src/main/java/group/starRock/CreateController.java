package group.starRock;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CreateController {

    @FXML
    private Label companyNameWarning;

    @FXML
    private Label currentPriceWarning;

    @FXML
    private TextField nameField;

    @FXML
    private TextField priceField;

    @FXML
    private TextField volumeField;

    @FXML
    private Label volumeWarning;

    public void initialize() {
        companyNameWarning.setVisible(false);
        currentPriceWarning.setVisible(false);
        volumeWarning.setVisible(false);

    }

    @FXML
    public void addStock(ActionEvent event) {
        boolean priceError = false;
         boolean volumeError = false;
        Admin admin = new Admin();
        companyNameWarning.setText("Required Field");
        currentPriceWarning.setText("Required Field");
        volumeWarning.setText("Required Field");
        companyNameWarning.setVisible(nameField.getLength() == 0);
        currentPriceWarning.setVisible(priceField.getLength() == 0);
        volumeWarning.setVisible(volumeField.getLength() == 0);

        String name = nameField.getText();
        double price = 0.0;
        int volume = 0;

        try {
            price = Double.parseDouble(priceField.getText());
        } catch (Exception e) {
            priceError = true;
        }

        try {
            volume = Integer.parseInt(volumeField.getText());
        } catch (Exception e) {
            volumeError = true;
        }

        if (!priceError && !volumeError) {
            admin.createStock(name,  (float) price, volume);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
        }
        if (priceError && priceField.getLength()!=0) {
            currentPriceWarning.setText("Invalid Input");
            currentPriceWarning.setVisible(true);
        }
        if (volumeError && priceField.getLength()!=0) {
            volumeWarning.setText("Invalid Input");
            volumeWarning.setVisible(true);
            volumeField.setStyle("-fx-text-fill: red");
        }
    }

}
