package com.katalogizegroup.katalogize.models;

import com.katalogizegroup.katalogize.models.itemfields.ItemFieldImage;
import com.katalogizegroup.katalogize.models.itemfields.ItemFieldNumber;
import com.katalogizegroup.katalogize.models.itemfields.ItemFieldString;
import lombok.*;
import org.springframework.data.annotation.Id;

import java.util.List;

@Data
public class CatalogItemInput {
    @Id
    @NonNull private String id;

    @NonNull private String catalogId;

    @NonNull private String templateId;

    @NonNull private String name;

    @NonNull private List<ItemFieldNumber> numberFields;
    @NonNull private List<ItemFieldString> stringFields;
    @NonNull private List<ItemFieldImage> imageFields;
}