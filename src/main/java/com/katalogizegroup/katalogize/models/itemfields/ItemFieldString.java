package com.katalogizegroup.katalogize.models.itemfields;

import com.katalogizegroup.katalogize.utils.FieldTemplateEnum;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class ItemFieldString extends ItemField {

    public String value;

    public ItemFieldString(@NonNull String name, @NonNull String value) {
        super(name);
        this.value = value;
    }
}
