package com.twitter.clone.service;

import com.twitter.clone.dto.TweetDTO;
import com.twitter.clone.dto.UserDTO;
import com.twitter.clone.entity.Tweet;
import com.twitter.clone.entity.User;
import com.twitter.clone.exception.TwitterException;
import com.twitter.clone.repository.TweetRepository;
import com.twitter.clone.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service(value = "tweetService")
public class TweetServiceImpl implements TweetService{

    @Autowired
    TweetRepository tweetRepository;

    @Autowired
    UserRepository userRepository;

    private User getUser(Long userId) throws TwitterException{
        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalUser.isEmpty()){
            throw new TwitterException("Invalid User Id");
        }
        return optionalUser.get();
    }

    @Override
    @Transactional @Modifying
    public TweetDTO createTweet(TweetDTO tweetDTO) throws TwitterException{
        User user = getUser(tweetDTO.getUserId());
        Tweet tweet = new Tweet();
        tweet.setText(tweetDTO.getText());
        tweet.setSource(tweetDTO.getSource());
        tweet.setUser(user);
        tweet = tweetRepository.save(tweet);
        TweetDTO savedTweetDTO = tweet.toTweetDTO();
        savedTweetDTO.setFavoriteCount(getFavouriteCount(tweet));
        savedTweetDTO.setReplyCount(getReplyCount(tweet));
        savedTweetDTO.setRetweetCount(getRetweetCount(tweet));
        return savedTweetDTO;
    }

    @Override
    public TweetDTO getTweet(Long tweetId) throws TwitterException{
        Optional<Tweet> optionalTweet = tweetRepository.findById(tweetId);
        if(optionalTweet.isEmpty()){
            throw new TwitterException("Invalid Tweet Id");
        }
        Tweet tweet = optionalTweet.get();
        TweetDTO savedTweetDTO = tweet.toTweetDTO();
        setCounts(tweet,savedTweetDTO);
        return savedTweetDTO;
    }

    @Override
    public List<TweetDTO> getAllTweets(Long userId) throws TwitterException{
        User user = getUser(userId);
        List<Tweet> tweetList = tweetRepository.findByUser(user);
        return tweetList.stream().map(tweet -> { TweetDTO tweetDTO = tweet.toTweetDTO();
            try {
                setCounts(tweet,tweetDTO);
            } catch (TwitterException e) {
                e.printStackTrace();
            } return tweetDTO;})
            .collect(Collectors.toList());

    }

    @Override
    @Transactional @Modifying
    public void deleteTweet(Long tweetId) throws TwitterException{
        Tweet tweet = tweetRepository.findById(tweetId).orElseThrow(()->new TwitterException("Invalid Tweet Id"));

        //If it is a retweet then we call undo retweet method
        if(tweet.getRetweetStatus() != null){
            undoRetweet(tweet.getRetweetStatus().getId(),tweet.getUser().getId());
            return;
        }
        //Remove entry from retweet table
        for(User user : new HashSet<>(tweet.getRetweetUsers())){
            tweet.removeRetweetUser(user);
        }
        //Remove entry from tweets_liked table
        for(User user : new HashSet<>(tweet.getLikeUsers())){
            tweet.removeLikeUser(user);
        }

        tweetRepository.delete(tweet);
    }

    @Override
    @Transactional @Modifying
    public TweetDTO replyTweet(TweetDTO tweetDTO,Long originalTweetId) throws TwitterException {
        User user = getUser(tweetDTO.getUserId());
        //Creating new Tweet Object
        Tweet tweet = new Tweet();
        tweet.setText(tweetDTO.getText());
        tweet.setSource(tweetDTO.getSource());
        tweet.setUser(user);

        //Replying to the tweet
        Optional<Tweet> optionalOriginalTweet = tweetRepository.findById(originalTweetId);
        if(optionalOriginalTweet.isEmpty()){
            throw new TwitterException("Original tweet no longer exists");
        }
        Tweet originalTweet = optionalOriginalTweet.get();
        tweet.setRepliedTweet(originalTweet);
        tweet = tweetRepository.save(tweet);
        TweetDTO savedTweetDTO = tweet.toTweetDTO();
        setCounts(tweet,savedTweetDTO);
        return savedTweetDTO;
    }

    @Override
    @Transactional @Modifying
    public void likeTweet(Long tweetId, Long userId) throws TwitterException {
        User user = getUser(userId);
        Optional<Tweet> optionalTweet = tweetRepository.findById(tweetId);
        if(optionalTweet.isEmpty()){
            throw new TwitterException("Invalid Tweet Id");
        }
        Tweet tweet = optionalTweet.get();
        if(tweet.getRetweetStatus() != null) {
            tweet = tweet.getRetweetStatus();
        }
        tweet.addLikeUser(user);
        userRepository.save(user);
        tweetRepository.save(tweet);
    }

    @Override
    @Transactional @Modifying
    public void dislikeTweet(Long tweetId, Long userId) throws TwitterException {
        User user = getUser(userId);
        Optional<Tweet> optionalTweet = tweetRepository.findById(tweetId);
        if(optionalTweet.isEmpty()){
            throw new TwitterException("Invalid Tweet Id");
        }
        Tweet tweet = optionalTweet.get();
        if(tweet.getRetweetStatus()!=null){
            tweet = tweet.getRetweetStatus();
        }
        tweet.removeLikeUser(user);

        userRepository.save(user);
        tweetRepository.save(tweet);
    }

    @Override @Transactional @Modifying
    public TweetDTO retweet(Long tweetId,Long userId) throws TwitterException{
        Tweet tweet = tweetRepository.findById(tweetId).orElseThrow(()->new TwitterException("Invalid Tweet Id"));
        User user = getUser(userId);
        //If the tweet is a retweet itself
        if(tweet.getRetweetStatus() != null){
            //Then we set tweet to the original tweet
            tweet = tweet.getRetweetStatus();
        }
        //If user has already retweeted the original tweet we simply return
        if(user.getRetweets().contains(tweet)){
            return tweetRepository.findById(tweet.getId()).orElseThrow().toTweetDTO();
        }
        //Retweet is a new tweet
        Tweet retweet = new Tweet();
        retweet.setUser(user);
        retweet.setRetweetStatus(tweet);
        retweet.setText(tweet.getText());
        //Create entry in retweet table;
        tweet.addRetweetUser(user);
        userRepository.save(user);
        TweetDTO tweetDTO = tweetRepository.save(retweet).toTweetDTO();
        setCounts(tweet,tweetDTO);
        return tweetDTO;
    }

    @Override @Transactional @Modifying
    public void undoRetweet(Long tweetId,Long userId) throws TwitterException{
        Tweet tweet = tweetRepository.findById(tweetId).orElseThrow(()->new TwitterException("Invalid Tweet Id"));
        User user = getUser(userId);
        //Remove entry from retweet table
        tweet.removeRetweetUser(user);
        //Get the retweet
        Tweet retweet = tweetRepository.findByRetweetStatusAndUser(tweet,user);
        //Delete the retweet
        tweetRepository.delete(retweet);
        userRepository.save(user);
        tweetRepository.save(tweet);
    }

    public long getFavouriteCount(Tweet tweet){
        return tweet.getLikeUsers() != null ? tweet.getLikeUsers().size() : 0L;
    }

    public long getRetweetCount(Tweet tweet){
        return tweet.getRetweetUsers() != null ? tweet.getRetweetUsers().size() : 0L;
    }

    public long getReplyCount(Tweet tweet) {
        List<Tweet> replies = tweetRepository.findByRepliedTweet(tweet);
        return replies.size();
    }

    public void setCounts(Tweet tweet,TweetDTO savedTweetDTO) throws TwitterException{
        savedTweetDTO.setFavoriteCount(getFavouriteCount(tweet.getRetweetStatus() == null ? tweet : tweet.getRetweetStatus()));
        savedTweetDTO.setReplyCount(getReplyCount(tweet.getRetweetStatus() == null ? tweet : tweet.getRetweetStatus()));
        savedTweetDTO.setRetweetCount(getRetweetCount(tweet.getRetweetStatus() == null ? tweet : tweet.getRetweetStatus()));
    }

    @Override
    public List<UserDTO> getLikeUsers(Long tweetId) throws TwitterException{
        Tweet tweet = tweetRepository.findById(tweetId).orElseThrow(()-> new TwitterException("Invalid Tweet Id"));
        return tweet.getLikeUsers().stream().map(User::toUserDTO).collect(Collectors.toList());
    }

    @Override
    public List<UserDTO> getRetweetUsers(Long tweetId) throws TwitterException {
        Tweet tweet = tweetRepository.findById(tweetId).orElseThrow(() -> new TwitterException("Invalid Tweet Id"));
        return tweet.getRetweetUsers().stream().map(User::toUserDTO).collect(Collectors.toList());
    }
}
