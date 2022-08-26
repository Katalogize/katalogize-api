package com.katalogizegroup.katalogize.models;

import com.katalogizegroup.katalogize.models.itemfields.ItemFieldInt;
import com.katalogizegroup.katalogize.models.itemfields.ItemFieldString;
import lombok.*;
import org.springframework.data.annotation.Id;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class CatalogItemInput {
    @Id
    @NonNull private int id;

    @NonNull private int catalogId;

    @NonNull private int templateId;

    private List<ItemFieldInt> integerFields;
    private List<ItemFieldString> stringFields;
}