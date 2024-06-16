package com.group3979.badmintonbookingbe.service;

import com.group3979.badmintonbookingbe.entity.Account;
import com.group3979.badmintonbookingbe.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import com.group3979.badmintonbookingbe.entity.Wallet;
import com.group3979.badmintonbookingbe.model.response.WalletResponse;
import com.group3979.badmintonbookingbe.repository.IAuthenticationRepository;
import com.group3979.badmintonbookingbe.repository.IWalletRepository;
import javassist.NotFoundException;


import java.math.BigDecimal;

@Service
public class WalletService {
    @Autowired

    AccountUtils accountUtils;

    public String createUrl(String amount) throws NoSuchAlgorithmException, InvalidKeyException, Exception{
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime createDate = LocalDateTime.now();
        String formattedCreateDate = createDate.format(formatter);

        Account user = accountUtils.getCurrentAccount();

        String orderId = UUID.randomUUID().toString().substring(0,6);

//        Wallet wallet = walletRepository.findWalletByUser_Id(user.getId());
//
//        Transaction transaction = new Transaction();
//
//        transaction.setAmount(Float.parseFloat(rechargeRequestDTO.getAmount()));
//        transaction.setTransactionType(TransactionEnum.PENDING);
//        transaction.setTo(wallet);
//        transaction.setTransactionDate(formattedCreateDate);
//        transaction.setDescription("Recharge");
//        Transaction transactionReturn = transactionRepository.save(transaction);

        String tmnCode = "4AI8TZAL";
        String secretKey = "L7PTI36MCPCFFB0JON87IXJFQYPOYJ2I";
        String vnpUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
        String returnUrl = "http://datsan79.online/";// trang tra ve khi hoan thanh

        String currCode = "VND";
        Map<String, String> vnpParams = new TreeMap<>();
        vnpParams.put("vnp_Version", "2.1.0");
        vnpParams.put("vnp_Command", "pay");
        vnpParams.put("vnp_TmnCode", tmnCode);
        vnpParams.put("vnp_Locale", "vn");
        vnpParams.put("vnp_CurrCode", currCode);
        vnpParams.put("vnp_TxnRef", orderId);
        vnpParams.put("vnp_OrderInfo", "Thanh toan cho ma GD: " + orderId);
        vnpParams.put("vnp_OrderType", "other");
        vnpParams.put("vnp_Amount", amount +"00");
        vnpParams.put("vnp_ReturnUrl", returnUrl);
        vnpParams.put("vnp_CreateDate", formattedCreateDate);
        vnpParams.put("vnp_IpAddr", "157.245.153.47");

        StringBuilder signDataBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : vnpParams.entrySet()) {
            signDataBuilder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.toString()));
            signDataBuilder.append("=");
            signDataBuilder.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.toString()));
            signDataBuilder.append("&");
        }
        signDataBuilder.deleteCharAt(signDataBuilder.length() - 1); // Remove last '&'

        String signData = signDataBuilder.toString();
        String signed = generateHMAC(secretKey, signData);

        vnpParams.put("vnp_SecureHash", signed);

        StringBuilder urlBuilder = new StringBuilder(vnpUrl);
        urlBuilder.append("?");
        for (Map.Entry<String, String> entry : vnpParams.entrySet()) {
            urlBuilder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.toString()));
            urlBuilder.append("=");
            urlBuilder.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.toString()));
            urlBuilder.append("&");
        }
        urlBuilder.deleteCharAt(urlBuilder.length() - 1); // Remove last '&'

        return urlBuilder.toString();
    }

    private String generateHMAC(String secretKey, String signData) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac hmacSha512 = Mac.getInstance("HmacSHA512");
        SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
        hmacSha512.init(keySpec);
        byte[] hmacBytes = hmacSha512.doFinal(signData.getBytes(StandardCharsets.UTF_8));

        StringBuilder result = new StringBuilder();
        for (byte b : hmacBytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }


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
