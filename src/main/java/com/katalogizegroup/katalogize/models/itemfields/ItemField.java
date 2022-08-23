package com.katalogizegroup.katalogize.models.itemfields;

import com.katalogizegroup.katalogize.utils.FieldTemplateEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public abstract class ItemField {
    @NonNull private String name;
}

