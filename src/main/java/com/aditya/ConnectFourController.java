package com.aditya;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import javax.swing.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ConnectFourController implements Initializable {

    public GridPane rootGridPane;
    public Pane insertedDiscPane;
    public Label playerNameLabel;

    public TextField playerOneTextField;
    public TextField playerTwoTextField;

    public Button setNames;

    // Creating Playground

    private static final int COLUMNS = 7;
    private static final int ROWS = 6;
    private static final int CIRCLE_DIAMETER = 80;
    private static final String discColor1 = "#24303E";
    private static final String discColor2 = "#4CAA88";

    private static String playerOne;
    private static String playerTwo;


    private static boolean isPlayerOneTurn = true;
    private boolean isAllowedToInsert = true;           // Flag to Avoid Same Colour Disc Being Added.

    private  Disc[][] insertedDiscArray = new Disc[ROWS][COLUMNS];


    public void createPlayground() {

        Shape rectangleWithHole = createGameStructuralGrid();
        rectangleWithHole.setFill(Color.WHITE);

        rootGridPane.add(rectangleWithHole, 0, 1);

        List<Rectangle> rectangleList = createClickableColumn();
        for(Rectangle rectangle : rectangleList) {
            rootGridPane.add(rectangle , 0, 1);
        }

        setNames.setOnAction(event -> convert());

    }

    public void convert() {

        String input1 = playerOneTextField.getText();
        String input2 = playerTwoTextField.getText();


        playerOne = input1;
        playerTwo = input2;


        playerNameLabel.setText(isPlayerOneTurn? playerOne : playerTwo);

    }



    private Shape createGameStructuralGrid() {

        Shape rectangleWithHole = new Rectangle((COLUMNS + 1) * CIRCLE_DIAMETER , (ROWS + 1) * CIRCLE_DIAMETER);

        for (int row = 0; row < ROWS; row++) {

            for (int col = 0; col < COLUMNS; col++) {

                Circle circle = new Circle();
                circle.setRadius(CIRCLE_DIAMETER / 2);
                circle.setCenterX(CIRCLE_DIAMETER / 2);
                circle.setCenterY(CIRCLE_DIAMETER / 2);
                circle.setSmooth(true);

                circle.setTranslateX(col * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);
                circle.setTranslateY(row * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);

                rectangleWithHole = Shape.subtract(rectangleWithHole, circle);

            }
        }

        return rectangleWithHole;
    }

    private List<Rectangle> createClickableColumn() {

        List<Rectangle> rectangleList = new ArrayList<>();

        for (int col = 0; col < COLUMNS; col++) {

            Rectangle rectangle = new Rectangle(CIRCLE_DIAMETER , (ROWS + 1) * CIRCLE_DIAMETER);
            rectangle.setFill(Color.TRANSPARENT);
            rectangle.setTranslateX(col * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);

            rectangle.setOnMouseEntered(event -> rectangle.setFill(Color.valueOf("#eeeeee26")));
            rectangle.setOnMouseExited(event -> rectangle.setFill(Color.TRANSPARENT));

            final int column = col;

            rectangle.setOnMouseClicked(event -> {
              if(isAllowedToInsert) {
                insertDisc(new Disc(isPlayerOneTurn) , column);     // Insert Disc

                  isAllowedToInsert = false;
              }
           });

            rectangleList.add(rectangle);

        }

        return  rectangleList;

    }

    private void insertDisc(Disc disc , int column) {

        int row = ROWS - 1;

        while(row >= 0) {

            if(getDiscIfPresent(row , column) == null) {            // getDiscIFPresent Function
                break;
            }

            row--;
        }

        if(row < 0) {       // it is full we cannot insert anymore disc
            return;
        }

        insertedDiscArray[row][column] = disc;            // For Structural Change : For Developer
        insertedDiscPane.getChildren().add(disc);         // For Visual Changes : For User


        disc.setTranslateX(column * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);

        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5) , disc);
        translateTransition.setToY(row * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);

        int currentRow = row;

        translateTransition.setOnFinished(event -> {

            isAllowedToInsert = true;

            if(gameEnded(currentRow , column)) {                // Game Ended Function
                gameOver();
                return;

            }

            isPlayerOneTurn = !isPlayerOneTurn;
            playerNameLabel.setText(isPlayerOneTurn? playerOne : playerTwo);


        });

        translateTransition.play();

    }

    private boolean gameEnded(int row , int column) {

        List<Point2D> verticalPoints= IntStream.rangeClosed(row - 3 , row + 3)
                                     .mapToObj(r -> new Point2D(r , column))     // 0,3  1,3  2,3  3,3  4,3  5,3 -> Point2D (x,y)
                                     .collect(Collectors.toList());

        List<Point2D> horizontalPoints = IntStream.rangeClosed(column - 3 , column + 3)
                                        .mapToObj(col -> new Point2D(row , col))
                                        .collect(Collectors.toList());

        Point2D startPoint1 = new Point2D(row - 3 , column + 3);
        List<Point2D> diagonal1 = IntStream.rangeClosed(0 , 6)
                                  .mapToObj(i -> startPoint1.add(i , -i))
                                  .collect(Collectors.toList());

        Point2D startPoint2 = new Point2D(row - 3 , column - 3);
        List<Point2D> diagonal2 = IntStream.rangeClosed(0 , 6)
                                  .mapToObj(i -> startPoint2.add(i , i))
                                  .collect(Collectors.toList());



        boolean isEnded = checkCombination(verticalPoints) || checkCombination(horizontalPoints)     // CheckCombination Function
                            || checkCombination(diagonal1) || checkCombination(diagonal2);

        return isEnded;
    }

    private boolean checkCombination(List<Point2D> points) {

        int chain = 0;

        for(Point2D point : points) {

            int rowIndexForArray = (int) point.getX();
            int colIndexForArray = (int) point.getY();

            Disc disc = getDiscIfPresent(rowIndexForArray , colIndexForArray);

            if(disc != null && disc.isPlayerOneMove == isPlayerOneTurn) { // last inserted Disc belong to current player

                chain++;

                if(chain == 4) {
                    return true;
                }
            }
            else {
                chain = 0;
            }

        }

        return false;
    }

    private Disc getDiscIfPresent(int row , int column) {                   // to prevent ArrayIndexOutOfBound Exception
        if(row >= ROWS || row < 0 || column >= COLUMNS || column < 0)
            return null;

        return insertedDiscArray[row][column];

    }

    private void gameOver() {

        String winner = isPlayerOneTurn ? playerOne : playerTwo;
        System.out.println("The Winner Is : " + winner);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Connect Four");
        alert.setHeaderText("The Winner Is : " + winner);
        alert.setContentText("Want To Play Again");

        ButtonType yesButton = new ButtonType("Yse");
        ButtonType noButton = new ButtonType("No , Exit");


        Platform.runLater(() -> {                               // Help to resolve illegal Exception

            alert.getButtonTypes().setAll(yesButton, noButton);

            Optional<ButtonType> clickedButton = alert.showAndWait();

            if (clickedButton.isPresent() && clickedButton.get() == yesButton) {

                gameReset();

            } else {
                Platform.exit();
                System.exit(0);
            }

        });

    }
    public void gameReset() {

        insertedDiscPane.getChildren().clear();         // remove all inserted disc

        for(int row = 0; row < insertedDiscArray.length; row++) {       // Structurally , // Make all element of insertedDisc[][] to null

            for(int col =0; col < insertedDiscArray[row].length; col++) {

                insertedDiscArray[row][col] = null;
            }
        }

        isPlayerOneTurn = true;
        playerNameLabel.setText(playerOne);

    }

    private static  class Disc extends Circle {

        private final boolean isPlayerOneMove;


        public Disc(boolean isPlayerOneMove) {
            this.isPlayerOneMove = isPlayerOneMove;
            setRadius(CIRCLE_DIAMETER / 2);
            setFill(isPlayerOneMove ? Color.valueOf(discColor1) : Color.valueOf(discColor2));
            setCenterX(CIRCLE_DIAMETER / 2);
            setCenterY(CIRCLE_DIAMETER / 2);
        }
    }




    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


    }
}
