package com.group3979.badmintonbookingbe.service;

import com.group3979.badmintonbookingbe.eNum.BookingStatus;
import com.group3979.badmintonbookingbe.eNum.TransactionType;
import com.group3979.badmintonbookingbe.entity.*;
import com.group3979.badmintonbookingbe.exception.InsufficientBalanceException;
import com.group3979.badmintonbookingbe.model.request.TransferRequest;
import com.group3979.badmintonbookingbe.repository.*;
import com.group3979.badmintonbookingbe.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import com.group3979.badmintonbookingbe.model.response.WalletResponse;
import javassist.NotFoundException;


@Service
public class WalletService {
    @Autowired
    AccountUtils accountUtils;
    @Autowired
    IAuthenticationRepository authenticationRepository;
    @Autowired
    IWalletRepository walletRepository;
    @Autowired
    TransactionService transactionService;
    @Autowired
    ITransactionRepository transactionRepository;
    @Autowired
    IClubRepository clubRepository;
    @Autowired
    IBookingRepository bookingRepository;

    @Value("${fee.default.percent}")
    private double feePercentOfPlatform;

    @Value("${platform.wallet.id}")
    private long platformWalletId;


    public String createUrl(String amount) throws NoSuchAlgorithmException, InvalidKeyException, Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime createDate = LocalDateTime.now();
        String formattedCreateDate = createDate.format(formatter);

//        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
//        Date timestamp = new Date();
//        String timestampString = df.format(timestamp);


        Account user = accountUtils.getCurrentAccount();

        String orderId = UUID.randomUUID().toString().substring(0, 6);

        Wallet wallet = walletRepository.findWalletByAccount(user);

        Transaction transaction = new Transaction();

        transaction.setAmount(Double.parseDouble(amount));
        transaction.setType(TransactionType.PENDING);
        transaction.setReceiverWallet(wallet);
        transaction.setTimestamp(createDate);
        transaction.setDescription(TransactionType.PENDING.getDescription());
        transaction = transactionRepository.save(transaction);


        String tmnCode = "4AI8TZAL";
        String secretKey = "L7PTI36MCPCFFB0JON87IXJFQYPOYJ2I";
        String vnpUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
        String returnUrl = "http://datsan79.online/payment?walletId=" + user.getWallet().getWalletId() + "&transactionId=" + transaction.getTransactionId();
        // trang tra ve khi hoan thanh


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
        vnpParams.put("vnp_Amount", amount + "00");
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


    // Create Wallet for User by Email
    public WalletResponse createWalletByEmail(String email) throws NotFoundException {
        Account user = authenticationRepository.findAccountByEmail(email);
        if (user == null) {
            throw new NotFoundException("Không tìm thấy tài khoản với Email: " + email);
        }
        Wallet wallet = new Wallet();
        wallet.setAccount(user);
        wallet.setBalance(0);

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
        wallet.setBalance(0);

        walletRepository.save(wallet);
    }

    // Find Wallet By Email
    public WalletResponse getWalletByEmail(String email) throws NotFoundException {
        Account user = authenticationRepository.findAccountByEmail(email);
        if (user == null) {
            throw new NotFoundException("Không tìm thấy tài khoản với email: " + email);
        }

        Wallet wallet = walletRepository.findWalletByAccount(user);
        if (wallet == null) {
            throw new NotFoundException("Không tìm thấy ví cho tài khoản với ID: " + user.getId());
        }

        return WalletResponse.builder()
                .walletId(wallet.getWalletId())
                .balance(wallet.getBalance())
                .build();
    }

    // Find Wallet By AccountId(just input AccountID of User - find the Wallet)
    public WalletResponse getWalletById(Long accountId) throws NotFoundException {
        Account user = authenticationRepository.findAccountById(accountId);
        if (user == null) {
            throw new NotFoundException("Không tìm thấy tài khoản với ID: " + accountId);
        }

        Wallet wallet = walletRepository.findWalletByAccount(user);
        if (wallet == null) {
            throw new NotFoundException("Không tìm thấy ví cho tài khoản với ID: " + accountId);
        }

        return WalletResponse.builder()
                .walletId(wallet.getWalletId())
                .balance(wallet.getBalance())
                .build();
    }

    public WalletResponse getWalletOfClubOwner(Long clubId) throws NotFoundException {
        Club clubOfOwner = clubRepository.findByClubId(clubId);

        if (clubOfOwner == null) {
            throw new NotFoundException("Không tìm thấy câu lạc bộ có ID: " + clubId);
        }

        Account clubOwner = clubOfOwner.getAccount();

        if (clubOwner == null) {
            throw new NotFoundException("Không tìm thấy tài khoản chủ câu lạc bộ có ID: " + clubId);
        }

        Wallet wallet = walletRepository.findWalletByAccount(clubOwner);

        if (wallet == null) {
            throw new NotFoundException("Không tìm thấy ví cho tài khoản có ID: " + clubOwner.getId());
        }

        return WalletResponse.builder()
                .walletId(wallet.getWalletId())
                .balance(wallet.getBalance())
                .build();
    }


