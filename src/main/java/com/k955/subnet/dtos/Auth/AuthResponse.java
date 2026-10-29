package com.k955.subnet.dtos.Auth;

import com.k955.subnet.dtos.User.UserProfileResponse;

public record AuthResponse(
        String token,
        UserProfileResponse user
) {
}
