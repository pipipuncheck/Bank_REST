package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, Integer> {

    Page<Card> findAll(Pageable pageable);

    Page<Card> findByUserId(Integer userId, Pageable pageable);

    Page<Card> findByUserIdAndNumberContaining(Integer userId, String searchTerm, Pageable pageable);

    List<Card> findByValidityPeriodBeforeAndStatusNot(LocalDate date, CardStatus status);
}
