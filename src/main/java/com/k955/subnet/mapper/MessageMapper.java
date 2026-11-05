package com.k955.subnet.mapper;

import com.k955.subnet.dtos.Message.MessageResponse;
import com.k955.subnet.entity.Message;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    MessageResponse toMessageResponse(Message message);

}
