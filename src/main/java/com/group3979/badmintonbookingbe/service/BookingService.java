package com.group3979.badmintonbookingbe.service;

import com.group3979.badmintonbookingbe.eNum.BookingDetailStatus;
import com.group3979.badmintonbookingbe.eNum.BookingStatus;
import com.group3979.badmintonbookingbe.eNum.BookingType;
import com.group3979.badmintonbookingbe.eNum.ExpirationStatus;
import com.group3979.badmintonbookingbe.entity.*;
import com.group3979.badmintonbookingbe.exception.CustomException;
import com.group3979.badmintonbookingbe.model.request.DailyBookingRequest;
import com.group3979.badmintonbookingbe.model.request.FixedBookingRequest;
import com.group3979.badmintonbookingbe.model.request.FlexibleBookingRequest;
import com.group3979.badmintonbookingbe.model.response.BookingDetailResponse;
import com.group3979.badmintonbookingbe.model.request.TransferRequest;
import com.group3979.badmintonbookingbe.model.response.BookingResponse;
import com.group3979.badmintonbookingbe.model.response.RevenueResponse;
import com.group3979.badmintonbookingbe.repository.*;
import com.group3979.badmintonbookingbe.utils.AccountUtils;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional
public class BookingService {
    @Autowired
    private IBookingRepository bookingRepository;

    @Autowired
    private BookingDetailService bookingDetailService;

    @Autowired
    private AccountUtils accountUtils;

    @Autowired
    private IClubRepository clubRepository;

    @Autowired
    PromotionService promotionService;

    @Autowired
    CourtSlotService courtSlotService;

    @Autowired
    ICourtSlotRepository courtSlotRepository;

    @Autowired
    IDiscountRuleRepository discountRuleRepository;

    @Autowired
    private IBookingDetailRepository bookingDetailRepository;

    @Value("${percent.cancel.booking}")
    private double percentCancelBooking;
    @Autowired
    private WalletService walletService;

    public BookingResponse createDailyBooking(DailyBookingRequest dailyBookingRequest) {
        CourtSlot courtSlot = courtSlotRepository.findCourtSlotByCourtSlotId(dailyBookingRequest.getBookingDetailRequests().get(0).getCourtSlotId());
        Club club = clubRepository.findByClubId(courtSlot.getCourt().getClub().getClubId());
        Booking flexBooking = bookingRepository.findByBookingId(dailyBookingRequest.getFlexibleId());
        //xem da dat lich linh hoat ch va lich linh hoat con han ko va dat dung club ch
        if (flexBooking != null) {
            return bookingDetailService.createFlexibleBookingDetail(flexBooking, dailyBookingRequest);
        } else {
            Booking booking = new Booking();
            booking.setAccount(accountUtils.getCurrentAccount());
            LocalDateTime localDateTime = LocalDateTime.now();
            booking.setBookingDate(localDateTime);
            booking.setBookingType(BookingType.DAILYBOOKING);
            booking.setClub(club);
            booking.setBookingStatus(BookingStatus.PENDING);
            booking = bookingRepository.save(booking);
            return bookingDetailService.createDailyBookingDetail(booking, dailyBookingRequest);
        }
    }

    public List<BookingResponse> getFlexibleBookingCurrentAccount() {
        Account currentAccount = accountUtils.getCurrentAccount();
        List<Booking> bookings = bookingRepository.findBookingByAccount(currentAccount);
        bookings.removeIf(booking -> !(booking.getBookingType().equals(BookingType.FLEXIBLEBOOKING)));
        bookings.removeIf(booking -> booking.getExpirationStatus().equals(ExpirationStatus.EXPIRED));
        //choose flexible booking that is comfirmed
        bookings.removeIf(booking -> !booking.getBookingStatus().equals(BookingStatus.CONFIRMED));
        List<BookingResponse> bookingResponses = new ArrayList<>();
        for (Booking booking : bookings) {
            bookingResponses.add(this.getBookingResponse(booking));
        }
        return bookingResponses;
    }

