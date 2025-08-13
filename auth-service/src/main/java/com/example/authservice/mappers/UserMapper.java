package com.example.authservice.mappers;

import com.example.authservice.DTO.UserDTO;
import com.example.authservice.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    UserDTO userToUserDTO(User user);
    User userDTOToUser(UserDTO userDTO);
    List<UserDTO> usersToUserDTOs(List<User> users);
}
