package com.bet.platform.wallet.service;

import com.bet.platform.transaction.model.Transaction;
import com.bet.platform.transaction.model.TransactionOrigin;
import com.bet.platform.transaction.model.TransactionStatus;
import com.bet.platform.transaction.model.TransactionType;
import com.bet.platform.transaction.repository.TransactionRepository;
import com.bet.platform.user.model.User;
import com.bet.platform.wallet.model.Wallet;
import com.bet.platform.wallet.repository.WalletRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public void deposit(User user, BigDecimal amount) {

        Wallet wallet = walletRepository
                .findByUserIdForUpdate(user.getId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);

        Transaction transaction = Transaction.builder()
                .wallet(wallet)
                .amount(amount)
                .type(TransactionType.DEPOSIT)
                .origin(TransactionOrigin.USER)
                .status(TransactionStatus.COMPLETED)
                .createdAt(LocalDateTime.now())
                .build();

        transactionRepository.save(transaction);
    }

    @Transactional
    public void withdraw(User user, BigDecimal amount) {

        Wallet wallet = walletRepository.findByUserIdForUpdate(user.getId())
                .orElseThrow();

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));

        Transaction transaction = Transaction.builder()
                .wallet(wallet)
                .amount(amount)
                .type(TransactionType.WITHDRAW)
                .origin(TransactionOrigin.USER)
                .status(TransactionStatus.COMPLETED)
                .createdAt(LocalDateTime.now())
                .build();

        transactionRepository.save(transaction);
    }
    @Transactional(readOnly = true)
    public BigDecimal getBalance(User user) {

        Wallet wallet = walletRepository
                .findByUserId(user.getId())  // <- método normal
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        return wallet.getBalance();
    }
    @Transactional(readOnly = true)
    public List<Transaction> getTransactions(User user) {

        Wallet wallet = walletRepository
                .findByUserId(user.getId()) // <- método normal
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        return transactionRepository.findByWalletId(wallet.getId());
    }
}