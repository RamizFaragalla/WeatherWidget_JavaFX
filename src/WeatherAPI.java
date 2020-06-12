import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

public class WeatherAPI extends API{
	private JSONArray hourly;
	private JSONArray daily;
	public WeatherAPI(double lat, double lon) throws IOException {
		super("https://api.openweathermap.org/data/2.5/onecall?lat="
				+ lat + "&lon=" + lon + "&exclude=minutely&appid=" +
				"37031aa5fc78b53a1b529a3735e2e2c2&units=imperial");
	}
	
	public void fill() {
		hourly = getJSON().getJSONArray("hourly");
		daily = getJSON().getJSONArray("daily");
	}
	
	public JSONArray getHourly() {
		return hourly; 
	}
	
	public int getTomorrowHigh() {
		JSONObject tomorrow = daily.getJSONObject(1);
		return tomorrow.getJSONObject("temp").getInt("max");
	}	
	
	public int getTomorrowLow() {
		JSONObject tomorrow = daily.getJSONObject(1);
		return tomorrow.getJSONObject("temp").getInt("min");
	}
	
	public String getTomorrowDescription() {
		JSONObject tomorrow = daily.getJSONObject(1);
		JSONArray weather = tomorrow.getJSONArray("weather");
		JSONObject temp = weather.getJSONObject(0);
		
		return temp.getString("description");
	}
	
	public String getTomorrowIcon() {
		JSONObject tomorrow = daily.getJSONObject(1);
		JSONArray weather = tomorrow.getJSONArray("weather");
		JSONObject temp = weather.getJSONObject(0);
		
		return temp.getString("icon");
	}
	
	public JSONArray getDaily() {
		return daily;
	}
}
