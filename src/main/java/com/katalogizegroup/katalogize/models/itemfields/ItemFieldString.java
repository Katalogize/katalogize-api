package com.katalogizegroup.katalogize.models.itemfields;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemFieldString extends ItemField {

    public String value;

    public ItemFieldString(int order, String name, String value) {
        super(order, name);
        this.value = value;
    }
}