    public BookingResponse createFlexibleBooking(FlexibleBookingRequest flexibleBookingRequest) {
        Club club = clubRepository.findByClubId(flexibleBookingRequest.getClubId());
        Booking existedBooking = this.getFlexibleBooking(accountUtils.getCurrentAccount(), club);

        if (club != null) {
            if (existedBooking == null) {
                Promotion promotion = promotionService.checkValidPromotion(club.getClubId(),
                        flexibleBookingRequest.getPromotionCode());
                double temporaryPrice = courtSlotService.getDefaultPriceByClub(club) * flexibleBookingRequest.getAmountTime();
                Booking flexibleBooking = new Booking();
                flexibleBooking.setAccount(accountUtils.getCurrentAccount());
                LocalDateTime localDateTime = LocalDateTime.now();
                flexibleBooking.setBookingDate(localDateTime);
                flexibleBooking.setBookingType(BookingType.FLEXIBLEBOOKING);
                flexibleBooking.setAmountTime(flexibleBookingRequest.getAmountTime());
                flexibleBooking.setClub(club);
                flexibleBooking.setExpirationStatus(ExpirationStatus.UNEXPIRED);
                flexibleBooking.setBookingType(BookingType.FLEXIBLEBOOKING);
                flexibleBooking.setBookingStatus(BookingStatus.PENDING);
                double totalPrice;
                double discountPrice = temporaryPrice *
                        (discountRuleRepository.findDiscountRuleByClub(flexibleBooking.getClub()).getFixedPercent() / 100);
                if (promotion != null) {
                    flexibleBooking.setPromotion(promotion);
                    discountPrice += promotion.getDiscount();
                    totalPrice = temporaryPrice - discountPrice;
                } else {
                    totalPrice = temporaryPrice - discountPrice;
                }
                flexibleBooking.setTotalPrice(totalPrice);
                flexibleBooking = bookingRepository.save(flexibleBooking);
                return this.getBookingResponse(flexibleBooking, temporaryPrice, discountPrice);
            } else {
                throw new CustomException("Lịch linh hoạt của bạn trên sân này vẫn chưa hết hạn. " +
                        "Vui lòng sử dụng hết thời gian để đặt lịch linh hoạt tiếp theo.");
            }

        } else {
            throw new CustomException("Câu lạc bộ không tồn tại");
        }
    }

    public BookingResponse createFixedBooking(FixedBookingRequest fixedBookingRequest) {
        Club club = clubRepository.findByClubId(fixedBookingRequest.getClubId());
        Booking fixedBooking = new Booking();
        if (club != null) {

            fixedBooking.setAccount(accountUtils.getCurrentAccount());
            fixedBooking.setClub(club);
            fixedBooking.setBookingType(BookingType.FIXEDBOOKING);
            LocalDateTime localDateTime = LocalDateTime.now();
            fixedBooking.setBookingDate(localDateTime);
            fixedBooking.setBookingStatus(BookingStatus.PENDING);
            fixedBooking = bookingRepository.save(fixedBooking);
            return bookingDetailService.createFixedBookingDetail(fixedBooking, fixedBookingRequest);
        } else {
            throw new CustomException("Câu lạc bộ không tồn tại");
        }
    }

    public BookingResponse getBookingResponse(Booking booking, double temporaryPrice, double discountPrice) {
        List<BookingDetailResponse> bookingDetailResponseList = new ArrayList<>();
        List<BookingDetail> bookingDetails = bookingDetailRepository.findBookingDetailByBooking_BookingId(booking.getBookingId());
        for(BookingDetail bookingDetail:bookingDetails){
            bookingDetailResponseList.add(bookingDetailService.getBookingDetailResponse(bookingDetail));
        }
        BookingResponse bookingResponse = BookingResponse.builder().bookingType(booking.getBookingType())
                .bookingDate(booking.getBookingDate())
                .bookingId(booking.getBookingId())
                .bookingDetailResponseList(bookingDetailResponseList)
                .amountTime(booking.getAmountTime())
                .totalPrice(booking.getTotalPrice())
                .temporaryPrice(temporaryPrice)
                .discountPrice(discountPrice)
                .ClubId(booking.getClub().getClubId())
                .bookingType(booking.getBookingType())
                .ClubName(booking.getClub().getClubName())
                .expirationStatus(booking.getExpirationStatus())
                .customerId(booking.getAccount().getId())
                .bookingStatus(booking.getBookingStatus())
                .build();
        return bookingResponse;
    }

