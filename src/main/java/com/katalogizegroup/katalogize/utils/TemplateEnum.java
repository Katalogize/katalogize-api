package com.katalogizegroup.katalogize.utils;

import lombok.Getter;

@Getter
public enum TemplateEnum {
    INTEGER(1), STRING(2), CHECKLIST(3), DATE(4);

    private final int value;
    TemplateEnum(int value){
        this.value = value;
    }
}