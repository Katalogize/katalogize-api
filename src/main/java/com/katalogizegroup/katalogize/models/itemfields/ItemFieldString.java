package com.katalogizegroup.katalogize.models.itemfields;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemFieldString extends ItemField {

    public String value;

    public ItemFieldString(String templateFieldId, String name, String value) {
        super(templateFieldId, name);
        this.value = value;
    }
}
