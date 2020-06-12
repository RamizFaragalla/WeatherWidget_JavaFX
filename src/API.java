import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONObject;

public abstract class API {
	private JSONObject json;
	
	public API(String urlString) throws IOException {
		try {
			String result = "";
			URL url = new URL(urlString);
			
			URLConnection conn = url.openConnection();
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line = rd.readLine();
			
			while(line != null) {
				result += line;
				line = rd.readLine();
			}
			
			rd.close();

			json = new JSONObject(result);
			fill();

		} catch (MalformedURLException e) {
			System.out.println(e.getMessage());
		}
		catch (IOException e) {
			throw new IOException("invalid location");
		}
	}

	public JSONObject getJSON() {
		return json;
	}
	
	public abstract void fill();
	
}
