package com.katalogizegroup.katalogize.models;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

@Getter
@Setter
@Data
public class TemplateField {
    @Id
    private String id = new ObjectId().toString();
    @NonNull private int order;
    @NonNull private String name;
    @NonNull private int fieldType; //1: String, 2: Number, 3: Image
}