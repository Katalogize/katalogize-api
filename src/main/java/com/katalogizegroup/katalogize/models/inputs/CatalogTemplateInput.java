package com.katalogizegroup.katalogize.models.inputs;

import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;

import java.util.List;

@Data
public class CatalogTemplateInput {
    @Id
    @NonNull private String id;

    @NonNull private String name;

    @NonNull private List<TemplateFieldInput> templateFields;

    @NonNull private boolean allowNewFields;

}