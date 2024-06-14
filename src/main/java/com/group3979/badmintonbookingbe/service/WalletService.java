package com.group3979.badmintonbookingbe.service;

import com.group3979.badmintonbookingbe.entity.Account;
import com.group3979.badmintonbookingbe.entity.Wallet;
import com.group3979.badmintonbookingbe.model.response.WalletResponse;
import com.group3979.badmintonbookingbe.repository.IAuthenticationRepository;
import com.group3979.badmintonbookingbe.repository.IWalletRepository;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class WalletService {
    @Autowired
    IAuthenticationRepository authenticationRepository;

    @Autowired
    IWalletRepository walletRepository;

    // Create Wallet for User by Email
    public WalletResponse createWalletByEmail(String email) throws NotFoundException {
            Account user = authenticationRepository.findAccountByEmail(email);
            if(user == null){
                throw new NotFoundException("Không tìm thấy tài khoản với Email: " + email);
            }
            Wallet wallet = new Wallet();
            wallet.setAccount(user);
            wallet.setBalance(BigDecimal.ZERO);

            wallet = walletRepository.save(wallet);

            return WalletResponse.builder()
                    .walletId(wallet.getWalletId())
                    .balance(wallet.getBalance())
                    .build();
    }

    // Create Wallet (when user register an account, a wallet is automatically created)
    public void createWallet(String email) {
        Account user = authenticationRepository.findAccountByEmail(email);
        Wallet wallet = new Wallet();
        wallet.setAccount(user);
        wallet.setBalance(BigDecimal.ZERO);

        walletRepository.save(wallet);
    }

    // Find Wallet By AccountId(just input AccountID of User - find the Wallet)
    public WalletResponse getWalletById(Long accountId) throws NotFoundException {
        Account user = authenticationRepository.findAccountById(accountId);
        if(user == null){
            throw new NotFoundException("Không tìm thấy tài khoản với ID: " + accountId);
        }

        Wallet wallet = walletRepository.findWalletByAccount(user);
        if(wallet == null){
            throw new NotFoundException("Không tìm thấy ví cho tài khoản với ID: " + accountId);
        }

        return WalletResponse.builder()
                .walletId(wallet.getWalletId())
                .balance(wallet.getBalance())
                .build();
    }

    // Update Balance of Wallet
    public WalletResponse updateWallet(Long accountId, BigDecimal newBalance) throws NotFoundException {
        Account user = authenticationRepository.findAccountById(accountId);
        if(user == null){
            throw new NotFoundException("Không tìm thấy tài khoản với ID: " + accountId);
        }
        Wallet wallet = walletRepository.findWalletByAccount(user);
        if(wallet == null){
            throw new NotFoundException("Không tìm thấy ví cho tài khoản với ID: " + accountId);
        }
        wallet.setBalance(newBalance);
        wallet = walletRepository.save(wallet);
        return WalletResponse.builder()
                .walletId(wallet.getWalletId())
                .balance(wallet.getBalance())
                .build();
    }
}
