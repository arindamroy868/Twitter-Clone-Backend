package com.twitter.clone.entity;

import com.twitter.clone.dto.UserDTO;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long id;

    @Column(name = "name",nullable = false)
    private String name;

    @Column(name = "screen_name",nullable = false,unique = true)
    private String screenName;

    @Column(name = "description")
    private String description;

    @Column(name = "_protected",nullable = false)
    private Boolean _protected = false;


    @Column(name = "verified",nullable = false)
    private Boolean verified = false;

    @Column(name = "created_at",columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "email",unique = true,nullable = false)
    private String email;

    @OneToMany(mappedBy = "user",cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    Set<Tweet> tweets = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "follower_following",
            joinColumns = @JoinColumn(name = "follower_id", referencedColumnName = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "following_id", referencedColumnName = "user_id"))
    private Set<User> following = new HashSet<>();

    @ManyToMany(mappedBy = "following")
    private Set<User> followers = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "tweets_liked",
            joinColumns = @JoinColumn(name = "user_id",referencedColumnName = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "tweet_id",referencedColumnName = "tweet_id"))
    private Set<Tweet> likedTweets = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "retweets",
            joinColumns = @JoinColumn(name = "user_id",referencedColumnName = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "tweet_id",referencedColumnName = "tweet_id"))
    private Set<Tweet> retweets = new HashSet<>();

    public UserDTO toUserDTO(){
        UserDTO userDTO = new UserDTO();
        userDTO.setId(getId());
        userDTO.set_protected(get_protected());
        userDTO.setDescription(getDescription());
        userDTO.setCreatedAt(getCreatedAt());
        userDTO.setName(getName());
        userDTO.setScreenName(getScreenName());
        userDTO.setEmail(getEmail());
        return userDTO;
    }
    public void addFollowing(User following){
        this.getFollowing().add(following);
        following.getFollowers().add(this);
    }
    public void removeFollowing(User following){
        this.following.remove(following);
        following.getFollowers().remove(this);
    }

    public void removeFollowings(){ this.following = null; }
    public void removeFollowers(){ this.followers = null; }
    public void removeLikedTweets(){
        this.likedTweets = null;
    }
    public void removeRetweets(){
        this.retweets = null;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", screenName='" + screenName + '\'' +
                ", description='" + description + '\'' +
                ", _protected=" + _protected +
                ", verified=" + verified +
                ", createdAt=" + createdAt +
                ", email='" + email + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(name, user.name) && Objects.equals(screenName, user.screenName) && Objects.equals(description, user.description) && Objects.equals(_protected, user._protected) && Objects.equals(verified, user.verified) && Objects.equals(createdAt, user.createdAt) && Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, screenName, description, _protected, verified, createdAt, email);
    }
}
