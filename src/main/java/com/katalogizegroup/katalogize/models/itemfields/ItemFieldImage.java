package com.katalogizegroup.katalogize.models.itemfields;

import com.katalogizegroup.katalogize.models.UploadFile;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ItemFieldImage extends ItemField {

    public List<UploadFile> value;

    public ItemFieldImage(int order, String name, List<UploadFile> value) {
        super(order, name);
        this.value = value;
    }
}
