package com.twitter.clone.service;

import com.twitter.clone.dto.UserDTO;
import com.twitter.clone.entity.User;
import com.twitter.clone.exception.TwitterException;
import com.twitter.clone.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service(value = "userService")
public class UserServiceImpl implements UserService{

    @Autowired
    UserRepository userRepository;

    @Override
    public List<UserDTO> getUsers() {
        Iterable<User> users = userRepository.findAll();
        List<UserDTO> userDTOList = new ArrayList<>();

        users.forEach(user -> {
            userDTOList.add(user.toUserDTO());
        });

        return userDTOList;
    }

    @Override
    @Transactional
    public UserDTO createUser(UserDTO userDTO) throws TwitterException {
        if(!userRepository.findByScreenName(userDTO.getScreenName()).isEmpty()){
            throw new TwitterException("Username is already in use. Please choose other username!");
        }
        if(!userRepository.findByEmailId(userDTO.getEmail()).isEmpty()){
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
        return user.toUserDTO();
    }


}
