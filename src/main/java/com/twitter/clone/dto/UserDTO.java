package com.twitter.clone.dto;

import com.twitter.clone.entity.User;
import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Getter @Setter @NoArgsConstructor @ToString @EqualsAndHashCode
public class UserDTO {
    private Long id;

    @NotBlank(message = "Name should not be blank")
    @Size(min = 3,max = 50)
    private String name;

    @NotBlank(message = "Username should not be blank")
    @Size(min = 4,max = 50)
    private String screenName;

    @Size(max = 265)
    private String description;

    private boolean _protected;
    private boolean verified;
    private int followersCount;
    private int friendsCount;
    private int statusesCount;
    private LocalDateTime createdAt;

    @Email
    @NotBlank(message = "Email should not be blank")
    @Size(min = 5,max = 50)
    private String email;

    private List<Long> userFollowing;
    private List<Long> retweets;
    private List<Long> likedTweets;

    public User toUser(){
        User user = new User();
        user.setName(getName());
        user.setScreenName(getScreenName());
        user.setEmail(getEmail());
        user.setDescription(getDescription());
        return user;
    }

}
