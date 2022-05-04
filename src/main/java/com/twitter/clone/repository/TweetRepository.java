package com.twitter.clone.repository;

import com.twitter.clone.entity.Tweet;
import com.twitter.clone.entity.User;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface TweetRepository extends CrudRepository<Tweet,Long> {
    public List<Tweet> findByUser(User user);
    public List<Tweet> findByRetweetStatus(Tweet tweet);
    public Tweet findByRetweetStatusAndUser(Tweet tweet,User user);
    public List<Tweet> findByRepliedTweet(Tweet tweet);
}
