package com.katalogizegroup.katalogize.models.inputs;

import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;

import java.util.List;

@Data
public class CatalogInput {
    @Id
    @NonNull private String id;
    @NonNull private String name;
    @NonNull private String description;
    @NonNull private String userId;
    @NonNull private List<String> templateIds;
}