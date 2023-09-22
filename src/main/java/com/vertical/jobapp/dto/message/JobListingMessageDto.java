package com.vertical.jobapp.dto.message;

import com.vertical.jobapp.dto.rest.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobListingMessageDto {
    private MessageAction action;
    private JobListingDTO jobListing;

}
