package com.twitter.clone.service;

import com.twitter.clone.dto.TweetDTO;
import com.twitter.clone.entity.User;
import com.twitter.clone.exception.TwitterException;

import java.util.List;

public interface TweetService {
    TweetDTO createTweet(TweetDTO tweetDTO) throws TwitterException;
    TweetDTO getTweet(Long tweetId) throws TwitterException;
    List<TweetDTO> getAllTweets(Long userId) throws TwitterException;
    void deleteTweet(Long tweetId) throws TwitterException;
    TweetDTO replyTweet(TweetDTO tweetDTO,Long replyId) throws TwitterException;
    void likeTweet(Long tweetId,Long userId) throws TwitterException;
    void dislikeTweet(Long tweetId,Long userId) throws TwitterException;
    TweetDTO retweet(Long tweetId,Long userId) throws TwitterException;
    void undoRetweet(Long tweetId,Long userId) throws TwitterException;
    List<User> getLikeUsers(Long tweetId) throws TwitterException;
    List<User> getRetweetUsers(Long tweetId) throws TwitterException;
}
