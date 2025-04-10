package com.example.AutkaFX;
import javafx.scene.control.Button;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javafx.scene.control.Alert;

import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import java.io.File;

public class CarShowroomGUI extends Application {

    private CarShowroomContainer container;
    private ComboBox<String> showroomComboBox;
    private ListView<Vehicle> vehicleListView;
    private TableView<Vehicle> vehicleTableView;
    private TextField searchField; //wyszukiwanie
    private Stage primaryStage;
    private BorderPane root;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
       // Inicjalizacja kontenera i dodanie próbnych pojazdów
              container = new CarShowroomContainer();
//        container.addCenter("Autko", 16);
//        container.addCenter("Diselek", 45);
//        container.addCenter("Kopciuch", 24);
//        container.addCenter("MacQueen", 52);
//        container.addCenter("Domain", 10);
//        System.out.println("Number of showrooms: " + container.getShowrooms().size());
        //

//        container.getShowroom("Autko").addVehicle(new Vehicle("Toyota", "Corolla", ItemCondition.NEW, 25000.0, 2023, 10000.0, 1.8));
//        container.getShowroom("Diselek").addVehicle(new Vehicle("Ford", "Mustang", ItemCondition.USED, 35000.0, 2019, 5000.0, 5));
//        container.getShowroom("Kopciuch").addVehicle(new Vehicle("BMW", "X5", ItemCondition.NEW, 60000.0, 2022, 2000.0, 3.0));
//        container.getShowroom("Kopciuch").addVehicle(new Vehicle("Honda", "Civic", ItemCondition.USED, 20000.0, 2020, 8000.0, 1.6));
//        container.getShowroom("Domain").addVehicle(new Vehicle("Mercedes", "C-Class", ItemCondition.NEW, 55000.0, 2023, 1500.0, 2.0));
//        container.getShowroom("Diselek").addVehicle(new Vehicle("Audi", "A4", ItemCondition.NEW, 50000.0, 2023, 2000.0, 2.0));
//        container.getShowroom("Diselek").addVehicle(new Vehicle("Ford", "Focus", ItemCondition.NEW, 55000.00, 2020, 15000, 1600));
//        container.getShowroom("Diselek").addVehicle(new Vehicle("Ford", "Focus", ItemCondition.NEW, 55000.00, 2020, 15000, 1600));
//        container.getShowroom("Diselek").addVehicle( new Vehicle("Ford", "QRS", ItemCondition.DAMAGED, 42000.00, 2019, 78000, 1600));

        // Tworzenie UI
        root = new BorderPane();
        root.setPadding(new Insets(10));

        // Tworzenie przycisków
        Button saveButton = new Button("Save");
        saveButton.setOnAction(event -> saveCarShowroomStateToFile());

        Button loadButton = new Button("Load");
        loadButton.setOnAction(event -> loadCarShowroomStateFromFile());

        HBox fileButtons = new HBox(saveButton, loadButton);
        root.setLeft(fileButtons);

        showroomComboBox = new ComboBox<>();
        showroomComboBox.setItems(FXCollections.observableArrayList("Autko", "Diselek", "Kopciuch","MacQueen","Domain", "Dowolny"));
        showroomComboBox.setValue("Dowolny");

        searchField = new TextField();
        searchField.setPromptText("Search by name");

        vehicleListView = new ListView<>();
        vehicleTableView = new TableView<>();

        Button searchButton = new Button("Search");
        searchButton.setOnAction(event -> searchVehicleByBrand());

        Button deleteButton = new Button("Buy Vehicle");
        deleteButton.setOnAction(event -> deleteVehicle());

        // Układ UI
        //BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setTop(new HBox(showroomComboBox, searchField, searchButton, deleteButton));
        root.setCenter(vehicleListView);
        root.setBottom(vehicleTableView);

        // Tworzenie sceny i ustawienie UI
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Car Showroom");
        primaryStage.show();

        // Inicjalizacja komponentów UI
        initializeUIComponents();

