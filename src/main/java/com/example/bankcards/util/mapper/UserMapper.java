package com.example.bankcards.util.mapper;

import com.example.bankcards.dto.UserQuery;
import com.example.bankcards.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper extends Mappable<User, UserQuery> {
}
