package com.example.bankcards.repository;

import com.example.bankcards.entity.CardRequest;
import com.example.bankcards.entity.RequestStatus;
import com.example.bankcards.entity.RequestType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRequestRepository extends JpaRepository<CardRequest, Integer> {

    Page<CardRequest> findByStatus(RequestStatus status, Pageable pageable);

    boolean existsByUserIdAndCardIdAndRequestTypeAndStatus(
            Integer userId, Integer cardId, RequestType requestType, RequestStatus status);
}