        // Wybierz domyślną opcję "Dowolny" i wyświetl wszystkie pojazdy
        showroomComboBox.getSelectionModel().select("Dowolny");
        showroomComboBox.fireEvent(new ActionEvent());
    }

    private void initializeUIComponents() {
        showroomComboBox.setOnAction(e -> {
            String selectedShowroom = showroomComboBox.getValue();
            if (selectedShowroom.equals("Dowolny")) {
                showAllVehicles();
            } else {
                showVehiclesInShowroom(selectedShowroom);
            }
        });
        //listView
        vehicleListView.setCellFactory(param -> new ListCell<Vehicle>() {
            @Override
            protected void updateItem(Vehicle item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setTooltip(null);
                } else {
                    setText(item.getBrand() + " " + item.getModel());
                    Tooltip tooltip = new Tooltip("Mileage: " + item.getMileage() + "\nEngine: " + item.getEngineCapacity());
                    setTooltip(tooltip);
                }
            }
        });


        Button exportToCSVButton = new Button("Export to CSV");
        exportToCSVButton.setOnAction(event -> {
            String currentShowroomName = showroomComboBox.getValue();
            CarShowroom currentShowroom = container.getShowroom(currentShowroomName);
            if (currentShowroom != null) {
                CSVUtil.exportShowroomState(currentShowroom, "showroom.csv");
            } else {
                System.out.println("No showroom selected for export.");
            }
        });


        Button importFromCSVButton = new Button("Import from CSV");
        importFromCSVButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Load Showroom State");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                CarShowroom importedShowroom = CSVUtil.importShowroomState(file.getAbsolutePath());
                // Uaktualnij UI, aby pokazać zaimportowany salon samochodowy
            }
        });



        HBox csvButtons = new HBox(exportToCSVButton, importFromCSVButton);
        root.setRight(csvButtons);




        TableColumn<Vehicle, String> brandColumn = new TableColumn<>("Brand");
        brandColumn.setCellValueFactory(new PropertyValueFactory<>("brand"));
        TableColumn<Vehicle, String> modelColumn = new TableColumn<>("Model");
        modelColumn.setCellValueFactory(new PropertyValueFactory<>("model"));
        TableColumn<Vehicle, ItemCondition> conditionColumn = new TableColumn<>("Condition");
        conditionColumn.setCellValueFactory(new PropertyValueFactory<>("condition"));
        TableColumn<Vehicle, Double> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        TableColumn<Vehicle, Integer> yearColumn = new TableColumn<>("Year");
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("productionYear"));
        TableColumn<Vehicle, Double> engineColumn = new TableColumn<>("Engine Capacity");
        engineColumn.setCellValueFactory(new PropertyValueFactory<>("engineCapacity"));

        vehicleTableView.getColumns().addAll(brandColumn, modelColumn, conditionColumn, priceColumn, yearColumn, engineColumn);

        vehicleTableView.setRowFactory(param -> new TableRow<Vehicle>() {
            @Override
            protected void updateItem(Vehicle item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setTooltip(null);
                } else {
                    Tooltip tooltip = new Tooltip("Mileage: " + item.getMileage() + "\nEngine: " + item.getEngineCapacity());
                    setTooltip(tooltip);
                }
            }
        });

        Button saveButton = new Button("Save");
        saveButton.setOnAction(event -> saveCarShowroomStateToFile());

        Button loadButton = new Button("Load");
        loadButton.setOnAction(event -> loadCarShowroomStateFromFile());

        HBox fileButtons = new HBox(saveButton, loadButton);
        root.setLeft(fileButtons);


    }


    private void deleteVehicle() {
        Vehicle selectedVehicle = vehicleTableView.getSelectionModel().getSelectedItem();
        if (selectedVehicle != null) {
            String selectedShowroom = showroomComboBox.getValue();
            CarShowroom showroom = container.getShowroom(selectedShowroom);
            if (showroom != null) {
                showroom.removeVehicle(selectedVehicle); // Usunięcie pojazdu z salonu
                // Odświeżenie widoku
                showroomComboBox.fireEvent(new ActionEvent());
            }
        } else {
            // Wyświetlenie ostrzeżenia, jeśli nie wybrano żadnego pojazdu
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("Please select a vehicle to buy!");
            alert.showAndWait();
        }
    }




    private void searchVehicleByBrand() {
        String searchText = searchField.getText().trim().toLowerCase();
        if (!searchText.isEmpty()) {

            List<Vehicle> filteredList = new ArrayList<>();
            System.out.println("Number of showrooms: " + container.getShowrooms().size());

            for (Map.Entry<String, CarShowroom> entry : container.getShowrooms().entrySet()) {

                CarShowroom showroom = entry.getValue();
                if (showroom != null) {
                    for (Vehicle vehicle : showroom.getInventory().keySet()) {
                        String brand = vehicle.getBrand().toLowerCase();
                        if (brand.contains(searchText)) {
                            filteredList.add(vehicle);
                        }
                    }
                }
            }

            vehicleListView.getItems().clear();
            vehicleTableView.getItems().clear();

            vehicleListView.getItems().addAll(filteredList);
            vehicleTableView.getItems().addAll(filteredList);
        }
    }


    private void showAllVehicles() {
        vehicleListView.getItems().clear();
        vehicleTableView.getItems().clear();
        for (Map.Entry<String, CarShowroom> entry : container.getShowrooms().entrySet()) {
            CarShowroom showroom = entry.getValue();
            if (showroom != null) {
                for (Vehicle vehicle : showroom.getInventory().keySet()) {
                    vehicleListView.getItems().add(vehicle);
                    vehicleTableView.getItems().add(vehicle);
                }
            }
        }
    }

    public void saveCarShowroomStateToFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Car Showroom State");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Serialized Files", "*.ser"));
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            SerializationUtil.saveCarShowroomState(container, file.getAbsolutePath());
        }
    }

    private void loadCarShowroomStateFromFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Car Showroom State");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Serialized Files", "*.ser"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            CarShowroomContainer loadedContainer = SerializationUtil.loadCarShowroomState(file.getAbsolutePath());
            if (loadedContainer != null) {
                container = loadedContainer;
                showroomComboBox.fireEvent(new ActionEvent());
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error loading car showroom state from file.", ButtonType.OK);
                alert.showAndWait();
            }
        }
    }

    private void showVehiclesInShowroom(String showroomName) {
        vehicleListView.getItems().clear();
        vehicleTableView.getItems().clear();
        CarShowroom showroom = container.getShowroom(showroomName);
        if (showroom != null) {
            for (Vehicle vehicle : showroom.getInventory().keySet()) {
                vehicleListView.getItems().add(vehicle);
                vehicleTableView.getItems().add(vehicle);
            }
        }
    }

}
