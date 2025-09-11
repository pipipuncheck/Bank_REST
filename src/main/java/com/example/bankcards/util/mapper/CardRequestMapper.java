package com.example.bankcards.util.mapper;

import com.example.bankcards.dto.CardRequestQuery;
import com.example.bankcards.entity.CardRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CardRequestMapper extends Mappable<CardRequest, CardRequestQuery> {

    @Mapping(target = "fullName", source = "user.username")
    @Mapping(target = "cardNumber", source = "card.number")
    CardRequestQuery toDTO(CardRequest cardRequest);

}
