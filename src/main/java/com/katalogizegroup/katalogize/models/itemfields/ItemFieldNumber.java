package com.katalogizegroup.katalogize.models.itemfields;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemFieldNumber extends ItemField {

    public double value;

    public ItemFieldNumber(String templateFieldId, String name, double value) {
        super(templateFieldId, name);
        this.value = value;
    }
}
