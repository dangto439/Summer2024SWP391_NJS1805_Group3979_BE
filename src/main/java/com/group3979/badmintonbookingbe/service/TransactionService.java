package com.group3979.badmintonbookingbe.service;

import com.group3979.badmintonbookingbe.eNum.TransactionType;
import com.group3979.badmintonbookingbe.entity.Account;
import com.group3979.badmintonbookingbe.entity.Transaction;
import com.group3979.badmintonbookingbe.entity.Wallet;
import com.group3979.badmintonbookingbe.exception.CustomException;
import com.group3979.badmintonbookingbe.model.request.TransactionRequest;
import com.group3979.badmintonbookingbe.model.response.TransactionResponse;
import com.group3979.badmintonbookingbe.repository.IAuthenticationRepository;
import com.group3979.badmintonbookingbe.repository.IBookingRepository;
import com.group3979.badmintonbookingbe.repository.ITransactionRepository;
import com.group3979.badmintonbookingbe.repository.IWalletRepository;
import com.group3979.badmintonbookingbe.utils.AccountUtils;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import java.util.List;


@Service
public class TransactionService {
    @Autowired
    AccountUtils accountUtils;

    @Autowired
    IBookingRepository bookingRepository;

    @Autowired
    IWalletRepository walletRepository;

    @Autowired
    IAuthenticationRepository authenticationRepository;

    @Autowired
    ITransactionRepository transactionRepository;
    // Create Transaction sau bat' ky` 1 giao dich. nao` (default type: PENDING)
    public TransactionResponse createTransaction(TransactionRequest transactionRequest) {
        Account senderAccount = accountUtils.getCurrentAccount();
        if (senderAccount == null) {
            throw new CustomException("Sender account not found");
        }
        Wallet senderWallet = walletRepository.findWalletByAccount(senderAccount);
        if (senderWallet == null) {
            throw new CustomException("Sender wallet not found");
        }

        LocalDateTime timestamp = LocalDateTime.now();
        Transaction transaction = new Transaction();
        transaction.setAmount(transactionRequest.getAmount());
        transaction.setTimestamp(timestamp);
        transaction.setType(TransactionType.PENDING);
        transaction.setSenderWallet(senderWallet);
        transaction.setDescription(TransactionType.PENDING.getDescription());
        transaction.setBooking(bookingRepository.findByBookingId(transactionRequest.getBookingId()));

        transaction = transactionRepository.save(transaction);
        return buildTransactionResponse(transaction);
    }

    // Create Transaction with the optional TransactionType
    public TransactionResponse createTransactionV2(TransactionRequest transactionRequest){
        Account senderAccount = accountUtils.getCurrentAccount();
        Wallet senderWallet = walletRepository.findWalletByAccount(senderAccount);
        Wallet receiverWallet = walletRepository.findWalletByWalletId(transactionRequest.getReceiverWalletId());
        if (receiverWallet == null) {
            throw new CustomException("Receiver wallet not found with ID: " + transactionRequest.getReceiverWalletId());
        }

        TransactionType transactionType = transactionRequest.getType();
        if (transactionType == null) {
            throw new CustomException("Transaction type cannot be null");
        }
        LocalDateTime timestamp = LocalDateTime.now();
        Transaction transaction = new Transaction();
        transaction.setAmount(transactionRequest.getAmount());
        transaction.setTimestamp(timestamp);
        transaction.setType(transactionType);
        transaction.setSenderWallet(senderWallet);
        transaction.setReceiverWallet(receiverWallet);
        transaction.setDescription(transactionType.getDescription());
        transaction.setBooking(bookingRepository.findByBookingId(transactionRequest.getBookingId()));

        transaction = transactionRepository.save(transaction);
        return buildTransactionResponse(transaction);
    }

    // Get All Transactions for Admin
    public List<TransactionResponse> getAllTransactions() {
        List<Transaction> transactions = transactionRepository.findAll();
        List<TransactionResponse> transactionResponses = new ArrayList<>();
        for (Transaction transaction : transactions) {
            transactionResponses.add(buildTransactionResponse(transaction));
        }
        return transactionResponses;
    }

    // Get Transactions for specifically account
    public List<TransactionResponse> getTransactionsForAccount(Long accountId)throws NotFoundException {
        Account account = authenticationRepository.findAccountById(accountId);
        if (account == null) {
            throw new NotFoundException("Account not found with ID: " + accountId);
        }
        // Find Wallet of this account
        Wallet wallet = walletRepository.findWalletByAccount(account);
        if (wallet == null) {
            throw new NotFoundException("Wallet not found for account with ID: " + accountId);
        }
        // Get all transaction of account
        List<Transaction> transactions = transactionRepository.findTransactionsBySenderWallet(wallet);
        // init
        List<TransactionResponse> transactionResponses = new ArrayList<>();
        for (Transaction transaction : transactions) {
            transactionResponses.add(buildTransactionResponse(transaction));
        }
        return transactionResponses;
    }

    private TransactionResponse buildTransactionResponse(Transaction transaction) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if(transaction.getBooking() == null){
            return TransactionResponse.builder().
                    transactionId(transaction.getTransactionId()).
                    description(transaction.getDescription()).
                    amount(transaction.getAmount()).
                    timestamp(transaction.getTimestamp().format(formatter)).
                    type(transaction.getType()).
                    senderWallet(transaction.getSenderWallet()).
                    receiverWallet(transaction.getReceiverWallet()).
                    bookingId(0).build();
        }
        return TransactionResponse.builder().
                transactionId(transaction.getTransactionId()).
                description(transaction.getDescription()).
                amount(transaction.getAmount()).
                timestamp(transaction.getTimestamp().format(formatter)).
                type(transaction.getType()).
                senderWallet(transaction.getSenderWallet()).
                receiverWallet(transaction.getReceiverWallet()).
                bookingId(transaction.getBooking().getBookingId()).build();
    }
}
