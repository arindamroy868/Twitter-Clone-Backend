package com.twitter.clone.dto;

import com.twitter.clone.entity.User;
import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

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
    private long followingCount;
    private long followersCount;
    private long tweetsCount;
    private long likesCount;
    private LocalDateTime createdAt;

    @Email
    @NotBlank(message = "Email should not be blank")
    @Size(min = 5,max = 50)
    private String email;

    public User toUser(){
        User user = new User();
        user.setName(getName());
        user.setScreenName(getScreenName());
        user.setEmail(getEmail());
        user.setDescription(getDescription());
        return user;
    }

}
