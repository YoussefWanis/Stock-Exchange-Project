package group.starRock;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.IOException;
import java.util.List;
import java.util.Arrays;
import java.util.Objects;

public class TransactionController
{
    @FXML
    private TableView<ObservableList<String>> transactionTable;
    Alert alert = new Alert(Alert.AlertType.ERROR);

    public void loadTransactions()
    {
        String fileName = "src/UserInfo/" + MenuController.username + ".csv";

        try {
            List<String> allRows = CSVModifier.readCSV(fileName);
            ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();

            transactionTable.getColumns().clear();

            if (!allRows.isEmpty()) {
                String headerLine = allRows.getFirst();
                String[] headers = headerLine.split(",");
                for (int i = 0; i < headers.length; i++) {
                    final int colIndex = i;
                    TableColumn<ObservableList<String>, String> column = new TableColumn<>(headers[i]);
                    column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(colIndex)));
                    transactionTable.getColumns().add(column);
                }

                for (int i = 1; i < allRows.size(); i++) {
                    String row = allRows.get(i);
                    ObservableList<String> rowData = FXCollections.observableArrayList(Arrays.asList(row.split(",")));
                    data.add(rowData);
                }
            }

            transactionTable.setItems(data);
            transactionTable.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles.css")).toExternalForm());
        }
        catch (Exception e)
        {

        showError("There is no history ");

        }
    }
    private void showError(String message)
    {
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
