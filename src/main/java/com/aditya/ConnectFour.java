package com.aditya;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;

public class ConnectFour extends Application {

    private ConnectFourController controller;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {
        System.out.println("Init");
        super.init();
    }

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("connectFour_layout.fxml"));
        GridPane rootNode = loader.load();
        controller = loader.getController();
        controller.createPlayground();

        Stage primaryStage = new Stage();

        MenuBar menuBar = createMenu();
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());

        Pane menuPane = (Pane) rootNode.getChildren().get(0);
        menuPane.getChildren().add(menuBar);


        Scene scene = new Scene(rootNode);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Connect Four");
        primaryStage.setResizable(false);
        primaryStage.show();

    }

    private MenuBar createMenu() {

        // File Menu
        Menu fileMenu = new Menu("File");

        MenuItem newGame = new MenuItem("New Game");
        newGame.setOnAction(actionEvent -> controller.gameReset());

        MenuItem resetGame = new MenuItem("Reset Game");
        resetGame.setOnAction(actionEvent -> controller.gameReset());

        SeparatorMenuItem separator = new SeparatorMenuItem();

        MenuItem quitGame = new MenuItem("Quit Game");
        quitGame.setOnAction(ActionEvent -> quitGame());

        fileMenu.getItems().addAll(newGame, resetGame, separator, quitGame);

        // Help Menu
        Menu helpMenu = new Menu("Help");

        MenuItem aboutGame = new MenuItem("About Game");
        aboutGame.setOnAction(ActionEvent -> aboutGame());

        SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();

        MenuItem aboutMe = new MenuItem("About Me");
        aboutMe.setOnAction(ActionEvent -> aboutMe());

        helpMenu.getItems().addAll(aboutGame, separatorMenuItem, aboutMe);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, helpMenu);

        return menuBar;

    }

    private void aboutMe() {
        Alert alertDialogue = new Alert(Alert.AlertType.INFORMATION);
        alertDialogue.setTitle("About Developer");
        alertDialogue.setHeaderText("Aditya");
        alertDialogue.setContentText("Learning Java From InterShala And This Is My First Game.");
        alertDialogue.show();
    }

    private void aboutGame() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Connect Four");
        alert.setHeaderText("How To Play");
        alert.setContentText("Connect Four is a two-player connection game in which the players first choose a color and then take turns dropping colored discs from the top into a seven-column, six-row vertically suspended grid. The pieces fall straight down, occupying the next available space within the column. The objective of the game is to be the first to form a horizontal, vertical, or diagonal line of four of one's own discs. Connect Four is a solved game. The first player can always win by playing the right moves.");
        alert.show();
    }

    private void quitGame() {
        Platform.exit();
        System.exit(0);
    }




    @Override
    public void stop() throws Exception {
        System.out.println("Stop");
        super.stop();
    }
}
