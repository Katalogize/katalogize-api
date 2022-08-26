package com.katalogizegroup.katalogize.models;

import com.katalogizegroup.katalogize.models.itemfields.ItemField;
import com.katalogizegroup.katalogize.models.itemfields.ItemFieldInt;
import com.katalogizegroup.katalogize.models.itemfields.ItemFieldString;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document (collection = "catalogitems")
@Getter
@Setter
@AllArgsConstructor
public class CatalogItemInput {
    @Id
    @NonNull private int id;

    @NonNull private int catalogId;

    private List<ItemFieldInt> integerFields;
    private List<ItemFieldString> stringFields;

    @Transient
    public static final String SEQUENCE_NAME = "catalog_items_sequence";
}