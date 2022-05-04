package com.twitter.clone.repository;

import com.twitter.clone.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User,Long> {
    public Optional<User> findByScreenName(String screenName);

    @Query("SELECT u FROM User u WHERE u.email = :email")
    public Optional<User> findByEmailId(@Param("email") String email);
}
