package com.katalogizegroup.katalogize.models.itemfields;

import lombok.*;
import org.bson.types.ObjectId;

@Getter
@Setter
@Data
public abstract class ItemField {
    @NonNull private String templateFieldId;
    @NonNull private String name;
}

