package com.twitter.clone.service;

import com.twitter.clone.dto.TweetDTO;
import com.twitter.clone.exception.TwitterException;

import java.util.List;

public interface TweetService {
    public TweetDTO createTweet(TweetDTO tweetDTO) throws TwitterException;
    public TweetDTO getTweet(Long tweetId) throws TwitterException;
    List<TweetDTO> getAllTweets(Long userId) throws TwitterException;
    public void deleteTweet(Long tweetId) throws TwitterException;
    public TweetDTO replyTweet(TweetDTO tweetDTO,Long replyId) throws TwitterException;
    public void likeTweet(Long tweetId,Long userId) throws TwitterException;
    public void dislikeTweet(Long tweetId,Long userId) throws TwitterException;
    public TweetDTO retweet(Long tweetId,Long userId) throws TwitterException;
    public void undoRetweet(Long tweetId,Long userId) throws TwitterException;
}
