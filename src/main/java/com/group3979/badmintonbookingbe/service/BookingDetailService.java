package com.group3979.badmintonbookingbe.service;

import com.group3979.badmintonbookingbe.eNum.BookingDetailStatus;
import com.group3979.badmintonbookingbe.entity.*;
import com.group3979.badmintonbookingbe.exception.CustomException;
import com.group3979.badmintonbookingbe.model.request.BookingDetailRequest;
import com.group3979.badmintonbookingbe.model.request.DailyBookingRequest;
import com.group3979.badmintonbookingbe.model.request.FixedBookingRequest;
import com.group3979.badmintonbookingbe.model.request.TransferRequest;
import com.group3979.badmintonbookingbe.model.response.BookingDetailResponse;
import com.group3979.badmintonbookingbe.model.response.BookingResponse;
import com.group3979.badmintonbookingbe.model.response.CheckedBookingDetailResponse;
import com.group3979.badmintonbookingbe.repository.*;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;


@Service
public class BookingDetailService {
    @Autowired
    private IBookingDetailRepository bookingDetailRepository;

    @Autowired
    BookingService bookingService;

    @Autowired
    private ICourtSlotRepository courtSlotRepository;

    @Autowired
    private IBookingRepository bookingRepository;

    @Autowired
    private ICourtRepository courtRepository;

    @Autowired
    private ISlotRepository slotRepository;

    @Autowired
    PromotionService promotionService;

    @Autowired
    IDiscountRuleRepository discountRuleRepository;

    @Value("${percent.cancel.booking}")
    private double percentCancelBooking;
    @Autowired
    private WalletService walletService;

    //dat lich ngay ch dat lich linh hoat
    public BookingResponse createDailyBookingDetail(Booking booking, DailyBookingRequest dailyBookingRequest) {
        double temporaryPrice = 0;
        Promotion promotion =
                promotionService.checkValidPromotion(booking.getClub().getClubId(), dailyBookingRequest.getPromotionCode());
        for (BookingDetailRequest bookingDetailRequest : dailyBookingRequest.getBookingDetailRequests()) {
            BookingDetail bookingDetail = new BookingDetail();
            CourtSlot courtSlot = courtSlotRepository
                    .findCourtSlotByCourtSlotId(bookingDetailRequest.getCourtSlotId());
            temporaryPrice += courtSlot.getPrice();
            bookingDetail.setBooking(booking);
            bookingDetail.setCourtSlot(courtSlot);
            bookingDetail.setPrice(courtSlot.getPrice());
            bookingDetail.setStatus(BookingDetailStatus.UNFINISHED);
            bookingDetail.setPlayingDate(bookingDetailRequest.getPlayingDate());
            this.generateCheckInCode(bookingDetail);

        }
        double totalPrice;
        double discountPrice = 0;
        if (promotion != null) {
            discountPrice = promotion.getDiscount();
            totalPrice = temporaryPrice - discountPrice;
        } else {
            totalPrice = temporaryPrice;
        }
        booking.setTotalPrice(totalPrice);
        booking = bookingRepository.save(booking);
        return bookingService.getBookingResponse(booking, temporaryPrice, discountPrice);
    }

    //dat lich ngay sau khi da dat lich linh hoat
    public BookingResponse createFlexibleBookingDetail(Booking flexibleBooking, DailyBookingRequest dailyBookingRequest) {
        double totalPrice = 0;
        int amountTime = flexibleBooking.getAmountTime();
        for (BookingDetailRequest bookingDetailRequest : dailyBookingRequest.getBookingDetailRequests()) {
            BookingDetail bookingDetail = new BookingDetail();
            CourtSlot courtSlot = courtSlotRepository
                    .findCourtSlotByCourtSlotId(bookingDetailRequest.getCourtSlotId());
            bookingDetail.setBooking(flexibleBooking);
            bookingDetail.setCourtSlot(courtSlot);
            bookingDetail.setPrice(courtSlot.getPrice());
            bookingDetail.setStatus(BookingDetailStatus.UNFINISHED);
            bookingDetail.setPlayingDate(bookingDetailRequest.getPlayingDate());
            this.generateCheckInCode(bookingDetail);
            if (amountTime <= 0) {
                totalPrice += courtSlot.getPrice();
            }
            amountTime--;
        }
        if (amountTime <= 0) {
            flexibleBooking.setAmountTime(0);
        } else {
            flexibleBooking.setAmountTime(amountTime);
        }
        flexibleBooking.setTotalPrice(totalPrice);
        flexibleBooking = bookingRepository.save(flexibleBooking);
        return bookingService.getBookingResponse(flexibleBooking, totalPrice, 0);
    }