    public BookingResponse getBookingResponse(Booking booking) {
        List<BookingDetailResponse> bookingDetailResponseList = new ArrayList<>();
        List<BookingDetail> bookingDetails = bookingDetailRepository.findBookingDetailByBooking_BookingId(booking.getBookingId());
        for(BookingDetail bookingDetail:bookingDetails){
            bookingDetailResponseList.add(bookingDetailService.getBookingDetailResponse(bookingDetail));
        }
        BookingResponse bookingResponse = BookingResponse.builder().bookingType(booking.getBookingType())
                .bookingDate(booking.getBookingDate())
                .bookingId(booking.getBookingId())
                .bookingDetailResponseList(bookingDetailResponseList)
                .amountTime(booking.getAmountTime())
                .totalPrice(booking.getTotalPrice())
                .ClubId(booking.getClub().getClubId())
                .bookingType(booking.getBookingType())
                .ClubName(booking.getClub().getClubName())
                .expirationStatus(booking.getExpirationStatus())
                .customerId(booking.getAccount().getId())
                .bookingStatus(booking.getBookingStatus())
                .build();
        return bookingResponse;
    }

    public Booking getFlexibleBooking(Account account, Club club) {
        List<Booking> flexibleBookings = bookingRepository.findBookingByAccountAndClub(account, club);
        flexibleBookings.removeIf(booking -> (!(booking.getBookingType().equals(BookingType.FLEXIBLEBOOKING))));
        flexibleBookings.removeIf(booking -> booking.getExpirationStatus().equals(ExpirationStatus.UNEXPIRED));
        flexibleBookings.removeIf(booking -> !booking.getBookingStatus().equals(BookingStatus.CONFIRMED));
        if (!flexibleBookings.isEmpty()) {
            return flexibleBookings.get(0);
        }
        return null;
    }


    public List<BookingResponse> getBookingResponseCurrentAccount() {
        List<Booking> bookings = bookingRepository.findBookingByAccount(accountUtils.getCurrentAccount());
        List<BookingResponse> bookingResponses = new ArrayList<>();
        if (!bookings.isEmpty()) {
            for (Booking booking : bookings) {
                bookingResponses.add(this.getBookingResponse(booking));
            }
            return bookingResponses;
        } else {
            throw new CustomException("Không tồn tại booking nào");
        }
    }

    public BookingResponse getBookingById(long id) {
        Booking booking = bookingRepository.findByBookingId(id);
        if (booking != null) {
            return this.getBookingResponse(booking);
        } else {
            throw new CustomException("Booking không tồn tại");
        }
    }

    public List<BookingResponse> getAllBooking() {
        List<Booking> bookings = bookingRepository.findAll();
        List<BookingResponse> bookingResponses = new ArrayList<>();
        for (Booking booking : bookings) {
            bookingResponses.add(this.getBookingResponse(booking));
        }
        return bookingResponses;
    }

