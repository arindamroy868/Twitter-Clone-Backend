package com.twitter.clone.dto;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter @Setter @NoArgsConstructor @ToString @EqualsAndHashCode
public class TweetDTO {
    private Long id;

    @NotBlank(message = "Text cannot be blank")
    private String text;

    @NotBlank(message = "Specify twitter client source")
    private String source;

    private Long repliedTweetId;
    private Long repliedUserId;
    private int replyCount;
    private int retweetCount;
    private int favoriteCount;

    @Min(value = 1,message = "Invalid Tweet Id")
    private Long retweetStatus;

    @NotNull(message = "User Id is Mandatory")
    @Min(value = 1,message = "Invalid User id")
    private Long userId;

}
