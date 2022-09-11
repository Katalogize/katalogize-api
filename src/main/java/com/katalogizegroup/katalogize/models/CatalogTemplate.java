package com.katalogizegroup.katalogize.models;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@Document (collection = "catalog-templates")
public class CatalogTemplate {
    @Id
    private String id = new ObjectId().toString();;

    @NonNull private String name;

    @NonNull private List<TemplateField> templateFields;

    @NonNull private boolean allowNewFields;

//    @Transient
//    public static final String SEQUENCE_NAME = "catalog_templates_sequence";

}