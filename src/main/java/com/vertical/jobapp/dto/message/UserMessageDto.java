package com.vertical.jobapp.dto.message;

import com.vertical.jobapp.dto.rest.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserMessageDto {
    private MessageAction action;
    private UserDTO user;
}