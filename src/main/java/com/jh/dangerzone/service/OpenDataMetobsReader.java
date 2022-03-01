package com.jh.dangerzone.service;

import com.jh.dangerzone.domain.ResponseData;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
public class OpenDataMetobsReader {

    // Url for the metobs API
    private String metObsAPI = "https://opendata-download-metobs.smhi.se/api";

    /** Performs multiple API-calls and returns data in unified class
     *
     * @param stationKey - Key for specified station for the requests
     * @param periodName - Type of time period for the requests
     * @return Relevant extracted data
     */
    ResponseData callAPI(int stationKey, String periodName) throws IOException {

        JSONObject parameterObject = null;
        try {
            parameterObject = readJsonFromUrl(metObsAPI + "/version/latest/parameter/" + "1" + "/station/" + stationKey + "/period/" + periodName + "/data.json");
            float temp = Float.parseFloat(parameterObject.getJSONArray("value").getJSONObject(0).getString("value"));
            parameterObject = readJsonFromUrl(metObsAPI + "/version/latest/parameter/" + "3" + "/station/" + stationKey + "/period/" + periodName + "/data.json");
            int windDirection = Integer.parseInt(parameterObject.getJSONArray("value").getJSONObject(0).getString("value"));
            parameterObject = readJsonFromUrl(metObsAPI + "/version/latest/parameter/" + "4" + "/station/" + stationKey + "/period/" + periodName + "/data.json");
            float windSpeed = Float.parseFloat(parameterObject.getJSONArray("value").getJSONObject(0).getString("value"));
            String stationName = parameterObject.getJSONObject("station").getString("name");
//            long epochTime = parameterObject.getLong("updated");
            long epochTime = Instant.now().truncatedTo(ChronoUnit.SECONDS).toEpochMilli();
            return new ResponseData(stationKey,stationName,epochTime,temp,windDirection,windSpeed);
        } catch (IOException e) {
            throw new IOException("Problem with the URL: " + e.getMessage());
        }
        catch (JSONException e){
            throw new RuntimeException("Problem with JSON parsing: " + e.getMessage());
        }

    }

    /**
     *
     * @param url - location of the requested resource
     * @return JSONObject - converts the String response to a JSONObject
     * @throws IOException
     * @throws JSONException
     */
    JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        String text = readStringFromUrl(url);
        return new JSONObject(text);
    }

    /** Actual request to the remote source
     *
     * @param url - location of the requested resource
     * @return String based result of the response
     * @throws IOException
     */
    String readStringFromUrl(String url) throws IOException {

        InputStream inputStream = new URL(url).openStream();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
            StringBuilder stringBuilder = new StringBuilder();
            int cp;
            while ((cp = reader.read()) != -1) {
                stringBuilder.append((char) cp);
            }
            return stringBuilder.toString();
        } finally {
            inputStream.close();
        }
    }
}
