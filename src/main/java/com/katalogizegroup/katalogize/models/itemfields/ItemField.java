package com.katalogizegroup.katalogize.models.itemfields;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public abstract class ItemField {
    @NonNull private int order;
    @NonNull private String name = "Field Name";
}

