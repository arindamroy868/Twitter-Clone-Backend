package com.twitter.clone.service;

import com.twitter.clone.dto.TweetDTO;
import com.twitter.clone.dto.UserDTO;
import com.twitter.clone.entity.Tweet;
import com.twitter.clone.entity.User;
import com.twitter.clone.exception.TwitterException;
import com.twitter.clone.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service(value = "userService")
public class UserServiceImpl implements UserService{

    @Autowired
    UserRepository userRepository;

    @Autowired
    TweetServiceImpl tweetService;

    @Override
    public List<UserDTO> getUsers() {
        Iterable<User> users = userRepository.findAll();
        List<UserDTO> userDTOList = new ArrayList<>();

        users.forEach(user -> {
            UserDTO userDTO = user.toUserDTO();
            setCounts(user,userDTO);
            userDTOList.add(userDTO);
        });

        return userDTOList;
    }

    @Override
    @Transactional
    public UserDTO createUser(UserDTO userDTO) throws TwitterException {
        if(userRepository.findByScreenName(userDTO.getScreenName()).isPresent()){
            throw new TwitterException("Username is already in use. Please choose other username!");
        }
        if(userRepository.findByEmailId(userDTO.getEmail()).isPresent()){
            throw new TwitterException("Email is associated with other account!");
        }
        User user = userDTO.toUser();
        user.setCreatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);
        return savedUser.toUserDTO();
    }

    @Override
    public UserDTO getUser(Long id) throws TwitterException {
        User user;
        Optional<User> userOptional = userRepository.findById(id);
        if(userOptional.isEmpty()){
            throw new TwitterException("User not present");
        }
        user = userOptional.get();
        UserDTO userDTO = user.toUserDTO();
        setCounts(user,userDTO);
        return userDTO;
    }

    @Override
    public UserDTO followUser(Long followingId, Long followerId) throws TwitterException {
        User following = userRepository.findById(followingId).orElseThrow(()->new TwitterException("Invalid User Id"));
        User follower = userRepository.findById(followerId).orElseThrow(()->new TwitterException("Invalid User Id"));
        follower.addFollowing(following);

        UserDTO userDTO = userRepository.save(follower).toUserDTO();
        setCounts(follower,userDTO);
        return userDTO;
    }

    @Override
    public UserDTO unfollowUser(Long followingId, Long followerId) throws TwitterException {
        User following = userRepository.findById(followingId).orElseThrow(()->new TwitterException("Invalid User Id"));
        User follower = userRepository.findById(followerId).orElseThrow(()->new TwitterException("Invalid User Id"));
        follower.removeFollowing(following);

        UserDTO userDTO = userRepository.save(follower).toUserDTO();
        setCounts(follower,userDTO);
        return userDTO;
    }

    @Override
    public List<UserDTO> getFollowers(Long userId) throws TwitterException{
        User user = userRepository.findById(userId).orElseThrow(() -> new TwitterException("Invalid User Id"));
        return user.getFollowers().stream().map(user1 -> {
            UserDTO userDTO = user1.toUserDTO();
            setCounts(user1,userDTO);
            return userDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public List<UserDTO> getFollowings(Long userId) throws TwitterException{
        User user = userRepository.findById(userId).orElseThrow(() -> new TwitterException("Invalid User Id"));
        return user.getFollowing().stream().map(user1 -> {
            UserDTO userDTO = user1.toUserDTO();
            setCounts(user1,userDTO);
            return userDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public List<TweetDTO> getLikedTweets(Long userId) throws TwitterException {
        User user  = userRepository.findById(userId).orElseThrow(()->new TwitterException("Invalid User Id"));

        return user.getLikedTweets().stream().map(tweet -> {
            TweetDTO tweetDTO = tweet.toTweetDTO();
            try {
                tweetService.setCounts(tweet,tweetDTO);
            } catch (TwitterException e) {
                e.printStackTrace();
            }
            return tweetDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long userId) throws TwitterException{
        User user  = userRepository.findById(userId).orElseThrow(()->new TwitterException("Invalid User Id"));
        //Remove Liked Tweets from Table tweets_liked
        for(Tweet tweet : new HashSet<>(user.getLikedTweets())){
            tweet.removeLikeUser(user);
        }
        //Remove Retweets from Table retweets
        for(Tweet tweet : new HashSet<>(user.getRetweets())){
            tweet.removeRetweetUser(user);
        }
        //Remove followings from Table follower_following
        for(User following : new HashSet<>(user.getFollowing())){
            user.removeFollowing(following);
        }
        //Remove followers from Table follower_following
        for(User follower : new HashSet<>(user.getFollowers())){
            follower.removeFollowing(user);
        }
        for(Tweet tweet : new HashSet<>(user.getTweets())){
            tweetService.deleteTweet(tweet.getId());
        }
        userRepository.delete(user);
    }

    private long getFollowingCount(User user){
        return user.getFollowing() != null ? user.getFollowing().size() : 0L;
    }

    private long getLikeCount(User user){
        return user.getLikedTweets() != null ? user.getLikedTweets().size() : 0L;
    }

    private long getTweetCount(User user){
        try{
            return tweetService.getAllTweets(user.getId()).size();

        }catch (TwitterException e){
            System.out.println(e.getMessage());
        }
        return 0;
    }

    private long getFollowerCount(User user){
        return user.getFollowers() != null ? user.getFollowers().size() : 0L;
    }

    public void setCounts(User user,UserDTO userDTO){
        userDTO.setFollowingCount(getFollowingCount(user));
        userDTO.setFollowersCount(getFollowerCount(user));
        userDTO.setLikesCount(getLikeCount(user));
        userDTO.setTweetsCount(getTweetCount(user));
    }
}
