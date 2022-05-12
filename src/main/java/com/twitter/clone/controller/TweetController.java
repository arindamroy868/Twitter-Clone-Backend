package com.twitter.clone.controller;

import com.twitter.clone.dto.TweetDTO;
import com.twitter.clone.dto.UserDTO;
import com.twitter.clone.entity.User;
import com.twitter.clone.exception.TwitterException;
import com.twitter.clone.service.TweetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/tweets")
@Validated
public class TweetController {
    @Autowired
    TweetService tweetService;

    @PostMapping("/create")
    public ResponseEntity<TweetDTO> createTweet(@Valid @RequestBody TweetDTO tweetDTO) throws TwitterException {
        TweetDTO savedTweet = tweetService.createTweet(tweetDTO);
        return new ResponseEntity<>(savedTweet, HttpStatus.CREATED);
    }

    @GetMapping("/{tweetId}")
    public ResponseEntity<TweetDTO> getTweet(@PathVariable(value = "tweetId") @Min(value = 1) Long tweetId) throws TwitterException{
        TweetDTO tweetDTO = tweetService.getTweet(tweetId);
        return new ResponseEntity<>(tweetDTO,HttpStatus.OK);
    }

    @GetMapping("/all/{userId}")
    public ResponseEntity<List<TweetDTO>> getAllTweets(@PathVariable(value = "userId") @Min(value = 1) Long userId) throws TwitterException{
        List<TweetDTO> tweetDTOList = tweetService.getAllTweets(userId);
        return new ResponseEntity<>(tweetDTOList,HttpStatus.OK);
    }

    @DeleteMapping("/delete/{tweetId}")
    public ResponseEntity<String> deleteTweet(@PathVariable(value = "tweetId") @Min(value = 1) Long tweetId) throws TwitterException{
        tweetService.deleteTweet(tweetId);
        return new ResponseEntity<>("Tweet Deleted",HttpStatus.OK);
    }

    @GetMapping("/like/{tweetId}/{userId}")
    public ResponseEntity<String> likeTweet(@PathVariable(value = "tweetId") @Min(value = 1) Long tweetId,
        @PathVariable(value = "userId") @Min(value = 1) Long userId ) throws TwitterException{
        tweetService.likeTweet(tweetId,userId);
        return new ResponseEntity<>("Liked Tweet",HttpStatus.OK);
    }

    @GetMapping("/dislike/{tweetId}/{userId}")
    public ResponseEntity<String> dislikeTweet(@PathVariable(value = "tweetId") @Min(value = 1) Long tweetId,
        @PathVariable(value = "userId") @Min(value = 1) Long userId ) throws TwitterException{
        tweetService.dislikeTweet(tweetId,userId);
        return new ResponseEntity<>("Disliked Tweet",HttpStatus.OK);
    }

    @GetMapping("/retweet/{tweetId}/{userId}")
    public ResponseEntity<TweetDTO> retweet(@PathVariable(value = "tweetId") @Min(value = 1) Long tweetId,
        @PathVariable(value = "userId") @Min(value = 1) Long userId) throws TwitterException{
        TweetDTO tweetDTO = tweetService.retweet(tweetId,userId);
        return new ResponseEntity<>(tweetDTO,HttpStatus.CREATED);
    }

    @DeleteMapping("/undoretweet/{tweetId}/{userId}")
    public ResponseEntity<String> undoRetweet(@PathVariable(value = "tweetId") @Min(value = 1) Long tweetId,
        @PathVariable(value = "userId") @Min(value = 1) Long userId) throws TwitterException{
        tweetService.undoRetweet(tweetId,userId);
        return new ResponseEntity<>("Undo retweet",HttpStatus.OK);
    }

    @PostMapping("/reply/{tweetId}")
    public ResponseEntity<TweetDTO> replyTweet(@Valid @RequestBody TweetDTO tweetDTO,
        @PathVariable(value = "tweetId") @Min(value = 1) Long tweetId) throws TwitterException{
        TweetDTO reply = tweetService.replyTweet(tweetDTO,tweetId);
        return new ResponseEntity<>(reply,HttpStatus.CREATED);
    }

    @GetMapping("/likes/{tweetId}")
    public ResponseEntity<List<UserDTO>> getLikeUsers(@PathVariable(value = "tweetId") @Min(value = 1) Long tweetId) throws TwitterException{
        List<UserDTO> userList = tweetService.getLikeUsers(tweetId);
        return new ResponseEntity<>(userList,HttpStatus.OK);
    }

    @GetMapping("/retweets/{tweetId}")
    public ResponseEntity<List<UserDTO>> getRetweetUsers(@PathVariable(value = "tweetId") @Min(value = 1) Long tweetId) throws TwitterException{
        List<UserDTO> userList = tweetService.getRetweetUsers(tweetId);
        return new ResponseEntity<>(userList,HttpStatus.OK);
    }

}
