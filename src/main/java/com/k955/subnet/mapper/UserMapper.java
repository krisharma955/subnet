package com.k955.subnet.mapper;

import com.k955.subnet.dtos.User.UserProfileResponse;
import com.k955.subnet.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserProfileResponse toUserProfileResponse(User user);

}
