package com.bet.platform.wallet;

import com.bet.platform.user.model.User;
import com.bet.platform.user.model.UserStatus;
import com.bet.platform.user.repository.UserRepository;
import com.bet.platform.wallet.model.Wallet;
import com.bet.platform.wallet.model.WalletStatus;
import com.bet.platform.wallet.repository.WalletRepository;
import com.bet.platform.wallet.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.bet.platform.user.model.Role;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class WalletConcurrencyTest {

    @Autowired
    private WalletService walletService;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setup() {

        user = new User();
        user.setName("Test User");
        user.setEmail(UUID.randomUUID() + "@email.com");
        user.setPassword("123456");
        user.setRole(Role.USER); // ajuste para seu enum
        user.setStatus(UserStatus.ACTIVE); // ajuste para seu enum
        user.setCreatedAt(LocalDateTime.now());

        user = userRepository.save(user);

        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setBalance(new BigDecimal("100"));
        wallet.setStatus(WalletStatus.ACTIVE);
        wallet.setCreatedAt(LocalDateTime.now());

        walletRepository.save(wallet);
    }

    @Test
    void shouldNotAllowDoubleWithdraw() throws InterruptedException {

        ExecutorService executor = Executors.newFixedThreadPool(2);

        CountDownLatch latch = new CountDownLatch(2);

        Runnable withdrawTask = () -> {
            try {
                walletService.withdraw(user, new BigDecimal("100"), UUID.randomUUID().toString());
            } catch (Exception ignored){}
            latch.countDown();
        };

        executor.submit(withdrawTask);
        executor.submit(withdrawTask);

        latch.await();
        Wallet updated = walletRepository.findByUserId(user.getId()).orElseThrow();

        assertTrue(updated.getBalance().compareTo(BigDecimal.ZERO) >= 0);
        assertEquals(0, updated.getBalance().compareTo(BigDecimal.ZERO));

    }
}