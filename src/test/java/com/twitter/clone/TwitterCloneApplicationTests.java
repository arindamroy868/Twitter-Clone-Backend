package com.twitter.clone;

import com.twitter.clone.entity.User;
import com.twitter.clone.exception.TwitterException;
import com.twitter.clone.repository.UserRepository;
import com.twitter.clone.service.UserService;
import com.twitter.clone.service.UserServiceImpl;
import org.apache.tomcat.util.buf.UEncoder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.swing.text.html.Option;
import java.util.Optional;

import static org.mockito.Mockito.when;

@SpringBootTest
class TwitterCloneApplicationTests {

	@InjectMocks
	UserService userService = new UserServiceImpl();

	@Mock
	UserRepository userRepository;

	private static User user1, user2;
	@BeforeAll
	public static void createUsers(){
		user1 = new User(); user2 = new User();
		user1.setName("user1"); user1.setEmail("user1@email.com"); user1.setScreenName("user1"); user1.setId(1l);
		user2.setName("user2"); user2.setEmail("user2@email.com"); user2.setScreenName("user2"); user2.setId(2l);
	}

	@AfterAll
	public static void deleteUsersFromTable(){
		user1 = null; user2 = null;
	}


	@Test
	public void getUserWithValidId() throws TwitterException {
		when(userRepository.findById(1l)).thenReturn(Optional.of(user1));
		Assertions.assertEquals(userService.getUser(1l),user1.toUserDTO());
	}

	@Test
	public void getUserWithInvalidId() throws TwitterException {
		when(userRepository.findById(3l)).thenReturn(Optional.ofNullable(null));
		Exception e = Assertions.assertThrows(TwitterException.class,()->userService.getUser(3l));
		Assertions.assertEquals(e.getMessage(),"User not present");
	}

}