    public List<String> notifyFullSlot(FixedBookingRequest fixedBookingRequest) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        List<LocalDate> playingDates = bookingDetailService.getAllDaysOfBooking(
                fixedBookingRequest.getYear(),
                fixedBookingRequest.getMonth(),
                fixedBookingRequest.getDayOfWeeks()
        );
        List<String> fullyBookeds = new ArrayList<>();
        for (LocalDate playingDate : playingDates) {
            for (Long slotId : fixedBookingRequest.getSlotIds()) {
                // Select courtSlot
                CourtSlot courtSlot = bookingDetailService.selectCourtSlot(
                        fixedBookingRequest.getCourtIds(), slotId, playingDate
                );
                if (courtSlot == null) {
                    String date = dtf.format(playingDate);
                    String slot = String.format("Slot %dh tại sân này vào ngày %s đã hết", slotId, date);
                    fullyBookeds.add(slot);
                }
            }
        }
        if (fullyBookeds.isEmpty()) {
            fullyBookeds.add("Không trùng slot");
        }
        return fullyBookeds;
    }


    public double getPriceFixedBooking(FixedBookingRequest fixedBookingRequest) {
        List<LocalDate> playingDates = bookingDetailService.getAllDaysOfBooking(fixedBookingRequest.getYear(),
                fixedBookingRequest.getMonth(), fixedBookingRequest.getDayOfWeeks());
        Promotion promotion =
                promotionService.checkValidPromotion(fixedBookingRequest.getClubId(), fixedBookingRequest.getPromotionCode());
        double temporaryPrice = 0;
        for (LocalDate playingDate : playingDates) {
            for (Long slotId : fixedBookingRequest.getSlotIds()) {
                CourtSlot courtSlot = bookingDetailService.selectCourtSlot(fixedBookingRequest.getCourtIds(), slotId, playingDate);
                if (courtSlot != null) {
                    temporaryPrice += courtSlot.getPrice();
                }
            }
        }
        double discountPrice = (temporaryPrice *
                (discountRuleRepository.findDiscountRuleByClub_ClubId(fixedBookingRequest.getClubId()).getFixedPercent() / 100));
        if (promotion != null) {

            discountPrice += promotion.getDiscount();
            return temporaryPrice - discountPrice;
        } else {
            return (temporaryPrice - discountPrice);
        }
    }

    public List<RevenueResponse> getRevenueBookingResponse(int year) {
        return bookingRepository.findRevenueResponseBookingByYear(year);
    }

    public List<BookingResponse> getBookingByClubId(long id) {
        List<BookingResponse> bookingResponses = new ArrayList<>();
        List<Booking> bookings = bookingRepository.findBookingByClub_ClubId(id);
        for (Booking booking : bookings) {
            bookingResponses.add(this.getBookingResponse(booking));
        }
        return bookingResponses;
    }

    @Transactional
    public BookingResponse cancelBookingClubId(long bookingId) throws NotFoundException {
        Booking booking = bookingRepository.findByBookingId(bookingId);
        if (booking != null) {
            booking.setBookingStatus(BookingStatus.CANCEL);
            booking = bookingRepository.save(booking);
            List<BookingDetail> bookingDetails = bookingDetailRepository.findBookingDetailByBooking_BookingId(bookingId);

            for (BookingDetail bookingDetail : bookingDetails) {
                bookingDetail.setStatus(BookingDetailStatus.CANCEL);
            }
            bookingDetailRepository.saveAll(bookingDetails);
            // refund
            try {
                refundBooking(booking, bookingDetails);
            } catch (Exception e) {
                throw new CustomException("Đã xảy ra lỗi trong quá trình hoàn tiền: " + e.getMessage());
            }
            return this.getBookingResponse(booking);
        } else {
            throw new CustomException("Đơn đặt lịch không tồn tại");
        }
    }

    // refund on Booking (huy toan bo bookingdetail trong Booking)
    @Transactional
    public void refundBooking(Booking booking, List<BookingDetail> bookingDetails) throws NotFoundException {
        Club club = booking.getClub();
        bookingDetails.sort(Comparator.comparing(BookingDetail::getPlayingDate));
        Account clubOwner = club.getAccount();
        Account customer = booking.getAccount();
        Wallet walletOfCustomer = customer.getWallet();
        Wallet walletOfClubOwner = clubOwner.getWallet();
        //check status
        if (booking.getBookingStatus() == BookingStatus.CANCEL) {
            long daysUntilPlayingDate = ChronoUnit.DAYS.between(LocalDate.now(),bookingDetails.get(0).getPlayingDate());
            if(daysUntilPlayingDate >= 7){
                double refundAmount = booking.getTotalPrice() * percentCancelBooking;
                // config param
                TransferRequest transferRequest = new TransferRequest();
                transferRequest.setBookingId(booking.getBookingId());
                transferRequest.setAmount(refundAmount);
                transferRequest.setSenderWalletId(walletOfClubOwner.getWalletId());
                transferRequest.setReceiverWalletId(walletOfCustomer.getWalletId());
                walletService.refundOnBooking(transferRequest);
            }
        }else {
            throw new IllegalArgumentException("Đơn đặt lịch này không ở trạng thái HỦY");
        }
    }

    public List<RevenueResponse> getMonthlyBookingsByClubId(Long id) {
        return bookingRepository.findMonthlyBookingsByClubId(id);
    }

    public List<RevenueResponse> getMonthlyBookingByAccountId(long accountId) {
        return bookingRepository.findMonthlyBookingsByAccountId(accountId);
    }
}

