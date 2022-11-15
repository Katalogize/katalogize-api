package com.katalogizegroup.katalogize.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TemplateField {
    private int order;
    private String name;
    private int fieldType; //1: String, 2: Number, 3: Image
}