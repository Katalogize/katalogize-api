package com.katalogizegroup.katalogize.models.itemfields;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemFieldNumber extends ItemField {

    public double value;

    public ItemFieldNumber(int order, String name, double value) {
        super(order, name);
        this.value = value;
    }
}
