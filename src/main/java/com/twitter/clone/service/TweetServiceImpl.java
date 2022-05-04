package com.twitter.clone.service;

import com.twitter.clone.dto.TweetDTO;
import com.twitter.clone.entity.Tweet;
import com.twitter.clone.entity.User;
import com.twitter.clone.exception.TwitterException;
import com.twitter.clone.repository.TweetRepository;
import com.twitter.clone.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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
        Optional<Tweet> optionalTweet = tweetRepository.findById(tweetId);
        if(optionalTweet.isEmpty()){
            throw new TwitterException("Invalid Tweet Id");
        }
        Tweet tweet = optionalTweet.get();

        //If it is a retweet then we call undo retweet method
        if(tweet.getRetweetStatus() != null){
            undoRetweet(tweet.getRetweetStatus().getId(),tweet.getUser().getId());
            return;
        }
        for(User user : tweet.getRetweetUsers()){
            user.getRetweets().remove(tweet);
        }
        tweet.removeRetweetUsers();
        for(User user : tweet.getLikeUsers()){
            user.getLikedTweets().remove(tweet);
        }
        tweet.removeLikeUsers();
        tweetRepository.delete(tweet);
    }

    @Override
    @Transactional @Modifying
    public TweetDTO replyTweet(TweetDTO tweetDTO,Long replyId) throws TwitterException {
        User user = getUser(tweetDTO.getUserId());
        //Creating new Tweet Object
        Tweet tweet = new Tweet();
        tweet.setText(tweetDTO.getText());
        tweet.setSource(tweetDTO.getSource());
        tweet.setUser(user);

        //Replying to the tweet
        Optional<Tweet> optionalOriginalTweet = tweetRepository.findById(replyId);
        if(optionalOriginalTweet.isEmpty()){
            throw new TwitterException("Original tweet no longer exists");
        }
        Tweet originalTweet = optionalOriginalTweet.get();
        tweet.setRepliedTweet(originalTweet);
        tweetRepository.save(originalTweet);
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
        Optional<Tweet> optionalTweet = tweetRepository.findById(tweetId);
        if(optionalTweet.isEmpty()){
            throw new TwitterException("Invalid Tweet Id");
        }
        User user = getUser(userId);
        Tweet tweet = optionalTweet.get();
        if(tweet.getRetweetStatus() != null){
            tweet = tweet.getRetweetStatus();
        }
        if(user.getRetweets().contains(tweet)){
            return tweetRepository.findById(tweet.getId()).orElseThrow().toTweetDTO();
        }
        Tweet retweet = new Tweet();
        retweet.setUser(user);
        retweet.setRetweetStatus(tweet);
        retweet.setText(tweet.getText());
        tweet.addRetweetUser(user);
        userRepository.save(user);
        TweetDTO tweetDTO = tweetRepository.save(retweet).toTweetDTO();
        setCounts(tweet,tweetDTO);
        return tweetDTO;
    }

    @Override @Transactional @Modifying
    public void undoRetweet(Long tweetId,Long userId) throws TwitterException{
        Optional<Tweet> optionalTweet = tweetRepository.findById(tweetId);
        if(optionalTweet.isEmpty()){
            throw new TwitterException("Invalid Tweet Id");
        }
        User user = getUser(userId);
        Tweet tweet = optionalTweet.get();
        tweet.removeRetweetUser(user);
        userRepository.save(user);
        tweetRepository.save(tweet);
        Tweet retweet = tweetRepository.findByRetweetStatusAndUser(tweet,user);
        tweetRepository.delete(retweet);
    }

    public int getFavouriteCount(Tweet tweet){
        return tweet.getLikeUsers() != null ? tweet.getLikeUsers().size() : 0;
    }

    public int getRetweetCount(Tweet tweet){
        return tweet.getRetweetUsers() != null ? tweet.getRetweetUsers().size() : 0;
    }

    public int getReplyCount(Tweet tweet) {
        List<Tweet> replies = tweetRepository.findByRepliedTweet(tweet);
        return replies.size();
    }

    public void setCounts(Tweet tweet,TweetDTO savedTweetDTO) throws TwitterException{
        savedTweetDTO.setFavoriteCount(getFavouriteCount(tweet.getRetweetStatus() == null ? tweet : tweet.getRetweetStatus()));
        savedTweetDTO.setReplyCount(getReplyCount(tweet.getRetweetStatus() == null ? tweet : tweet.getRetweetStatus()));
        savedTweetDTO.setRetweetCount(getRetweetCount(tweet.getRetweetStatus() == null ? tweet : tweet.getRetweetStatus()));
    }
}