    // Update Balance of Wallet
    public WalletResponse updateWallet(Long accountId, double newBalance) throws NotFoundException {
        Account user = authenticationRepository.findAccountById(accountId);
        if (user == null) {
            throw new NotFoundException("Không tìm thấy tài khoản với ID: " + accountId);
        }
        Wallet wallet = walletRepository.findWalletByAccount(user);
        if (wallet == null) {
            throw new NotFoundException("Không tìm thấy ví cho tài khoản với ID: " + accountId);
        }
        wallet.setBalance(newBalance);
        wallet = walletRepository.save(wallet);
        return WalletResponse.builder()
                .walletId(wallet.getWalletId())
                .balance(wallet.getBalance())
                .build();
    }

    // Nap tien vao vi (Deposit)
    public WalletResponse deposit(Long walletId, double amount) throws NotFoundException {
        Wallet wallet = walletRepository.findWalletByWalletId(walletId);
        if (wallet == null) {
            throw new NotFoundException("Không tìm thấy ví với ID: " + walletId);
        }

        // nap tien
        double currentBalance = wallet.getBalance();
        double newBalance = currentBalance + amount;
        wallet.setBalance(newBalance);

        wallet = walletRepository.save(wallet);

        return WalletResponse.builder()
                .walletId(wallet.getWalletId())
                .balance(wallet.getBalance())
                .build();
    }

    // Rut tien tu vi (Withdrawl)
//    public WalletResponse withdrawl(Long accountId, double amount) throws NotFoundException, InsufficientBalanceException {
//        Account user = authenticationRepository.findAccountById(accountId);
//        if(user == null){
//            throw new NotFoundException("Không tìm thấy tài khoản với ID: " + accountId);
//        }
//        Wallet wallet = walletRepository.findWalletByAccount(user);
//        if(wallet == null){
//            throw new NotFoundException("Không tìm thấy ví cho tài khoản với ID: " + accountId);
//        }
//        // kiem tra so du truoc khi rut
//        double currentBalance = wallet.getBalance();
//        if(currentBalance < amount){
//            throw new InsufficientBalanceException("Số dư không đủ để thực hiện giao dịch rút tiền");
//        }
//        // rut tien (withdrawl)
//        double newBalance = currentBalance - amount;
//        wallet.setBalance(newBalance);
//
//        wallet = walletRepository.save(wallet);
//
//        return WalletResponse.builder()
//                .walletId(wallet.getWalletId())
//                .balance(wallet.getBalance())
//                .build();
//    }

    // Chuyen tien trong khi dat lich (chuyen cho clubOwner va chiet 5% cua booking cho Platform)
    public void transferOnBooking(TransferRequest transferRequest) throws NotFoundException,
            InsufficientBalanceException {
        Wallet senderWallet = walletRepository.findWalletByWalletId(transferRequest.getSenderWalletId());
        if (senderWallet == null) {
            throw new NotFoundException("Không tìm thấy ví cho tài khoản nguồn với ID: " + transferRequest.getSenderWalletId());
        }

        Wallet clubOwnerWallet = walletRepository.findWalletByWalletId(transferRequest.getReceiverWalletId());
        if (clubOwnerWallet == null) {
            throw new NotFoundException("Không tìm thấy ví cho tài khoản đích với ID: " + transferRequest.getReceiverWalletId());
        }

        Wallet platformWallet = walletRepository.findWalletByWalletId(platformWalletId);
        if (platformWallet == null) {
            throw new NotFoundException("Không tìm thấy ví của Platform");
        }

        // Kiem tra so du co du de chuyen khong
        double senderBalance = senderWallet.getBalance();
        if (senderBalance < transferRequest.getAmount()) {
            throw new InsufficientBalanceException("Số dư không đủ để thực hiện giao dịch chuyển tiền");
        }

        // Thuc hien chuyen tien
        // amount = 200, amountReceivedOfOwner = 190, amountReceivedOfPlatform = 10;
        double amountReceivedOfOwner = transferRequest.getAmount() - (transferRequest.getAmount() * feePercentOfPlatform / 100); // 190
        double amountReceivedOfPlatform = transferRequest.getAmount() - amountReceivedOfOwner; // 10

        double newSenderBalance = senderWallet.getBalance() - transferRequest.getAmount(); // currentBalanceOfSender - amount
        double platformBalance = platformWallet.getBalance() + amountReceivedOfPlatform;
        double clubOwnerBalance = clubOwnerWallet.getBalance() + amountReceivedOfOwner;

        senderWallet.setBalance(newSenderBalance);
        clubOwnerWallet.setBalance(clubOwnerBalance);
        platformWallet.setBalance(platformBalance);

//         Create & Save Transaction cho Sender
//        transactionService.createTransactionV2(transferRequest.getBookingId(), transferRequest.getAmount(),
//                senderWallet.getWalletId(), clubOwnerWallet.getWalletId(), TransactionType.TRANSFER);

        // Create & Save Transaction  cho Platform and ClubOwner
        transactionService.createTransactionV2(transferRequest.getBookingId(), amountReceivedOfOwner,
                senderWallet.getWalletId(), clubOwnerWallet.getWalletId(), TransactionType.TRANSFER);
        transactionService.createTransactionV2(transferRequest.getBookingId(), amountReceivedOfPlatform,
                senderWallet.getWalletId(), platformWallet.getWalletId(), TransactionType.TRANSFER);
        // Save Wallet
        walletRepository.save(senderWallet);
        walletRepository.save(clubOwnerWallet);
        walletRepository.save(platformWallet);

        // after success payment - set BookingStatus(PENDING -> CONFIRMED)
        Booking bookingNeedToUpdate = bookingRepository.findByBookingId(transferRequest.getBookingId());
        if (bookingNeedToUpdate == null) {
            throw new NotFoundException("Không tìm thấy đơn đặt lịch với ID: " + transferRequest.getBookingId());
        }
        bookingNeedToUpdate.setBookingStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(bookingNeedToUpdate);
    }

