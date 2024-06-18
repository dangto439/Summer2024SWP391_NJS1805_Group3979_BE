package com.group3979.badmintonbookingbe.service;

import com.group3979.badmintonbookingbe.eNum.BookingType;
import com.group3979.badmintonbookingbe.eNum.ExpirationStatus;
import com.group3979.badmintonbookingbe.entity.*;
import com.group3979.badmintonbookingbe.exception.CustomException;
import com.group3979.badmintonbookingbe.model.request.DailyBookingRequest;
import com.group3979.badmintonbookingbe.model.request.FixedBookingRequest;
import com.group3979.badmintonbookingbe.model.request.FlexibleBookingRequest;
import com.group3979.badmintonbookingbe.model.response.BookingResponse;
import com.group3979.badmintonbookingbe.repository.*;
import com.group3979.badmintonbookingbe.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
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

    public BookingResponse createDailyBooking(DailyBookingRequest dailyBookingRequest) {
        CourtSlot courtSlot = courtSlotRepository.findCourtSlotByCourtSlotId(dailyBookingRequest.getBookingDetailRequests().get(0).getCourtSlotId());
        Club club = clubRepository.findByClubId(courtSlot.getCourt().getClub().getClubId());
        Booking flexBooking = this.getFlexibleBooking(accountUtils.getCurrentAccount(), club);

        //xem da dat lich linh hoat ch va lich linh hoat con han ko va dat dung club ch
        if (flexBooking != null) {
            return bookingDetailService.createFlexibleBookingDetail(flexBooking, dailyBookingRequest);
        } else {
            Booking booking = new Booking();
            booking.setAccount(accountUtils.getCurrentAccount());
            booking.setBookingDate(new Date());
            booking.setBookingType(BookingType.DAILYBOOKING);
            booking.setClub(club);
            booking = bookingRepository.save(booking);
            return bookingDetailService.createDailyBookingDetail(booking, dailyBookingRequest);
        }
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
                flexibleBooking.setBookingDate(new Date());
                flexibleBooking.setBookingType(BookingType.FLEXIBLEBOOKING);
                flexibleBooking.setAmountTime(flexibleBookingRequest.getAmountTime());
                flexibleBooking.setClub(club);
                flexibleBooking.setExpirationStatus(ExpirationStatus.UNEXPIRED);
                flexibleBooking.setBookingType(BookingType.FLEXIBLEBOOKING);
                double totalPrice;
                double discountPrice =  temporaryPrice *
                        (discountRuleRepository.findDiscountRuleByClub(flexibleBooking.getClub()).getFixedPercent() / 100);
                if (promotion != null) {
                    discountPrice = promotion.getDiscount();
                    totalPrice = temporaryPrice - discountPrice;
                } else {
                    totalPrice = temporaryPrice - discountPrice;
                }
                flexibleBooking.setTotalPrice(totalPrice);
                flexibleBooking = bookingRepository.save(flexibleBooking);
                return this.getBookingResponse(flexibleBooking, temporaryPrice, discountPrice);
            }else {
                throw new CustomException("Lịch linh hoạt của bạn trên sân này vẫn chưa hết hạn. " +
                        "Vui lòng sử dụng hết thời gian để đặt lịch linh hoạt tiếp theo.");
            }

        } else {
            throw new CustomException("Club không tồn tại");
        }
    }

    public BookingResponse createFixedBooking(FixedBookingRequest fixedBookingRequest) {
        Club club = clubRepository.findByClubId(fixedBookingRequest.getClubId());
        Booking fixedBooking = new Booking();
        if (club != null) {
            fixedBooking.setAccount(accountUtils.getCurrentAccount());
            fixedBooking.setClub(club);
            fixedBooking.setBookingType(BookingType.FIXEDBOOKING);
            fixedBooking.setBookingDate(new Date());
            fixedBooking = bookingRepository.save(fixedBooking);
            return bookingDetailService.createFixedBookingDetail(fixedBooking, fixedBookingRequest);
        } else {
            throw new CustomException("Club không tồn tại");
        }
    }

    public BookingResponse getBookingResponse(Booking booking, double temporaryPrice, double discountPrice) {
        BookingResponse bookingResponse = BookingResponse.builder().bookingType(booking.getBookingType())
                .bookingDate(booking.getBookingDate())
                .bookingId(booking.getBookingId())
                .amountTime(booking.getAmountTime())
                .totalPrice(booking.getTotalPrice())
                .temporaryPrice(temporaryPrice)
                .discountPrice(discountPrice)
                .ClubId(booking.getClub().getClubId())
                .bookingType(booking.getBookingType())
                .expirationStatus(booking.getExpirationStatus())
                .customerId(booking.getAccount().getId())
                .build();
        return bookingResponse;
    }
    public Booking getFlexibleBooking(Account account, Club club) {
        List<Booking> flexibleBookings = bookingRepository.findBookingByAccountAndClub(account, club);
        flexibleBookings.removeIf(booking -> (!booking.getBookingType().equals(BookingType.FLEXIBLEBOOKING))
                && booking.getExpirationStatus().equals(ExpirationStatus.UNEXPIRED));
        if (!flexibleBookings.isEmpty()) {
            return flexibleBookings.get(0);
        }
        return null;
    }
}

