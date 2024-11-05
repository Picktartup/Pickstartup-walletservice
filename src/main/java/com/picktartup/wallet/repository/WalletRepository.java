package com.picktartup.wallet.repository;

import com.picktartup.wallet.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    // 모든 Wallet 목록 조회
    List<Wallet> findAll();

    // ID로 Wallet 조회
    Optional<Wallet> findById(Long walletId);

    // 주소로 Wallet 조회
    Optional<Wallet> findByAddress(String address);

    // 지갑 주소를 기준으로 지갑 정보 삭제
    void deleteByAddress(String address);

    String findAddressByWalletId(Long walletId);

    // 잔액 업데이트
    @Modifying
    @Query("UPDATE Wallet w SET w.balance = :balance WHERE w.address = :address")
    int updateBalance(@Param("address") String address, @Param("balance") BigDecimal balance);
}