    //dat lich co dinh
    public BookingResponse createFixedBookingDetail(Booking fixedBooking, FixedBookingRequest fixedBookingRequest) {
        List<LocalDate> playingDates = this.getAllDaysOfBooking(fixedBookingRequest.getYear(),
                fixedBookingRequest.getMonth(), fixedBookingRequest.getDayOfWeeks());
        Promotion promotion =
                promotionService.checkValidPromotion(fixedBooking.getClub().getClubId(), fixedBookingRequest.getPromotionCode());
        double temporaryPrice = 0;

        for (LocalDate playingDate : playingDates) {
            for (Long slotId : fixedBookingRequest.getSlotIds()) {
                temporaryPrice +=
                        this.saveFixedBookingDetailByClub(fixedBooking,fixedBookingRequest.getCourtIds(), slotId, playingDate);
            }
        }
        double totalPrice;
        double discountPrice = (temporaryPrice *
                (discountRuleRepository.findDiscountRuleByClub(fixedBooking.getClub()).getFixedPercent() / 100));
        if (promotion != null) {
            discountPrice += promotion.getDiscount();
            totalPrice = temporaryPrice - discountPrice;
        } else {
            totalPrice = temporaryPrice - discountPrice;
        }
        fixedBooking.setTotalPrice(totalPrice);
        fixedBooking = bookingRepository.save(fixedBooking);
        return bookingService.getBookingResponse(fixedBooking, temporaryPrice, discountPrice);
    }

    public double saveFixedBookingDetailByClub(Booking fixedBooking, List<Long> courtIds, long slotId, LocalDate playingDate) {
        CourtSlot courtSlot = this.selectCourtSlot(courtIds, slotId, playingDate);
        if (courtSlot != null) {
            BookingDetail bookingDetail = new BookingDetail();
            bookingDetail.setPlayingDate(playingDate);
            bookingDetail.setCourtSlot(courtSlot);
            bookingDetail.setPrice(courtSlot.getPrice());
            bookingDetail.setBooking(fixedBooking);
            bookingDetail.setStatus(BookingDetailStatus.UNFINISHED);
            this.generateCheckInCode(bookingDetail);
            return courtSlot.getPrice();
        }
        return 0;
    }


    public BookingDetail generateCheckInCode(BookingDetail bookingDetail) {
        try {
            String checkInCode = String.valueOf(ThreadLocalRandom.current().nextInt(100_000_000, 1_000_000_000));
            bookingDetail.setCheckInCode(checkInCode);
            bookingDetail = bookingDetailRepository.save(bookingDetail);
            return bookingDetail;
        } catch (DataIntegrityViolationException ex) {
            bookingDetail = this.generateCheckInCode(bookingDetail);
            return bookingDetail;
        }
    }

    public CourtSlot selectCourtSlot(List<Long> courtIds, long slotId, LocalDate playingDate) {
        Slot slot = slotRepository.findSlotBySlotId(slotId);
        List<Court> courts = new ArrayList<>();
        for (Long courtId : courtIds) {
            courts.add(courtRepository.findByCourtId(courtId));
        }
        for (Court court : courts) {
            CourtSlot existedCourtSlot =
                    courtSlotRepository.findCourtSlotByPlayingDateAndSlot(playingDate, court, slot);
            if (existedCourtSlot == null) {
                return courtSlotRepository.findCourtSlotByCourtAndSlot(court, slot);
            }
        }
        return null;
    }

