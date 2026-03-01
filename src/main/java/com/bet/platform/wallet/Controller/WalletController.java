    package com.bet.platform.wallet.Controller;


    import com.bet.platform.transaction.model.Transaction;
    import com.bet.platform.user.model.User;
    import com.bet.platform.wallet.dto.BalanceResponse;
    import com.bet.platform.wallet.dto.DepositRequest;
    import com.bet.platform.wallet.dto.TransactionResponse;
    import com.bet.platform.wallet.dto.WithdrawRequest;
    import com.bet.platform.wallet.service.WalletService;
    import jakarta.validation.Valid;
    import lombok.RequiredArgsConstructor;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.core.annotation.AuthenticationPrincipal;
    import org.springframework.web.bind.annotation.*;
    import org.springframework.web.server.ResponseStatusException;

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
                @RequestHeader("Idempotency-Key") String idempotencyKey,
                @RequestBody @Valid DepositRequest request
        ) {
            if (idempotencyKey == null || idempotencyKey.isBlank()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Idempotency-Key header is required"
                );
            }

            walletService.deposit(
                    user,
                    request.amount(),
                    idempotencyKey
            );

            return ResponseEntity.ok().build();
        }

        @PostMapping("/withdraw")
        public ResponseEntity<Void> withdraw(
                @AuthenticationPrincipal User user,
                @RequestHeader("Idempotency-Key") String idempotencyKey,
                @RequestBody @Valid WithdrawRequest request
        ) {

            if (idempotencyKey == null || idempotencyKey.isBlank()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Idempotency-Key header is required"
                );
            }

            walletService.withdraw(
                    user,
                    request.amount(),
                    idempotencyKey

                    );

            return ResponseEntity.ok().build();
        }
        @GetMapping("/transactions")
        public ResponseEntity<List<TransactionResponse>> transactions(
                @AuthenticationPrincipal User user
        ) {
            return ResponseEntity.ok(
                    walletService.getTransactions(user)
                            .stream()
                            .map(t -> new TransactionResponse(
                                    t.getType().name(),
                                    t.getAmount(),
                                    t.getStatus().name(),
                                    t.getOrigin().name(),
                                    t.getCreatedAt().toString()
                            ))
                            .toList()
            );
        }

        @GetMapping("/balance")
        public ResponseEntity<BalanceResponse> balance(
                @AuthenticationPrincipal User user
        ) {
            return ResponseEntity.ok(
                    new BalanceResponse(walletService.getBalance(user))
            );
        }
    }
