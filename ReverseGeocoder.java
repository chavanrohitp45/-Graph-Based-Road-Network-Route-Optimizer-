import java.io.*;
import java.net.*;
import org.json.JSONObject;

public class ReverseGeocoder {

    public static String getAddress(double lat, double lon) {
        try {
            String urlStr = String.format(
                    "https://nominatim.openstreetmap.org/reverse?lat=%f&lon=%f&format=json&zoom=18&addressdetails=1",
                    lat, lon);

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Java-App");  // Mandatory for Nominatim API

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONObject json = new JSONObject(response.toString());
            JSONObject address = json.getJSONObject("address");

            String displayName = json.optString("display_name", "Unknown");
            return displayName;

        } catch (Exception e) {
            e.printStackTrace();
            return "Unknown";
        }
    }
}
