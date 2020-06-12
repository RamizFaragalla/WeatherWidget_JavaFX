import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import javax.imageio.ImageIO;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Runner extends Application {
	private static VBox vBox = new VBox();	// main container
	private static Scene scene;
	private static VBox infoBox = new VBox(); // current info
	private static CurrentWeatherAPI weatherNow;
	private static TabPane tabPane;
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public void start(Stage primaryStage) {
		// header
		TextField search = new TextField();
		search.setId("TextField");
		search.setPrefWidth(270);
		
		Image searchIcon = new Image("searchIcon.png");
		ImageView searchImage = new ImageView(searchIcon);
		searchImage.setFitHeight(20);
		searchImage.setFitWidth(20);
		Button b = new Button();
		b.setGraphic(searchImage);
		
		Image refreshIcon = new Image("refreshIcon.png");
		ImageView refreshImage = new ImageView(refreshIcon);
		refreshImage.setFitHeight(20);
		refreshImage.setFitWidth(20);
		Button r = new Button();
		r.setGraphic(refreshImage);
		
		HBox headerBox = new HBox(5, search, b, r);
		headerBox.setId("headerBox");
		
		// current location
		Label location = new Label("Staten Island");
		
		//tabs
		tabPane = new TabPane();
		Tab today = new Tab("Today");
		Tab tomorrow = new Tab("Tomorrow");
		Tab sevenDays = new Tab("7 Days");
		
		tabPane.getTabs().add(today);
		tabPane.getTabs().add(tomorrow);
		tabPane.getTabs().add(sevenDays);
		
		tabPane.getSelectionModel().selectedItemProperty().addListener(e -> {
			Tab temp = tabPane.getSelectionModel().getSelectedItem();
			if(temp.equals(today)) {
				updateCurrent(location);
			}
			
			else if(temp.equals(tomorrow)) {
				updateTomorrow(location);
			}
			
			else 
				sevenDays(location);
		});
		
		vBox.getChildren().add(headerBox);
		vBox.getChildren().add(tabPane);
		vBox.getChildren().add(new Label(""));
		vBox.getChildren().add(infoBox);
		vBox.getChildren().add(new Label(""));
		vBox.setPadding(new Insets(10));
		
		updateCurrent(location);
		
		b.setOnAction(e -> {
			if(!search.getText().equals("")) {
				location.setText(search.getText());
				if(tabPane.getSelectionModel().getSelectedItem().equals(today))
					updateCurrent(location);
				else
					tabPane.getSelectionModel().select(today);
				
			}
		});
		
		r.setOnAction(e -> {
			if(tabPane.getSelectionModel().getSelectedItem().equals(today))
				updateCurrent(location);
			else
				tabPane.getSelectionModel().select(today);
		});
		
		// Create a Scene and display it.
	    scene = new Scene(vBox, Color.TRANSPARENT);
	    scene.getStylesheets().add("style.css");
	    
	    scene.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
			if(key.getCode() == KeyCode.ENTER) {
				b.fire();
			}
		});
	    
	    AutoRefresh timer = new AutoRefresh(location, today, tomorrow);
	    timer.start();
	    
	    primaryStage.setResizable(false);
	    primaryStage.initStyle(StageStyle.TRANSPARENT);
	    primaryStage.setScene(scene);
	    primaryStage.toBack();
	    primaryStage.show();
	}
	
	
	public static void updateCurrent(Label l) {
		try {			
			infoBox.getChildren().clear();
			Label date = new Label();
			infoBox.getChildren().add(date);
			
			HBox currWeather = new HBox();
			VBox left = new VBox(new Label(""));
			VBox right = new VBox();
			weatherNow = new CurrentWeatherAPI(l.getText());
			
			date.setText(weatherNow.getLocation() + " | " + dateNow(true));
			date.setId("date");
			
			Label highLow = new Label("High " + weatherNow.getHigh() + "° | Low " + weatherNow.getLow() + "°");
			highLow.setId("smallFont");
			Label currTemp = new Label("" + weatherNow.getTemp() + "°F");
			currTemp.setId("currTemp");
			Label feelsLike = new Label("Feels like " + weatherNow.getFeelsLike() + "°");
			feelsLike.setId("smallFont");
			left.getChildren().add(highLow);
			left.getChildren().add(currTemp);
			left.getChildren().add(feelsLike);
			// picture
			URL url = new URL("http://openweathermap.org/img/wn/"+weatherNow.getIcon()+"@2x.png");
			BufferedImage icon = ImageIO.read(url);
			Image image = SwingFXUtils.toFXImage(icon, null);
			ImageView imageView = new ImageView(image);
			right.getChildren().add(imageView);
			Label desc = new Label(weatherNow.getDescription());
			right.getChildren().add(desc);
			right.setPadding(new Insets(10));
			
			right.setId("rightBox");
			right.setPrefWidth(250);
			left.setId("centerBox");
			
			currWeather.getChildren().add(left);
			currWeather.getChildren().add(right);
			infoBox.getChildren().addAll(currWeather, new Label(""));
			
			WeatherAPI hourlyWeather = new WeatherAPI(weatherNow.getLat(), weatherNow.getLon());
			showHourlyWeather(hourlyWeather, true);
		}
		catch(IOException e) {
			l.setText("invalid location");
			infoBox.getChildren().add(l);
		}
	}
	
	public static void updateTomorrow(Label l) {
		try {			
			infoBox.getChildren().clear();
			
			if(l.getText().equals("invalid location"))
				throw new IOException();
			
			Label date = new Label();
			infoBox.getChildren().add(date);
			
			HBox tomorrowWeather = new HBox();
			VBox left = new VBox(new Label(""));
			VBox right = new VBox();
			WeatherAPI weatherTomorrow = new WeatherAPI(weatherNow.getLat(), weatherNow.getLon());
			
			date.setText(weatherNow.getLocation() + " | " + dateNow(false));
			date.setId("date");
			
			Label highLow = new Label("High " + weatherTomorrow.getTomorrowHigh() + "° | Low " + weatherTomorrow.getTomorrowLow() + "°");
			highLow.setId("smallFont");
			Label description = new Label(weatherTomorrow.getTomorrowDescription());
			
			left.getChildren().add(highLow);
			left.getChildren().add(description);
			// picture
			URL url = new URL("http://openweathermap.org/img/wn/"+weatherTomorrow.getTomorrowIcon()+"@2x.png");
			BufferedImage icon = ImageIO.read(url);
			Image image = SwingFXUtils.toFXImage(icon, null);
			ImageView imageView = new ImageView(image);
			right.getChildren().add(imageView);
			right.setPadding(new Insets(10));
			
			right.setId("rightBox");
			left.setPrefWidth(500);
			left.setId("centerBox");
			
			tomorrowWeather.getChildren().add(left);
			tomorrowWeather.getChildren().add(right);
			infoBox.getChildren().addAll(tomorrowWeather, new Label(""));
			
			showHourlyWeather(weatherTomorrow, false);
		}
		catch(IOException e) {
			l.setText("invalid location");
			infoBox.getChildren().add(l);
		}
	}
	
	@SuppressWarnings("deprecation")
	public static void showHourlyWeather(WeatherAPI hourlyWeather, boolean b) {
		try {
			
			HBox hourlyBox = new HBox();
			ScrollPane hourly = new ScrollPane();
			hourly.pannableProperty().set(true);
			hourly.setPrefWidth(10);
			hourly.setPrefHeight(160);
			hourly.setId("scroll");
			
			JSONArray arr = hourlyWeather.getHourly();
			JSONObject curr;
			int day;
			Date date;
			int i;
			
			if(b) {
				date = new Date(System.currentTimeMillis());
				day = date.getDate();
				
				i = 0;
				curr = arr.getJSONObject(i);
			}
			
			else {
				date = new Date(System.currentTimeMillis());
				Calendar c = Calendar.getInstance(); 
				c.setTime(date); 
				c.add(Calendar.DATE, 1);
				date = c.getTime();
				day = date.getDate();
				
				i = 0;
				
				
				while(true) {
					curr = arr.getJSONObject(i);
					if(getDate(curr) != day) {
						i++;
						continue;
					}
					
					if(getTime(curr).equals("7AM"))
						break;
					
					else i++;
				}
				
				curr = arr.getJSONObject(i);
			}
			
			while(getDate(curr) == day || (getDate(curr) == (nextDay(date)) && getTime(curr).compareTo("7AM") < 0)) {
				Label temp = new Label("" + curr.getInt("temp") + "°");
				// picture
				JSONArray desc = curr.getJSONArray("weather");
				JSONObject desc1 = desc.getJSONObject(0);
				URL url = new URL("http://openweathermap.org/img/wn/"+desc1.getString("icon")+"@2x.png");
				BufferedImage icon = ImageIO.read(url);
				Image image = SwingFXUtils.toFXImage(icon, null);
				ImageView imageView = new ImageView(image);
				imageView.setFitHeight(50);
				imageView.setFitWidth(50);
				Label wind = new Label(curr.getInt("wind_speed") + "mph\n ");
				wind.setId("smallFont");
				Label time = new Label(getTime(curr));
				
				VBox cell = new VBox(temp, imageView, wind, time);
				cell.setId("cell");
				hourlyBox.getChildren().add(cell);
				
				i++;
				curr = arr.getJSONObject(i);
			}
			
			hourly.setContent(hourlyBox);
			infoBox.getChildren().add(hourly);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sevenDays(Label l) {
		try {
			WeatherAPI weather = new WeatherAPI(weatherNow.getLat(), weatherNow.getLon());
			infoBox.getChildren().clear();
			
			if(l.getText().equals("invalid location"))
				throw new IOException();
			
			ScrollPane s = new ScrollPane();
			s.pannableProperty().set(true);
			s.setPrefHeight(340);
			JSONArray daily = weather.getDaily();
			VBox items = new VBox();
			
			for(int i = 0; i < daily.length(); i++) {
				HBox c = new HBox();
				VBox left;
				HBox right; 
				JSONObject curr = daily.getJSONObject(i);
				JSONObject desc = curr.getJSONArray("weather").getJSONObject(0);
				
				// left
				Label l1 = new Label(fullDate(curr));
				Label l2 = new Label(desc.getString("description"));
				l2.setId("midFont");
				left = new VBox(l1, l2);
				left.setPrefWidth(270);
				
				// right
				URL url = new URL("http://openweathermap.org/img/wn/"+desc.getString("icon")+"@2x.png");
				BufferedImage icon = ImageIO.read(url);
				Image image = SwingFXUtils.toFXImage(icon, null);
				ImageView r1 = new ImageView(image);
				r1.setFitHeight(50);
				r1.setFitWidth(50);
				
				VBox r2 = new VBox(new Label("" + curr.getJSONObject("temp").getInt("max")), new Label("" + curr.getJSONObject("temp").getInt("min")));
				right = new HBox(r1, r2);
				
				c.getChildren().addAll(left, right);
				
				if(i + 1 < daily.length() ) {
					Image whiteLine = new Image("whiteLine.jpg");
					ImageView line = new ImageView(whiteLine);
					line.setFitHeight(1);
					line.setFitWidth(350);
					
					items.getChildren().addAll(c, line);
				}
				
				else items.getChildren().addAll(c);
				
				
			}
			
			s.setContent(items);
			infoBox.getChildren().add(s);
			
		} catch (IOException e) {
			l.setText("invalid location");
			infoBox.getChildren().add(l);
		}
	}
	
	@SuppressWarnings("deprecation")
	public static int getDate(JSONObject j) {
		int dt = j.getInt("dt");
		Timestamp ts = new Timestamp(dt);
		Date date = new Date(ts.getTime() * 1000);
		return date.getDate();
	}
	
	@SuppressWarnings("deprecation")
	public static String getTime(JSONObject j) {
		int dt = j.getInt("dt");
		Timestamp ts = new Timestamp(dt);
		Date date = new Date(ts.getTime() * 1000);
		
		int h = date.getHours();
		if(h > 12)
			return (h - 12) + "PM";
		
		else if(h == 0)
			return "12AM";
		
		else return h + "AM";
	}
	
	@SuppressWarnings("deprecation")
	public static String dateNow(boolean b) {
		Date date = new Date(System.currentTimeMillis());
		
		if(b) 
			return date.toString();
		
		else {
			Calendar c = Calendar.getInstance(); 
			c.setTime(date); 
			c.add(Calendar.DATE, 1);
			date = c.getTime();
			String[] day = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturnday"};
			String[] month = {"Jan", "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sep", "Oct", "Nov", "Dec"};
			return day[date.getDay()] + ", " + month[date.getMonth()] + " " + date.getDate();
		}
	}
	
	@SuppressWarnings("deprecation")
	public static int nextDay(Date date) {
		Calendar c = Calendar.getInstance(); 
		c.setTime(date); 
		c.add(Calendar.DATE, 1);
		date = c.getTime();
		return date.getDate();
	}
	
	@SuppressWarnings("deprecation")
	public static String fullDate(JSONObject curr) {
		int dt = curr.getInt("dt");
		Timestamp ts = new Timestamp(dt);
		Date date = new Date(ts.getTime() * 1000);
		Date today = new Date(System.currentTimeMillis());
		
		if(date.getDate() == today.getDate())
			return "Today";
		
		else {
			String[] day = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturnday"};
			String[] month = {"Jan", "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sep", "Oct", "Nov", "Dec"};
			return day[date.getDay()] +", " + month[date.getMonth()] + " " + date.getDate();
		}
	}
	
	private class AutoRefresh extends AnimationTimer {
		private long prevTime = 0;
		private Label location;
		private Tab today;
		private Tab tomorrow;
		
		public AutoRefresh(Label l, Tab t, Tab tom) {
			location = l;
			today = t;
			tomorrow = tom;
		}
		
		public void handle(long now) {
			long dt = now - prevTime;
			
			if(dt > 60e9) {
				prevTime = now;
				
				Tab temp = tabPane.getSelectionModel().getSelectedItem();
				if(temp.equals(today)) {
					updateCurrent(location);
				}
				
				else if(temp.equals(tomorrow)) {
					updateTomorrow(location);
				}
				
				else 
					sevenDays(location);
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