    //  Chuyen tien tu mot vi den vi khac (Transfer)
    public void transfer(TransferRequest transferRequest) throws NotFoundException,
            InsufficientBalanceException {
        Wallet senderWallet = walletRepository.findWalletByWalletId(transferRequest.getSenderWalletId());
        if (senderWallet == null) {
            throw new NotFoundException("Không tìm thấy ví cho tài khoản nguồn với ID: " + transferRequest.getSenderWalletId());
        }

        Wallet receiverWallet = walletRepository.findWalletByWalletId(transferRequest.getReceiverWalletId());
        if (receiverWallet == null) {
            throw new NotFoundException("Không tìm thấy ví cho tài khoản đích với ID: " + transferRequest.getReceiverWalletId());
        }

        // Kiem tra so du co du de chuyen khong
        double senderBalance = senderWallet.getBalance();
        if (senderBalance < transferRequest.getAmount()) {
            throw new InsufficientBalanceException("Số dư không đủ để thực hiện giao dịch chuyển tiền");
        }

        double newSenderBalance = senderBalance - transferRequest.getAmount();
        double receiverBalance = receiverWallet.getBalance() + transferRequest.getAmount();

        // set balance
        senderWallet.setBalance(newSenderBalance);
        receiverWallet.setBalance(receiverBalance);

        senderWallet = walletRepository.save(senderWallet);
        receiverWallet = walletRepository.save(receiverWallet);

        // transaction for sender & receiver
        transactionService.createTransactionV2(transferRequest.getBookingId(), transferRequest.getAmount(),
                senderWallet.getWalletId(), receiverWallet.getWalletId(), TransactionType.TRANSFER);
    }

    //  Refund tien tu mot vi den vi khac (Refund)
    public void refund(TransferRequest transferRequest) throws NotFoundException,
            InsufficientBalanceException {
        Wallet senderWallet = walletRepository.findWalletByWalletId(transferRequest.getSenderWalletId());
        if (senderWallet == null) {
            throw new NotFoundException("Không tìm thấy ví cho tài khoản nguồn với ID: " + transferRequest.getSenderWalletId());
        }

        Wallet receiverWallet = walletRepository.findWalletByWalletId(transferRequest.getReceiverWalletId());
        if (receiverWallet == null) {
            throw new NotFoundException("Không tìm thấy ví cho tài khoản đích với ID: " + transferRequest.getReceiverWalletId());
        }

        // Kiem tra so du co du de chuyen khong
        double senderBalance = senderWallet.getBalance();
        if (senderBalance < transferRequest.getAmount()) {
            throw new InsufficientBalanceException("Số dư không đủ để thực hiện giao dịch chuyển tiền");
        }

        double newSenderBalance = senderBalance - transferRequest.getAmount();
        double receiverBalance = receiverWallet.getBalance() + transferRequest.getAmount();

        // set balance
        senderWallet.setBalance(newSenderBalance);
        receiverWallet.setBalance(receiverBalance);

        senderWallet = walletRepository.save(senderWallet);
        receiverWallet = walletRepository.save(receiverWallet);

        // transaction for refund
        transactionService.createTransactionV2(transferRequest.getBookingId(), transferRequest.getAmount(),
                senderWallet.getWalletId(), receiverWallet.getWalletId(), TransactionType.REFUND);

    }
}
