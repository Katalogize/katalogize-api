package com.katalogizegroup.katalogize.models.itemfields;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class ItemFieldInt extends ItemField {

    public int value;

    public ItemFieldInt(int order, String name, int value) {
        super(order, name);
        this.value = value;
    }
}
