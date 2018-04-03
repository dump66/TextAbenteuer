import javafx.application.Application;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {

	private SimpleBooleanProperty isStarted = new SimpleBooleanProperty(false);
	private static Label hours;
	private static Label mins;
	private static Label sec;
	
	private double xOffset = 0;
	private double yOffset = 0;

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Timed Shutdown");
		VBox mainLayout = new VBox(20);
		mainLayout.setBackground(Background.EMPTY);
		mainLayout.setAlignment(Pos.CENTER);
		// ImageView ivUp = new ImageView("arrup.jpg");
		// ivUp.setFitHeight(50);
		// ivUp.setFitWidth(50);
		// ImageView ivDown = new ImageView("arrdown.png");
		// ivDown.setFitHeight(50);
		// ivDown.setFitWidth(50);
		// Button bUp = new Button();
		// bUp.setGraphic(ivUp);
		// bUp.setBackground(Background.EMPTY);
		// Button bDown = new Button();
		// bDown.setGraphic(ivDown);
		// bDown.setBackground(Background.EMPTY);

		Spinner<Integer> spinner = new Spinner<>();
		spinner.setEditable(true);
		SpinnerValueFactory<Integer> valueFactory = //
				new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 999, 25);

		spinner.setValueFactory(valueFactory);
		spinner.getValueFactory().valueProperty().addListener(new ChangeListener<Integer>() {

			@Override
			public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
				isStarted.set(false);
			}
		});

		ImageView imStop = new ImageView("red.png");
		imStop.setFitHeight(50);
		imStop.setFitWidth(50);
		ImageView imStart = new ImageView("green.png");
		imStart.setFitHeight(50);
		imStart.setFitWidth(50);
		Button startButton = new Button();
		startButton.setGraphic(imStop);
		startButton.setBackground(Background.EMPTY);
		
		MyTask task = new MyTask();
		
		startButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				isStarted.set(!isStarted.get());
			}
		});

		isStarted.addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				// Ausschalten
				if (oldValue && !newValue) {
					startButton.setGraphic(imStop);
					task.cancel();
					setHours("");
					setMinutes("");
					setSeconds("");
				}
				// Einschalten
				else if (!oldValue && newValue) {
					startButton.setGraphic(imStart);
					task.setSecondsToShutdown(spinner.getValue() * 60);
					task.reset();
					task.start();
				}

			}
		});

		HBox timeLeft = new HBox(10);
		VBox hoursLeft = new VBox();
		VBox minutesLeft = new VBox();
		VBox secondsLeft = new VBox();
		Label lHours = new Label("Stunden");
		Label lMins = new Label("Minuten");
		Label lSec = new Label("Sekunden");
		hours = new Label();
		mins = new Label();
		sec = new Label();
		hoursLeft.getChildren().addAll(lHours, hours);
		minutesLeft.getChildren().addAll(lMins, mins);
		secondsLeft.getChildren().addAll(lSec, sec);
		timeLeft.getChildren().addAll(hoursLeft, minutesLeft, secondsLeft);

		mainLayout.getChildren().addAll(spinner, startButton, timeLeft);
		Scene scene = new Scene(mainLayout);
		primaryStage.setScene(scene);
		primaryStage.show();
		
		scene.setOnKeyReleased(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.UP) {
					spinner.increment();
				}
				if (event.getCode() == KeyCode.DOWN) {
					spinner.decrement();
				}
				if (event.getCode() == KeyCode.ENTER) {
					isStarted.set(!isStarted.get());
				}
			}
		});
		
		scene.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = primaryStage.getX() - event.getScreenX();
                yOffset = primaryStage.getY() - event.getScreenY();
            }
        });
		scene.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                primaryStage.setX(event.getScreenX() + xOffset);
                primaryStage.setY(event.getScreenY() + yOffset);
            }
        });
		
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			
			@Override
			public void handle(WindowEvent event) {
				System.out.println("Closing...");
				task.cancel();
			}
		});
	}

	public static void main(String[] args) {
		launch(args);
	}
	
	public static void setHours(String text) {
		hours.setText(text);
	}
	
	public static void setMinutes(String text) {
		mins.setText(text);
	}
	
	public static void setSeconds(String text) {
		sec.setText(text);
	}
	
}
