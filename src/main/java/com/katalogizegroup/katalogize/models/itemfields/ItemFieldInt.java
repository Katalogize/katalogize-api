package com.katalogizegroup.katalogize.models.itemfields;

import com.katalogizegroup.katalogize.utils.FieldTemplateEnum;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class ItemFieldInt extends ItemField {

    public int value;

    public ItemFieldInt(@NonNull String name, @NonNull int value) {
        super(name);
        this.value = value;
    }
}
