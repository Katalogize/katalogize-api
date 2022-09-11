package com.katalogizegroup.katalogize.models;

import com.katalogizegroup.katalogize.models.itemfields.ItemFieldInt;
import com.katalogizegroup.katalogize.models.itemfields.ItemFieldString;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import java.util.List;

@Data
public class CatalogItemInput {
    @Id
    private String id = new ObjectId().toString();;

    @NonNull private String catalogId;

    @NonNull private String templateId;

    @NonNull private String name;

    private List<ItemFieldInt> integerFields;
    private List<ItemFieldString> stringFields;
}