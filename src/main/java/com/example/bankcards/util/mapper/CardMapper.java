package com.example.bankcards.util.mapper;

import com.example.bankcards.dto.CardQuery;
import com.example.bankcards.entity.Card;
import com.example.bankcards.util.MakeMaskCardNumber;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;


@Mapper(componentModel = "spring")
public interface CardMapper extends Mappable<Card, CardQuery> {

    @Mapping(target = "number",
            source = "number",
            qualifiedByName = "maskCardNumber")
    @Mapping(target = "fullName", source = "user.username")
    CardQuery toDTO(Card card);

    @Named("maskCardNumber")
    default String maskCardNumber(String number) {
        return MakeMaskCardNumber.maskCardNumber(number);
    }
}
