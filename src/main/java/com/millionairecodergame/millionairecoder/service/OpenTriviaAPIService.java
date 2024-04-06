package com.millionairecodergame.millionairecoder.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.millionairecodergame.millionairecoder.model.Question;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.bson.Document;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

public class OpenTriviaAPIService {
    private static final int MAX_RETRIES = 3;
    private static final int INITIAL_BACKOFF_DELAY_MS = 1000; // 1 second
    private static final String API_URL = "https://opentdb.com/api.php";
    private static final int category = 9; // General Knowledge


    public String fetchQuestions(int amount, String difficulty) throws IOException {
        int retries = 0;
        int backoffDelay = INITIAL_BACKOFF_DELAY_MS;
        String apiUrl = String.format("%s?amount=%d&category=%d&difficulty=%s&type=multiple", API_URL, amount, category, difficulty);
        HttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(apiUrl);
        while (retries < MAX_RETRIES) {
            try {
                // Check the response code
                HttpResponse response = httpClient.execute(httpGet);
                int statusCode = response.getStatusLine().getStatusCode();
                System.out.println("Response Code: " + statusCode);
                if (statusCode == 200) {
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        String responseBody = EntityUtils.toString(entity);
                        System.out.println("Response Body:");
                        System.out.println(responseBody);
                        return responseBody;
                    }
                    return null;
                } else if (statusCode == HttpURLConnection.HTTP_INTERNAL_ERROR) {
                    // Too many requests, wait and retry
                    System.out.println("Too many requests. Retrying in " + backoffDelay + " ms...");
                    Thread.sleep(backoffDelay);
                    backoffDelay *= 2; // Exponential backoff
                    retries++;
                } else {
                    break;
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
        return null;
    }

    public String fetchEasyQuestions(int amount) throws IOException {
        return fetchQuestions(amount, "easy");
    }

    public String fetchMediumQuestions(int amount) throws IOException {
        return fetchQuestions(amount, "medium");
    }

    public String fetchHardQuestions(int amount) throws IOException {
        return fetchQuestions(amount, "hard");
    }

}
