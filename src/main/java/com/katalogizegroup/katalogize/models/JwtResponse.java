package com.katalogizegroup.katalogize.models;

import lombok.*;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
public class JwtResponse {
    private  String type = "Bearer";
    @NonNull private String accessToken;
    @NonNull private String refreshToken;
    @NonNull private String userId;
    @NonNull private String username;
    @NonNull private String email;
    @NonNull private boolean isAdmin;
}
