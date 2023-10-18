package com.example.exercise;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Document {

    private LocalDateTime createdDate;
    private String name;
    private String content;

}
