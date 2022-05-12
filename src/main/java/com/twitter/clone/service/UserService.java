package com.twitter.clone.service;

import com.twitter.clone.dto.TweetDTO;
import com.twitter.clone.dto.UserDTO;
import com.twitter.clone.exception.TwitterException;

import java.util.List;

public interface UserService {
    List<UserDTO> getUsers();
    UserDTO createUser(UserDTO userDTO) throws TwitterException;
    UserDTO getUser(Long userId) throws TwitterException;
    UserDTO followUser(Long userId1,Long userId2) throws TwitterException;
    UserDTO unfollowUser(Long userId1,Long userId2) throws TwitterException;
    List<TweetDTO> getLikedTweets(Long userId) throws TwitterException;
    List<UserDTO> getFollowers(Long userId) throws TwitterException;
    List<UserDTO> getFollowings(Long userId) throws TwitterException;
    void deleteUser(Long userId) throws TwitterException;
}