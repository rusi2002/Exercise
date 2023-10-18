package com.example.exercise;

import com.example.exercise.services.CrptService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Crpt Api", description = "Crpt endpoints")
public class CrptApi {

    private final CrptService crptService;

    @PostMapping("/createDocument")
    public ResponseEntity<String> createDocument(@RequestBody Document document, @RequestParam String signature) {
        try {
            crptService.createDocument(document, signature);
            return ResponseEntity.ok("Document creation request sent successfully");
        } catch (InterruptedException | IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating the document: " + e.getMessage());
        }
    }
}
