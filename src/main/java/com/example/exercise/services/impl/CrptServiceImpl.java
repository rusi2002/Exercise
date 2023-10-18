package com.example.exercise.services.impl;

import com.example.exercise.Document;
import com.example.exercise.services.CrptService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
@RequiredArgsConstructor
public class CrptServiceImpl implements CrptService {

    private Semaphore requestSemaphore;

    public CrptServiceImpl(TimeUnit timeUnit, int requestLimit) {
        requestSemaphore = new Semaphore(requestLimit);
    }

    @Override
    public void createDocument(Document document, String signature) throws InterruptedException, IOException {
        if (requestSemaphore.tryAcquire(0, TimeUnit.SECONDS)) {
            HttpURLConnection connection = null;
            BufferedReader responseReader = null;
            try {
                String name = document.getName();
                String content = document.getContent();
                LocalDateTime createdDate = document.getCreatedDate();

                Document documentData = new Document(createdDate,name, content);
                String jsonData = convertDocumentDataToJson(documentData,signature);

                String apiUrl = "https://api.crpt.com/createDocument";

                URL url = new URL(apiUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                connection.setDoOutput(true);

                try (OutputStream os = connection.getOutputStream(); OutputStreamWriter osw = new OutputStreamWriter(os, StandardCharsets.UTF_8)) {
                    osw.write(jsonData);
                    osw.flush();
                }

                int responseCode = connection.getResponseCode();
                System.out.println("API Response Status: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    StringBuilder response = new StringBuilder();

                    while ((line = responseReader.readLine()) != null) {
                        response.append(line);
                    }
                    System.out.println("Response Content: " + response);
                }
            } finally {
                if (responseReader != null) {
                    responseReader.close();
                }

                if (connection != null) {
                    connection.disconnect();
                }
                requestSemaphore.release();
            }
        } else {
            System.out.println("Семафор заблокирован. Превышен лимит запросов.");
        }
    }

    private String convertDocumentDataToJson(Document documentData,String signature) {
        return "{ \"name\": \"" + documentData.getName() + "\", \"content\": \"" + documentData.getContent() +
                "\", \"createdDate\": \"" + documentData.getCreatedDate() + "\", \"signature\": \"" + signature + "\" }";
    }
}
