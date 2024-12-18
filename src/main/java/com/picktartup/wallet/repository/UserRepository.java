package com.picktartup.wallet.repository;

import com.picktartup.wallet.entity.Users;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {

  // 모든 Wallet 목록 조회
  List<Users> findAll();

  // ID로 Wallet 조회
  Optional<Users> findById(Long walletId);

  @Query("SELECT u.wallet.address FROM Users u WHERE u.userId = :userId")
  Optional<String> findWalletAddressByUserId(@Param("userId") Long userId);

}
