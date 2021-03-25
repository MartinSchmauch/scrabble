package gui.LoginScreen;

//** @Author nilbecke

import java.io.FileInputStream;

import gui.LoginScreen.LoginScreenActionHandler;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginScreenPanel extends Application {

	private Label scrabble;
	private Label version;
	private Button joinGame;
	private Button hostGame;
	private Button tutorial;
	private Button user;
	private Button settings;
	private Button info;
	private Button statistics;
	private BorderPane root;
	private Scene scene;
	private LoginScreenActionHandler handler;

	/** Sets up Pane of current Stage **/
	public void setUp() {
		root = new BorderPane();
		handler = new LoginScreenActionHandler();
		root.setPadding(new Insets(10, 10, 10, 10));
				
		try {
			root.setTop(setUpTop());
		} catch (Exception e) {
			e.printStackTrace();
		}
		root.setCenter(setUpCenter());
		root.setBottom(setUpBottom());
	}

	/**
	 * Sets up Top containing the tutorial option and button to enter user settings
	 **/
	public GridPane setUpTop() throws Exception {
		GridPane top = new GridPane();
		HBox topLeft= new HBox();
		
		FileInputStream input = new FileInputStream("C:/Users/Nils Becker/Downloads/Questionmark.png");
		Image image = new Image(input);
		input.close();
		ImageView view = new ImageView(image);
		view.setFitHeight(25);
		view.setFitWidth(25);
		
		tutorial = new Button("Tutorial",view);
		tutorial.setOnAction(this.handler);

		Label username = new Label("Hi, <Username>");
		

		input = new FileInputStream("C:/Users/Nils Becker/Downloads/avatar.png");
		image = new Image(input);
		input.close();
		view = new ImageView(image);
		view.setFitHeight(20);
		view.setFitWidth(20);
		user = new Button("", view);
		user.setOnAction(this.handler);
		
		topLeft.getChildren().addAll(username,user);
		topLeft.setSpacing(10);
		topLeft.setAlignment(Pos.CENTER);
		
		top.add(tutorial, 0, 0);
		top.add(topLeft, 1, 0);
		
		// Pusht username nach links
		GridPane.setHgrow(tutorial, Priority.ALWAYS);
		//top.setGridLinesVisible(true);

		return top;

	}

	/** Sets up bottom containing access to Settings, readme and statistics **/
	public GridPane setUpBottom() {
		GridPane bottom = new GridPane();
		HBox bottomCentre = new HBox();

		version = new Label("Early Alpha Testbuild");

		bottom.add(version, 0, 1);
		
		settings = new Button("Settings");
		settings.setOnAction(this.handler);
		
		
		info = new Button("Scrabble3");
		info.setOnAction(this.handler);
		

		statistics = new Button("Statistics");
		statistics.setOnAction(this.handler);
		
		bottomCentre.getChildren().addAll(settings, info, statistics);
		bottomCentre.setAlignment(Pos.CENTER);
		bottomCentre.setSpacing(30);
		bottom.add(bottomCentre, 0, 0);
		GridPane.setHgrow(bottomCentre, Priority.SOMETIMES);
		//bottom.setGridLinesVisible(true);

		return bottom;
	}

	/**
	 * Sets up center containing Scrabble label, option to join game and option to
	 * host game
	 **/
	
	public VBox setUpCenter() {
		VBox center = new VBox();
		center.setAlignment(Pos.CENTER);

		scrabble = new Label("SCRABBLE");
		scrabble.setId("scrabble");
		
		joinGame = new Button("Join Game");
		joinGame.setPadding(new Insets(15,50,15,50));
		joinGame.setOnAction(this.handler);
		joinGame.setId("joinGame");

		hostGame = new Button("Host Game");
		hostGame.setOnAction(this.handler);
		
		center.getChildren().addAll(scrabble, joinGame, hostGame);
		center.setSpacing(30);
		return center;
	}

	/** Starts the LoginScreen **/
	@Override
	public void start(Stage current) throws Exception {
		setUp();
		scene = new Scene(root, 1280, 720);
		scene.getStylesheets().add(this.getClass().getResource("LoginStyle.css").toExternalForm());
		current.setTitle("Scrabble3");
		current.setScene(scene);
		current.setResizable(true);
		current.show();
	}

	public static void main(String[] args) {
		launch(args);

	}
}
