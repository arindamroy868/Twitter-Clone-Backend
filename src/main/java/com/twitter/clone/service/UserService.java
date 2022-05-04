package com.twitter.clone.service;

import com.twitter.clone.dto.UserDTO;
import com.twitter.clone.exception.TwitterException;

import java.util.List;

public interface UserService {
    public List<UserDTO> getUsers();
    public UserDTO createUser(UserDTO userDTO) throws TwitterException;
    public UserDTO getUser(Long id) throws TwitterException;
}