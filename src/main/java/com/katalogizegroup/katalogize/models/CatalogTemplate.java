package com.katalogizegroup.katalogize.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document (collection = "catalog-templates")
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
public class CatalogTemplate {
    @Id
    @NonNull private int id;

    private String name;

    @NonNull private List<TemplateField> templateFields = new ArrayList<>();

    private boolean allowNewFields = false;

    @Transient
    public static final String SEQUENCE_NAME = "catalog_templates_sequence";

}