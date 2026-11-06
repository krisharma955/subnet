package com.k955.subnet.service.impl;

import com.k955.subnet.dtos.Message.MessageRequest;
import com.k955.subnet.dtos.Message.MessageResponse;
import com.k955.subnet.dtos.Message.UpdateMessageRequest;
import com.k955.subnet.dtos.MessageReaction.MessageReactionRequest;
import com.k955.subnet.dtos.MessageReaction.MessageReactionResponse;
import com.k955.subnet.entity.Channel;
import com.k955.subnet.entity.Message;
import com.k955.subnet.entity.MessageReaction;
import com.k955.subnet.entity.User;
import com.k955.subnet.exception.BadRequestException;
import com.k955.subnet.exception.ErrorCode;
import com.k955.subnet.exception.ResourceNotFoundException;
import com.k955.subnet.mapper.MessageMapper;
import com.k955.subnet.mapper.MessageReactionMapper;
import com.k955.subnet.repository.*;
import com.k955.subnet.security.JwtAuthUtil;
import com.k955.subnet.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final JwtAuthUtil jwtAuthUtil;
    private final ChannelMemberRepository channelMemberRepository;
    private final MessageMapper messageMapper;
    private final MessageReactionRepository messageReactionRepository;
    private final MessageReactionMapper messageReactionMapper;

    @Override
    @Transactional
    public MessageResponse sendMessage(UUID channelId, MessageRequest messageRequest) {
        UUID userId = jwtAuthUtil.getCurrentUserId();

        boolean isChannelMember = channelMemberRepository.existsByChannelIdAndUserId(channelId, userId);
        if(!isChannelMember) throw new BadRequestException(ErrorCode.CHANNEL_MEMBERSHIP_NOT_FOUND, "Only Channel Members can send messages.");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.USER_NOT_FOUND, "Your User Details can't be loaded at the moment. Try Again!"
                ));

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.CHANNEL_NOT_FOUND, "The Channel Details can't be loaded at the moment. Try Again!"
                ));

        Message message = Message.builder()
                .channel(channel)
                .sender(user)
                .content(messageRequest.content())
                .build();
        Message savedMessage = messageRepository.save(message);

        return messageMapper.toMessageResponse(savedMessage);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MessageResponse> getChannelMessages(UUID channelId, Pageable pageable) {
        UUID userId = jwtAuthUtil.getCurrentUserId();

        boolean isChannelMember = channelMemberRepository.existsByChannelIdAndUserId(channelId, userId);
        if(!isChannelMember) throw new BadRequestException(ErrorCode.CHANNEL_MEMBERSHIP_NOT_FOUND, "Only Channel Members can access messages.");

        return messageRepository.findAllByChannelId(channelId, pageable)
                .map(messageMapper::toMessageResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public MessageResponse getMessageById(UUID messageId) {
        UUID userId = jwtAuthUtil.getCurrentUserId();

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.BAD_REQUEST, "BAD REQUEST"
                ));

        boolean isChannelMember = channelMemberRepository.existsByChannelIdAndUserId(message.getChannel().getId(), userId);
        if(!isChannelMember) throw new BadRequestException(ErrorCode.CHANNEL_MEMBERSHIP_NOT_FOUND, "Only Channel Members can access messages.");

        return messageMapper.toMessageResponse(message);
    }

    @Override
    @Transactional
    public MessageResponse updateMessageById(UUID messageId, UpdateMessageRequest updateMessageRequest) {
        UUID userId = jwtAuthUtil.getCurrentUserId();

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.BAD_REQUEST, "BAD REQUEST"
                ));

        boolean isChannelMember = channelMemberRepository.existsByChannelIdAndUserId(message.getChannel().getId(), userId);
        if(!isChannelMember) throw new BadRequestException(ErrorCode.CHANNEL_MEMBERSHIP_NOT_FOUND, "Only Channel Members can access messages.");

        if(!message.getSender().getId().equals(userId)) {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST, "Only the sender can edit their messages");
        }

        if(updateMessageRequest.content() != null && !updateMessageRequest.content().isBlank()) {
            message.setContent(updateMessageRequest.content());
        }

        Message savedMessage = messageRepository.save(message);

        return messageMapper.toMessageResponse(savedMessage);
    }

    @Override
    @Transactional
    public void deleteMessageById(UUID messageId) {
        UUID userId = jwtAuthUtil.getCurrentUserId();

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.BAD_REQUEST, "BAD REQUEST"
                ));

        boolean isChannelMember = channelMemberRepository.existsByChannelIdAndUserId(message.getChannel().getId(), userId);
        if(!isChannelMember) throw new BadRequestException(ErrorCode.CHANNEL_MEMBERSHIP_NOT_FOUND, "Only Channel Members can access messages.");

        if(!message.getSender().getId().equals(userId)) {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST, "Only the sender can delete their messages");
        }

        messageRepository.delete(message);
    }

    @Override
    @Transactional
    public MessageResponse replyToMessage(UUID messageId, MessageRequest messageRequest) {
        UUID userId = jwtAuthUtil.getCurrentUserId();

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.MESSAGE_NOT_FOUND, "The message can't be loaded at the moment."
                ));

        boolean isChannelMember = channelMemberRepository
                .existsByChannelIdAndUserId(message.getChannel().getId(), userId);
        if(isChannelMember) {
            throw new BadRequestException(ErrorCode.BAD_REQUEST, "Only Channel Members can access the chat");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.USER_NOT_FOUND, "The User Details can't be loaded at this moment. Try Later!"
                ));

        Channel channel = channelRepository.findById(message.getChannel().getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.CHANNEL_NOT_FOUND, "The Channel Details can't be loaded at this moment. Try Later!"
                ));

        if (message.getParentMessage() != null) {
            throw new BadRequestException(
                    ErrorCode.BAD_REQUEST, "Replies to replies are not allowed"
            );
        }

        Message reply = Message.builder()
                .channel(channel)
                .sender(user)
                .content(messageRequest.content())
                .parentMessage(message)
                .build();
        Message savedReply = messageRepository.save(reply);

        return messageMapper.toMessageResponse(savedReply);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MessageResponse> getReplies(UUID messageId, Pageable pageable) {
        UUID userId = jwtAuthUtil.getCurrentUserId();

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.MESSAGE_NOT_FOUND, "The message can't be loaded at the moment."
                ));

        boolean isChannelMember = channelMemberRepository
                .existsByChannelIdAndUserId(message.getChannel().getId(), userId);
        if(isChannelMember) {
            throw new BadRequestException(ErrorCode.BAD_REQUEST, "Only Channel Members can access the chat");
        }

        return messageRepository.findAllByParentMessageId(messageId, pageable)
                .map(messageMapper::toMessageResponse);
    }

    @Override
    @Transactional
    public MessageReactionResponse addMessageReaction(UUID messageId, MessageReactionRequest messageReactionRequest) {
        UUID userId = jwtAuthUtil.getCurrentUserId();

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.MESSAGE_NOT_FOUND, "The message can't be loaded at the moment."
                ));

        boolean isChannelMember = channelMemberRepository
                .existsByChannelIdAndUserId(message.getChannel().getId(), userId);
        if(!isChannelMember) {
            throw new BadRequestException(ErrorCode.FORBIDDEN, "Only Channel Members can access the chat");
        }

        boolean exists = messageReactionRepository.existsByMessageIdAndUserId(messageId, userId);
        if(exists) {
            MessageReaction messageReaction = messageReactionRepository.findByMessageIdAndUserId(messageId, userId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            ErrorCode.MESSAGE_REACTION_NOT_FOUND, "No Message Reaction found!"
                    ));

            messageReaction.setEmoji(messageReactionRequest.emoji());
            messageReactionRepository.save(messageReaction);
            return messageReactionMapper.toMessageReactionResponse(messageReaction);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.USER_NOT_FOUND, "The User Details can't be loaded at this moment. Try Later!"
                ));

        MessageReaction messageReaction = MessageReaction.builder()
                .message(message)
                .user(user)
                .emoji(messageReactionRequest.emoji())
                .build();
        MessageReaction saved = messageReactionRepository.save(messageReaction);

        return messageReactionMapper.toMessageReactionResponse(saved);
    }

    @Override
    @Transactional
    public void removeMessageReaction(UUID messageId) {
        UUID userId = jwtAuthUtil.getCurrentUserId();

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.MESSAGE_NOT_FOUND, "The message can't be loaded at the moment."
                ));

        boolean isChannelMember = channelMemberRepository
                .existsByChannelIdAndUserId(message.getChannel().getId(), userId);
        if(!isChannelMember) {
            throw new BadRequestException(ErrorCode.FORBIDDEN, "Only Channel Members can access the chat");
        }

        MessageReaction messageReaction = messageReactionRepository.findByMessageIdAndUserId(messageId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.MESSAGE_REACTION_NOT_FOUND, "No Message Reaction found!"
                ));

        messageReactionRepository.delete(messageReaction);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageReactionResponse> getMessageReactions(UUID messageId) {
        UUID userId = jwtAuthUtil.getCurrentUserId();

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.MESSAGE_NOT_FOUND, "The message can't be loaded at the moment."
                ));

        boolean isChannelMember = channelMemberRepository
                .existsByChannelIdAndUserId(message.getChannel().getId(), userId);
        if(!isChannelMember) {
            throw new BadRequestException(ErrorCode.FORBIDDEN, "Only Channel Members can access the chat");
        }

        return messageReactionRepository.findAllByMessageId(messageId)
                .stream()
                .map(messageReactionMapper::toMessageReactionResponse)
                .toList();
    }

}
