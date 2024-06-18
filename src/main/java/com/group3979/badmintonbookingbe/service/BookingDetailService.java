package com.group3979.badmintonbookingbe.service;

import com.group3979.badmintonbookingbe.eNum.BookingDetailStatus;
import com.group3979.badmintonbookingbe.entity.*;
import com.group3979.badmintonbookingbe.exception.CustomException;
import com.group3979.badmintonbookingbe.model.request.BookingDetailRequest;
import com.group3979.badmintonbookingbe.model.request.DailyBookingRequest;
import com.group3979.badmintonbookingbe.model.request.FixedBookingRequest;
import com.group3979.badmintonbookingbe.model.response.BookingDetailResponse;
import com.group3979.badmintonbookingbe.model.response.BookingResponse;
import com.group3979.badmintonbookingbe.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


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
    private IClubRepository clubRepository;

    @Autowired
    private ICourtRepository courtRepository;

    @Autowired
    private ISlotRepository slotRepository;

    @Autowired
    PromotionService promotionService;

    @Autowired
    IDiscountRuleRepository discountRuleRepository;

    //dat lich ngay ch dat lich linh hoat
    public BookingResponse createDailyBookingDetail(Booking booking, DailyBookingRequest dailyBookingRequest) {
        float temporaryPrice = 0;
        Promotion promotion =
                promotionService.checkValidPromotion(booking.getClub().getClubId(), dailyBookingRequest.getPromotionCode());
        System.out.println(promotion);
        for (BookingDetailRequest bookingDetailRequest : dailyBookingRequest.getBookingDetailRequests()) {
            BookingDetail bookingDetail = new BookingDetail();
            CourtSlot courtSlot = courtSlotRepository
                    .findCourtSlotByCourtSlotId(bookingDetailRequest.getCourtSlotId());
            temporaryPrice += courtSlot.getPrice();
            bookingDetail.setBooking(booking);
            bookingDetail.setCourtSlot(courtSlot);
            bookingDetail.setPrice(courtSlot.getPrice());
            bookingDetail.setStatus(BookingDetailStatus.UNFINISHED);
            bookingDetail.setPlayingDate(this.truncateTime(bookingDetailRequest.getPlayingDate()));
            bookingDetailRepository.save(bookingDetail);

        }
        float totalPrice;
        float discountPrice = 0;
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
        float totalPrice = 0;
        int amountTime = flexibleBooking.getAmountTime();
        //Club club = flexibleBooking.getClub();
        for (BookingDetailRequest bookingDetailRequest : dailyBookingRequest.getBookingDetailRequests()) {
            BookingDetail bookingDetail = new BookingDetail();
            CourtSlot courtSlot = courtSlotRepository
                    .findCourtSlotByCourtSlotId(bookingDetailRequest.getCourtSlotId());
            bookingDetail.setBooking(flexibleBooking);
            bookingDetail.setCourtSlot(courtSlot);
            bookingDetail.setPrice(courtSlot.getPrice());
            bookingDetail.setStatus(BookingDetailStatus.UNFINISHED);
            bookingDetail.setPlayingDate(this.truncateTime(bookingDetailRequest.getPlayingDate()));
            bookingDetailRepository.save(bookingDetail);
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
    
    public Date truncateTime(Date inputDate) {
        if (inputDate == null) {
            return null;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(inputDate);
        // Đặt giờ, phút, giây, mili giây thành 0
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    //dat lich co dinh
    public BookingResponse createFixedBookingDetail(Booking fixedBooking, FixedBookingRequest fixedBookingRequest) {
        List<Date> playingDates = this.getAllDaysOfBooking(fixedBookingRequest.getYear(),
                fixedBookingRequest.getMonth(), fixedBookingRequest.getDayOfWeeks());
        Promotion promotion =
                promotionService.checkValidPromotion(fixedBooking.getClub().getClubId(), fixedBookingRequest.getPromotionCode());
        float temporaryPrice = 0;
        for (Date playingDate : playingDates) {
            for (Long slotId : fixedBookingRequest.getSlotIds()) {
                //select courtSlot
                CourtSlot courtSlot = this.selectCourtSlot(fixedBookingRequest.getClubId(), slotId, playingDate);
                if (courtSlot != null) {
                    System.out.println(courtSlot);
                    BookingDetail bookingDetail = new BookingDetail();
                    bookingDetail.setPlayingDate(playingDate);
                    bookingDetail.setCourtSlot(courtSlot);
                    bookingDetail.setPrice(courtSlot.getPrice());
                    bookingDetail.setBooking(fixedBooking);
                    temporaryPrice += courtSlot.getPrice();
                    bookingDetail.setStatus(BookingDetailStatus.UNFINISHED);
                    bookingDetailRepository.save(bookingDetail);
                }
            }
        }
        float totalPrice;
        float discountPrice = (float) (temporaryPrice *
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

    public CourtSlot selectCourtSlot(long clubId, Long slotId, Date playingDate) {
        Club club = clubRepository.findByClubId(clubId);
        Slot slot = slotRepository.findSlotBySlotId(slotId);
        List<Court> courts = courtRepository.findByClub(club);
        for (Court court : courts) {
            CourtSlot existedCourtSlot =
                    courtSlotRepository.findCourtSlotByPlayingDateAndSlot(playingDate, court, slot);
            if (existedCourtSlot == null) {
                return courtSlotRepository.findCourtSlotByCourtAndSlot(court, slot);
            }
        }
        return null;
    }

    public List<Date> getAllDaysOfBooking(int year, int month, List<Integer> dayOfWeeks) {
        List<Date> days = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 1);
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int day = 1; day <= daysInMonth; day++) {
            calendar.set(Calendar.DAY_OF_MONTH, day);
            for (Integer dayOfWeek : dayOfWeeks) {
                if (calendar.get(Calendar.DAY_OF_WEEK) == dayOfWeek) {
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                    days.add(calendar.getTime());
                }
            }
        }
        return days;
    }

    public BookingDetailResponse cancelBookingDetail(long bookingDetailId) {
        BookingDetail bookingDetail = bookingDetailRepository.findBookingDetailByBookingDetailId(bookingDetailId);
        if (bookingDetail != null) {
            bookingDetail.setStatus(BookingDetailStatus.CANCELLED);
            bookingDetail = bookingDetailRepository.save(bookingDetail);
            return this.getBookingDetailResponse(bookingDetail);
        } else {
            throw new CustomException("không tồn tại đơn đặt sân này");
        }

    }

    public BookingDetailResponse getBookingDetailResponse(BookingDetail bookingDetail) {
        return BookingDetailResponse.builder()
                .bookingDetailId(bookingDetail.getBookingDetailId())
                .checkInCode(bookingDetail.getCheckInCode())
                .price(bookingDetail.getPrice())
                .status(bookingDetail.getStatus())
                .CourtSlotId(bookingDetail.getCourtSlot().getCourtSlotId())
                .playingDate(bookingDetail.getPlayingDate())
                .bookingId(bookingDetail.getBooking().getBookingId()).build();
    }

}

