package com.katalogizegroup.katalogize.models;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "refresh-tokens")
@Getter
@Setter
@NoArgsConstructor
public class RefreshToken {
    @Id
    private String id = new ObjectId().toString();
    @NonNull private String token;
    @NonNull private int userId;
    @NonNull private Instant expiryDate;
}
