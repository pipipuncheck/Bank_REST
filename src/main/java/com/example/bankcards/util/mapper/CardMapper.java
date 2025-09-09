package com.example.bankcards.util.mapper;

import com.example.bankcards.dto.CardQuery;
import com.example.bankcards.entity.Card;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface CardMapper extends Mappable<Card, CardQuery> {
}
