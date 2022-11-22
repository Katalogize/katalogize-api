package com.katalogizegroup.katalogize.models.inputs;

import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
@Data
public class TemplateFieldInput {
    @Id
    @NonNull private String id;
    @NonNull private int order;
    @NonNull private String name;
    @NonNull private int fieldType; //1: String, 2: Number, 3: Image
}