package com.pbl6.VehicleBookingRental.user.repository.account;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import com.pbl6.VehicleBookingRental.user.domain.account.Account;


import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository  extends JpaRepository<Account, Integer>, JpaSpecificationExecutor<Account>{
    Optional<Account> findByEmail(String email);
    Optional<Account> findByPhoneNumber(String phoneNumber);
    Optional<Account> findByToken(String token);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);

    Optional<Account> findByRefreshTokenAndEmail(String refreshToken, String email);
    Optional<Account> findByRefreshTokenAndPhoneNumber(String refreshToken, String phoneNumber);
    @Query("SELECT a FROM Account a " +
            "WHERE a.verified = false ")
    List<Account> findAccountNotVerify();
//    @Query("select a.name from Account a " +
//            "JOIN a.accountRole ar " +
//            "JOIN ar.role r " +
//            "where r.name != 'ADMIN'")
//    Page<String> getAccountUser(Specification<Account> spec, Pageable pageable);
}
