package group.starRock;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.util.List;

public class UserEditController {

    @FXML
    private Button editButton;

    @FXML
    private TableColumn<UserData, String> idCol;

    @FXML
    private TextField newPrice;

    @FXML
    private TextField newVolume;

    @FXML
    private TableColumn<UserData, String> premiumCol;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private TableView<UserData> tableView;

    @FXML
    private TableColumn<UserData, String> usernameCol;

    public void initialize() {
        initializeTableColumns();
        refresh();
    }

    private void initializeTableColumns() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        premiumCol.setCellValueFactory(new PropertyValueFactory<>("premium"));
    }

    public void refresh() {
        String filePath = "path/to/your/csvfile.csv";
{
        }
    }

    public static class UserData {
        private final String id;
        private final String username;
        private final String premium;

        public UserData(String id, String username, String premium) {
            this.id = id;
            this.username = username;
            this.premium = premium;
        }

        public String getId() {
            return id;
        }

        public String getUsername() {
            return username;
        }

        public String getPremium() {
            return premium;
        }
    }
}
