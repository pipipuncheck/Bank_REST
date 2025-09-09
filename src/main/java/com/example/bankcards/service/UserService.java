package com.example.bankcards.service;

import com.example.bankcards.dto.CardCommand;
import com.example.bankcards.dto.UserQuery;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserQuery> getAll(){

        return userMapper.toDTO(userRepository.findAll());

    }


}
