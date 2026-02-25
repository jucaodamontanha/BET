package com.bet.platform.wallet.Controller;

import com.bet.platform.transaction.model.Transaction;
import com.bet.platform.user.model.User;
import com.bet.platform.wallet.dto.DepositRequest;
import com.bet.platform.wallet.dto.WithdrawRequest;
import com.bet.platform.wallet.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping("/deposit")
    public ResponseEntity<Void> deposit(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid DepositRequest request
    ) {
        walletService.deposit(user, request.amount());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/withdraw")
    public ResponseEntity<Void> withdraw(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid WithdrawRequest request
    ) {
        walletService.withdraw(user, request.amount());
        return ResponseEntity.ok().build();
    }
    @GetMapping("/balance")
    public ResponseEntity<BigDecimal> balance(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(walletService.getBalance(user));
    }
    @GetMapping("/transactions")
    public ResponseEntity<List<Transaction>> transactions(
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(walletService.getTransactions(user));
    }
}