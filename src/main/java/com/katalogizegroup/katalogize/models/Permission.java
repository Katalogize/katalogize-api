package com.katalogizegroup.katalogize.models;

import lombok.Data;
import lombok.NonNull;

@Data
public class Permission {
    @NonNull private String email;
    @NonNull private int permission; //0: No Access, 1: View, 2: Edit, 3: Owner
}
