package com.twitter.clone.controller;

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
}
