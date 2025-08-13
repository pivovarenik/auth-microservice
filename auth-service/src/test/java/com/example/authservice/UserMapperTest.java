package com.example.authservice;

import com.example.authservice.DTO.UserDTO;
import com.example.authservice.enums.Role;
import com.example.authservice.mappers.UserMapper;
import com.example.authservice.models.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testUserToUserDTO() {
        User user = new User();
        user.setUsername("oleg12");
        user.setFirstName("Артем");
        user.setLastName("Слонов");
        user.setRole(Role.USER);

        UserDTO userDTO = userMapper.userToUserDTO(user);

        assertNotNull(userDTO);
        assertEquals("oleg12", userDTO.username());
        assertEquals("Артем", userDTO.firstName());
        assertEquals("Слонов", userDTO.lastName());
        assertEquals(Role.USER, userDTO.role());
    }
}