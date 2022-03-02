package com.jh.dangerzone.service;

import com.jh.dangerzone.domain.ResponseData;
import org.json.JSONArray;
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

/**
 * Class which handles the API-calls
 */
@Component
public class OpenDataMetobsReader {

    // Url for the metobs API
    private String metObsAPI = "https://opendata-download-metobs.smhi.se/api";

    private String stationName;

    /**
     * Performs multiple API-calls and returns data in unified class
     * This method needs revisoning due to late problems
     *
     * @param stationKey - Key for specified station for the requests
     * @param periodName - Type of time period for the requests
     * @return Relevant extracted data
     */
    ResponseData callAPI(int stationKey, String periodName) {

        String parsedString;
        Float temp, windSpeed;
        Integer windDirection = 0;

        /* I happened upon issues with calls to certain resources, which was caused due to
       the active flag being set to false.
         */

        parsedString = getSpecificResource(jsonResponse(stationKey, periodName, 1));
        try {
            temp = Float.parseFloat(parsedString);
        } catch (Exception e) {
            temp = null;
        }
        parsedString = getSpecificResource(jsonResponse(stationKey, periodName, 3));
        try {
            windDirection = Integer.parseInt(parsedString);
        } catch (Exception e) {
            windDirection = null;
        }
        parsedString = getSpecificResource(jsonResponse(stationKey, periodName, 4));
        try {
            windSpeed = Float.parseFloat(parsedString);
        } catch (Exception e) {
            windSpeed = null;
        }

        long epochTime = Instant.now().truncatedTo(ChronoUnit.SECONDS).toEpochMilli();
        return new ResponseData(stationKey, stationName, epochTime, temp, windDirection, windSpeed);

    }

    private JSONObject jsonResponse(int stationKey, String periodName, int parameter) {
        try {
            JSONObject jsonObject = readJsonFromUrl(getResource(stationKey, periodName, parameter));
            stationName = jsonObject.getJSONObject("station").getString("name");
            return jsonObject;
        } catch (IOException e) {
            System.err.println("Problem with the URL: " + e.getMessage());
        } catch (JSONException e) {
            System.err.println("Problem with JSON parsing: " + e.getMessage());
        }
        return null;
    }

    private String getSpecificResource(JSONObject parameterObject) {
        if (parameterObject != null) return parameterObject.getJSONArray("value").getJSONObject(0).getString("value");

        return null;
    }

    private String getResource(int stationKey, String periodName, int parameter) {
        return metObsAPI + "/version/1.0/parameter/" + parameter + "/station/" + stationKey + "/period/" + periodName + "/data.json";
    }

    /**
     * Utility method convert the JSON in String to an object
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

    /**
     * Actual request to the remote source
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
