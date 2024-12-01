package com.picktartup.wallet.repository;

import com.picktartup.wallet.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByUserId(Long userId);
    Optional<Wallet> findByAddress(String address);
    boolean existsByUserId(Long userId);
    boolean existsByAddress(String address);
}
