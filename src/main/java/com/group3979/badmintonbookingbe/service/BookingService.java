package com.group3979.badmintonbookingbe.service;

import com.group3979.badmintonbookingbe.eNum.BookingType;
import com.group3979.badmintonbookingbe.eNum.ExpirationStatus;
import com.group3979.badmintonbookingbe.entity.*;
import com.group3979.badmintonbookingbe.exception.CustomException;
import com.group3979.badmintonbookingbe.model.request.DailyBookingRequest;
import com.group3979.badmintonbookingbe.model.request.FixedBookingRequest;
import com.group3979.badmintonbookingbe.model.request.FlexibleBookingRequest;
import com.group3979.badmintonbookingbe.model.response.BookingResponse;
import com.group3979.badmintonbookingbe.repository.IBookingRepository;
import com.group3979.badmintonbookingbe.repository.IClubRepository;
import com.group3979.badmintonbookingbe.repository.ICourtRepository;
import com.group3979.badmintonbookingbe.repository.ICourtSlotRepository;
import com.group3979.badmintonbookingbe.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

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

    public BookingResponse createDailyBooking(DailyBookingRequest dailyBookingRequest) {
        Booking flexBooking = bookingRepository.findByBookingId(dailyBookingRequest.getFlexibleBookingId());
        CourtSlot courtSlot = courtSlotRepository.findCourtSlotByCourtSlotId(dailyBookingRequest.getBookingDetailRequests().get(0).getCourtSlotId());
        Club club = clubRepository.findByClubId(courtSlot.getCourt().getClub().getClubId());

        //xem da dat lich linh hoat ch va lich linh hoat con han ko va dat dung club ch
        if (flexBooking != null && flexBooking.getBookingType().equals(BookingType.FLEXIBLEBOOKING)
                && flexBooking.getExpirationStatus().equals(ExpirationStatus.UNEXPIRED)
                && club.getClubId() == flexBooking.getClub().getClubId()) {
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
        if (club != null) {
            Promotion promotion = promotionService.checkValidPromotion(club.getClubId(),
                                                                        flexibleBookingRequest.getPromotionCode());
            float temporaryPrice = courtSlotService.getDefaultPriceByClub(club) * flexibleBookingRequest.getAmountTime();
            Booking flexibleBooking = new Booking();
            flexibleBooking.setAccount(accountUtils.getCurrentAccount());
            flexibleBooking.setBookingDate(new Date());
            flexibleBooking.setBookingType(BookingType.FLEXIBLEBOOKING);
            flexibleBooking.setAmountTime(flexibleBookingRequest.getAmountTime());
            flexibleBooking.setClub(club);
            flexibleBooking.setExpirationStatus(ExpirationStatus.UNEXPIRED);
            flexibleBooking.setBookingType(BookingType.FLEXIBLEBOOKING);
            float totalPrice;
            float discountPrice = 0;
            if (promotion != null) {
                discountPrice = promotion.getDiscount();
                totalPrice = temporaryPrice - discountPrice;
            } else {
                totalPrice = temporaryPrice;
            }
            flexibleBooking.setTotalPrice(totalPrice);
            flexibleBooking = bookingRepository.save(flexibleBooking);
            return bookingDetailService.getBookingResponse(flexibleBooking,temporaryPrice,discountPrice);
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
}

