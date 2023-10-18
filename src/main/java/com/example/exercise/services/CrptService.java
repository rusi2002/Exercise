package com.example.exercise.services;

import com.example.exercise.Document;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public interface CrptService {
    void createDocument(Document document, String signature) throws InterruptedException, IOException;

}
