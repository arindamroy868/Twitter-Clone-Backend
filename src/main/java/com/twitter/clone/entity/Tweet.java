package com.twitter.clone.entity;

import com.twitter.clone.dto.TweetDTO;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "tweets")
@Getter @Setter
public class Tweet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tweet_id", nullable = false,unique = true)
    private Long id;

    @Column(name = "text",nullable = false)
    private String text;

    @Column(name = "source")
    private String source="Twitter Web Client";

    @ManyToOne()
    @JoinColumn(name = "replied_tweet_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Tweet repliedTweet;

    @ManyToOne()
    @JoinColumn(name = "retweet_status")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Tweet retweetStatus;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToMany(mappedBy = "retweets")
    private Set<User> retweetUsers = new HashSet<>();

    @ManyToMany(mappedBy = "likedTweets")
    private Set<User> likeUsers = new HashSet<>();

    public void addRetweetUser(User user){
        if(retweetUsers == null) retweetUsers = new HashSet<>();
        retweetUsers.add(user);
        user.getRetweets().add(this);
    }

    public void removeRetweetUser(User user){
        retweetUsers.remove(user);
        user.getRetweets().remove(this);
    }

    public void addLikeUser(User user){
        if(likeUsers == null) likeUsers = new HashSet<>();
        likeUsers.add(user);
        user.getLikedTweets().add(this);
    }

    public void removeLikeUser(User user){
        likeUsers.remove(user);
        user.getLikedTweets().remove(this);
    }

    public TweetDTO toTweetDTO(){
        TweetDTO tweetDTO = new TweetDTO();
        tweetDTO.setId(getId());
        tweetDTO.setText(getText());
        tweetDTO.setSource(getSource());
        tweetDTO.setUserId(getUser().getId());
        if(getRetweetStatus() != null) tweetDTO.setRetweetStatus(getRetweetStatus().getId());
        if(this.getRepliedTweet() != null) tweetDTO.setRepliedTweetId(getRepliedTweet().getId());
        return tweetDTO;
    }

    @Override
    public String toString() {
        return "Tweet{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", source='" + source + '\'' +
                ", in_reply_to_tweet_id=" + repliedTweet +
                ", retweetStatus=" + retweetStatus +
                ", user=" + user +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, text, source, repliedTweet, retweetStatus, user);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tweet tweet = (Tweet) o;
        return Objects.equals(id, tweet.id) && Objects.equals(text, tweet.text) && Objects.equals(source, tweet.source) && Objects.equals(repliedTweet, tweet.repliedTweet) && Objects.equals(retweetStatus, tweet.retweetStatus) && Objects.equals(user, tweet.user);
    }
}
