import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

public class CurrentWeatherAPI extends API {
	private JSONObject main;
	private JSONObject description;
	private JSONObject coord;
	
	public CurrentWeatherAPI(String location) throws IOException {
		super("http://api.openweathermap.org/data/2.5/weather?q="
				+ location + "&appid=37031aa5fc78b53a1b529a3735e2e2c2&units=imperial");
	}
	
	public void fill() {
		main = getJSON().getJSONObject("main");
		JSONArray weather = getJSON().getJSONArray("weather");
		description = weather.getJSONObject(0);
		coord = getJSON().getJSONObject("coord");
	}
	
	public int getTemp() {
		return main.getInt("temp");
	}
	
	public int getFeelsLike() {
		return main.getInt("feels_like");
	}
	
	public int getHigh() {
		return main.getInt("temp_max");
	}
	
	public int getLow() {
		return main.getInt("temp_min");
	}
	
	public String getDescription() {
		return description.getString("description");
	}
	
	public String getIcon() {
		return description.getString("icon");
	}
	
	public String getLocation() {
		return getJSON().getString("name");
	}
	
	public double getLat() {
		return coord.getDouble("lat");
	}
	
	public double getLon() {
		return coord.getDouble("lon");
	}
}
