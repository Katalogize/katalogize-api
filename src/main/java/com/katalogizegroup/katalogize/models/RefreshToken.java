package com.katalogizegroup.katalogize.models;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Document(collection = "refresh-tokens")
public class RefreshToken {
    @Id
    private String id = new ObjectId().toString();
    @NonNull private String token;
    @NonNull private String userId;
    @NonNull private Instant expiryDate;
}
