package com.picktartup.wallet.repository;

import com.picktartup.wallet.entity.TokenTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenTransactionRepository extends JpaRepository<TokenTransaction, Long> {
}
