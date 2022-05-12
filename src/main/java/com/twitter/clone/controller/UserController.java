package com.twitter.clone.controller;

import com.twitter.clone.dto.TweetDTO;
import com.twitter.clone.dto.UserDTO;
import com.twitter.clone.exception.TwitterException;
import com.twitter.clone.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/users")
@Validated
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping(value = "")
    public ResponseEntity<List<UserDTO>> getAllUsers(){
        return new ResponseEntity<>(userService.getUsers(), HttpStatus.OK);
    }

    @PostMapping(value = "/register")
    public ResponseEntity<UserDTO> addUser(@Valid @RequestBody UserDTO userDTO) throws TwitterException {
        UserDTO savedUser = userService.createUser(userDTO);
        return new ResponseEntity<>(savedUser,HttpStatus.CREATED);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable(value = "id") Long id) throws TwitterException {
        UserDTO userDTO = userService.getUser(id);
        return new ResponseEntity<>(userDTO,HttpStatus.OK);
    }

    @GetMapping(value = "/likes/{id}")
    public ResponseEntity<List<TweetDTO>> getLikedTweets(@PathVariable(value = "id") Long id) throws TwitterException {
        List<TweetDTO> tweetDTOList = userService.getLikedTweets(id);
        return new ResponseEntity<>(tweetDTOList,HttpStatus.OK);
    }

    @GetMapping(value = "/follow/{followingId}/{followerId}")
    public ResponseEntity<UserDTO> followUser(@PathVariable(value = "followingId") Long followingId,
                 @PathVariable(value = "followerId") Long followerId) throws TwitterException {
        return new ResponseEntity<>(userService.followUser(followingId,followerId),HttpStatus.OK);
    }

    @GetMapping(value = "/unfollow/{followingId}/{followerId}")
    public ResponseEntity<UserDTO> unfollowUser(@PathVariable(value = "followingId") Long followingId,
                                              @PathVariable(value = "followerId") Long followerId) throws TwitterException {
        return new ResponseEntity<>(userService.unfollowUser(followingId,followerId),HttpStatus.OK);
    }

    @GetMapping(value = "/followings/{id}")
    public ResponseEntity<List<UserDTO>> getFollowings(@PathVariable(value = "id") Long id) throws TwitterException{
        List<UserDTO> followings = userService.getFollowings(id);
        return new ResponseEntity<>(followings,HttpStatus.OK);
    }

    @GetMapping(value = "/followers/{id}")
    public ResponseEntity<List<UserDTO>> getFollowers(@PathVariable(value = "id") Long id) throws TwitterException{
        List<UserDTO> followers = userService.getFollowers(id);
        return new ResponseEntity<>(followers,HttpStatus.OK);
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable(value = "id") Long id) throws TwitterException{
        userService.deleteUser(id);
        return new ResponseEntity<>("User Deleted",HttpStatus.NO_CONTENT);
    }
}