    public List<LocalDate> getAllDaysOfBooking(int year, int month, List<Integer> dayOfWeeks) {
        List<LocalDate> days = new ArrayList<>();
        LocalDate dayOfMonth = LocalDate.of(year, month, 1);
        int daysInMonth = dayOfMonth.lengthOfMonth();
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate currentDay = dayOfMonth.withDayOfMonth(day);
            for (Integer dayOfWeek : dayOfWeeks) {
                if (currentDay.getDayOfWeek() == this.getDayOfWeekByNumber(dayOfWeek)) {
                    days.add(currentDay);
                }
            }
        }
        return days;
    }

    public DayOfWeek getDayOfWeekByNumber(int dayNumber) {
        switch (dayNumber) {
            case 1:
                return DayOfWeek.SUNDAY;
            case 2:
                return DayOfWeek.MONDAY;
            case 3:
                return DayOfWeek.TUESDAY;
            case 4:
                return DayOfWeek.WEDNESDAY;
            case 5:
                return DayOfWeek.THURSDAY;
            case 6:
                return DayOfWeek.FRIDAY;
            case 7:
                return DayOfWeek.SATURDAY;
            default:
                throw new IllegalArgumentException("Invalid day number: " + dayNumber);
        }
    }

    @Transactional
    public BookingDetailResponse cancelBookingDetail(long bookingDetailId) throws NotFoundException {
        BookingDetail bookingDetail = bookingDetailRepository.findBookingDetailByBookingDetailId(bookingDetailId);
        if (bookingDetail != null && bookingDetail.getStatus() != BookingDetailStatus.CANCEL) {
            bookingDetail.setStatus(BookingDetailStatus.CANCEL);
            bookingDetail = bookingDetailRepository.save(bookingDetail);
            // refund
            try {
                refundBookingDetail(bookingDetail);
            } catch (Exception e) {
                // handle loi trong refund
                throw new CustomException("Đã xảy ra lỗi trong quá trình hoàn tiền: " + e.getMessage());
            }
            return this.getBookingDetailResponse(bookingDetail);
        } else {
            throw new CustomException("Không tồn tại đơn đặt sân này hoặc đơn đặt lịch này đã bị hủy trước đó");
        }
    }

    // methods refund tren tung bookingDetail cu the
    @Transactional
    public void refundBookingDetail(BookingDetail bookingDetail) throws NotFoundException {
        Booking booking = bookingDetail.getBooking();
        List<BookingDetail> bookingDetailList = bookingDetailRepository.
                findBookingDetailByBooking_BookingId(booking.getBookingId());
        Club club = booking.getClub();
        Account clubOwner = club.getAccount();
        Account customer = booking.getAccount();
        Wallet walletOfCustomer = customer.getWallet();
        Wallet walletOfClubOwner = clubOwner.getWallet();
        //check status
        if (bookingDetail.getStatus() == BookingDetailStatus.CANCEL) {
            long daysUntilPlayingDate = ChronoUnit.DAYS.between(LocalDate.now(), bookingDetail.getPlayingDate());
            if(daysUntilPlayingDate >= 7){
                double refundAmount = bookingDetail.getPrice() * percentCancelBooking;
                if(booking.getPromotion() != null){
                    // priceMinusOfPromotion = so tien duoc giam tren moi bookingDetail khi su dung promotion(discount)
                    double priceMinusOfPromotion = booking.getPromotion().getDiscount() / bookingDetailList.size();
                    refundAmount -= priceMinusOfPromotion;
                }
                // config param
                TransferRequest transferRequest = new TransferRequest();
                transferRequest.setBookingId(booking.getBookingId());
                transferRequest.setAmount(refundAmount);
                transferRequest.setSenderWalletId(walletOfClubOwner.getWalletId());
                transferRequest.setReceiverWalletId(walletOfCustomer.getWalletId());
                walletService.refund(transferRequest);
            }else{
                throw new CustomException("Đơn đặt lịch của bạn không được hoàn tiền vì đã vượt quá số ngày cho phép trong quy định");
            }
        }else {
            throw new IllegalArgumentException("Đơn đặt lịch này không ở trạng thái HỦY");
        }
    }

    public BookingDetailResponse getBookingDetailResponse(BookingDetail bookingDetail) {
        return BookingDetailResponse.builder()
                .bookingDetailId(bookingDetail.getBookingDetailId())
                .checkInCode(bookingDetail.getCheckInCode())
                .price(bookingDetail.getPrice())
                .courtName(bookingDetail.getCourtSlot().getCourt().getCourtName())
                .timeSlot(bookingDetail.getCourtSlot().getSlot().getTime())
                .status(bookingDetail.getStatus())
                .CourtSlotId(bookingDetail.getCourtSlot().getCourtSlotId())
                .playingDate(bookingDetail.getPlayingDate())
                .bookingId(bookingDetail.getBooking().getBookingId()).build();
    }

    public List<BookingDetailResponse> getBookingDetailByBookingId(long bookingId) {

        List<BookingDetailResponse> bookingDetailResponses = new ArrayList<>();
        List<BookingDetail> bookingDetails = bookingDetailRepository.findBookingDetailByBooking_BookingId(bookingId);
        if (!bookingDetails.isEmpty()) {
            for (BookingDetail bookingDetail : bookingDetails) {
                bookingDetailResponses.add(this.getBookingDetailResponse(bookingDetail));
            }
            return bookingDetailResponses;
        } else {
            throw new CustomException("Không tìm thấy kết quả nào");
        }
    }

    public CheckedBookingDetailResponse getBookingDetailByCheckInCode(String checkInCode) {
        BookingDetail bookingDetail = bookingDetailRepository.findBookingDetailByCheckInCode(checkInCode);
        if (bookingDetail != null) {
            CheckedBookingDetailResponse bookingDetailResponse = CheckedBookingDetailResponse.builder()
                    .bookingStatus(bookingDetail.getBooking().getBookingStatus())
                    .phone(bookingDetail.getBooking().getAccount().getPhone())
                    .customerName(bookingDetail.getBooking().getAccount().getName())
                    .checkInCode(bookingDetail.getCheckInCode())
                    .courtName(bookingDetail.getCourtSlot().getCourt().getCourtName())
                    .timeSlot(bookingDetail.getCourtSlot().getSlot().getTime())
                    .playingDate(bookingDetail.getPlayingDate()).build();
            return bookingDetailResponse;
        } else {
            throw new CustomException("Không tìm thấy booking nào");
        }
    }
}

