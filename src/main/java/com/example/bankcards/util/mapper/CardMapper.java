package com.example.bankcards.util.mapper;

import com.example.bankcards.dto.CardQuery;
import com.example.bankcards.entity.Card;
import com.example.bankcards.util.DataEncryptor;
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
    default String maskCardNumber(String encryptedNumber) {
        try {
            String decryptedNumber = DataEncryptor.decrypt(encryptedNumber);
            return MakeMaskCardNumber.maskCardNumber(decryptedNumber);
        } catch (Exception e) {
            return "**** **** **** ****";
        }
    }
}
