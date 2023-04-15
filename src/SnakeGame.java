import java.util.ArrayList;
import java.util.Random;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SnakeGame extends Application {
    private static final int WIDTH = 600;
    private static final int HEIGHT = 600;
    private static final int ROWS = 30;
    private static final int COLS = 30;
    private static final int CELL_SIZE = WIDTH / COLS;
    private static final int FPS = 10;

    private Snake snake;
    private Apple apple;
    private int score = 0;
    private boolean gameOver = false;

    @Override
    public void start(Stage stage) {
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        BorderPane root = new BorderPane(canvas);
        Scene scene = new Scene(root);

        snake = new Snake();
        apple = new Apple();

        //Game navigation

        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.LEFT) {
                snake.setDirection(-1, 0);
            } else if (e.getCode() == KeyCode.RIGHT) {
                snake.setDirection(1, 0);
            } else if (e.getCode() == KeyCode.UP) {
                snake.setDirection(0, -1);
            } else if (e.getCode() == KeyCode.DOWN) {
                snake.setDirection(0, 1);
            }
        });

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1.0 / FPS), event -> {
            gc.setFill(Color.WHITE);
            gc.fillRect(0, 0, WIDTH, HEIGHT);

            if (gameOver) {
                gc.setFill(Color.RED);
                gc.setFont(new Font(50));
                gc.fillText("Game Over", 150, HEIGHT / 2);
                return;
            }

            // Move the snake
            snake.move();

            // Check for eating with apple
            if (snake.getHead().getX() == apple.getX() && snake.getHead().getY() == apple.getY()) {
                snake.grow();
                apple = new Apple();
                score++;
            }

            // Check for collision with walls or self
            if (snake.collidesWithWall() || snake.collidesWithSelf()) {
                gameOver = true;
            }

            // Draw the apple
            gc.setFill(Color.RED);
            gc.fillRect(apple.getX() * CELL_SIZE, apple.getY() * CELL_SIZE, CELL_SIZE, CELL_SIZE);

            // Draw the snake
            gc.setFill(Color.BLUE);
            for (Cell cell : snake.getCells()) {
                gc.fillRect(cell.getX() * CELL_SIZE, cell.getY() * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }

            // Draw the score
            gc.setFill(Color.BLACK);
            gc.setFont(new Font(20));
            gc.fillText("Score: " + score, 10, 30);
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    private static class Cell {
        private int x, y;

        public Cell(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }
    private static class Snake {
        private ArrayList<Cell> cells = new ArrayList<>();
        private int dx = 1, dy = 0;

        public Snake() {
            cells.add(new Cell(5, 5));
            cells.add(new Cell(4, 5));
            cells.add(new Cell(3, 5));
        }

        public ArrayList<Cell> getCells() {
            return cells;
        }

        public Cell getHead() {
            return cells.get(0);
        }

        public void setDirection(int dx, int dy) {
            this.dx = dx;
            this.dy = dy;
        }

        public void move() {
            Cell head = getHead();
            int newX = head.getX() + dx;
            int newY = head.getY() + dy;

            // Move the head
            cells.add(0, new Cell(newX, newY));

            // Remove the tail
            cells.remove(cells.size() - 1);
        }

        public void grow() {
            Cell head = getHead();
            int newX = head.getX() + dx;
            int newY = head.getY() + dy;

            // Add a new cell to the head
            cells.add(0, new Cell(newX, newY));
        }

        public boolean collidesWithWall() {
            Cell head = getHead();
            return head.getX() < 0 || head.getX() >= COLS || head.getY() < 0 || head.getY() >= ROWS;
        }

        public boolean collidesWithSelf() {
            Cell head = getHead();
            for (int i = 1; i < cells.size(); i++) {
                Cell cell = cells.get(i);
                if (head.getX() == cell.getX() && head.getY() == cell.getY()) {
                    return true;
                }
            }
            return false;
        }
    }

    private static class Apple {
        private int x, y;

        public Apple() {
            Random rand = new Random();
            x = rand.nextInt(COLS);
            y = rand.nextInt(ROWS);
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }
}

