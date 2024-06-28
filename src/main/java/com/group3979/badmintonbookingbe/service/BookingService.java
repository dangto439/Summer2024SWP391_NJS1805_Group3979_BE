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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

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
    private ICourtRepository courtRepository;

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
            LocalDateTime localDateTime = LocalDateTime.now();
            booking.setBookingDate(localDateTime);
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
                LocalDateTime localDateTime = LocalDateTime.now();
                flexibleBooking.setBookingDate(localDateTime);
                flexibleBooking.setBookingType(BookingType.FLEXIBLEBOOKING);
                flexibleBooking.setAmountTime(flexibleBookingRequest.getAmountTime());
                flexibleBooking.setClub(club);
                flexibleBooking.setExpirationStatus(ExpirationStatus.UNEXPIRED);
                flexibleBooking.setBookingType(BookingType.FLEXIBLEBOOKING);
                double totalPrice;
                double discountPrice = temporaryPrice *
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
            } else {
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
            LocalDateTime localDateTime = LocalDateTime.now();
            fixedBooking.setBookingDate(localDateTime);
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
                .ClubName(booking.getClub().getClubName())
                .expirationStatus(booking.getExpirationStatus())
                .customerId(booking.getAccount().getId())
                .bookingStatus(booking.getBookingStatus())
                .build();
        return bookingResponse;
    }

    public BookingResponse getBookingResponse(Booking booking) {
        BookingResponse bookingResponse = BookingResponse.builder().bookingType(booking.getBookingType())
                .bookingDate(booking.getBookingDate())
                .bookingId(booking.getBookingId())
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
        flexibleBookings.removeIf(booking -> !(booking.getBookingType().equals(BookingType.FLEXIBLEBOOKING)));
        flexibleBookings.removeIf(booking -> booking.getExpirationStatus().equals(ExpirationStatus.UNEXPIRED));
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
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        List<LocalDate> playingDates = bookingDetailService.getAllDaysOfBooking(fixedBookingRequest.getYear(),
                fixedBookingRequest.getMonth(), fixedBookingRequest.getDayOfWeeks());
        List<String> fullyBookeds = new ArrayList<>();
        if (fixedBookingRequest.getCourtId() == 0) {
            for (LocalDate playingDate : playingDates) {
                for (Long slotId : fixedBookingRequest.getSlotIds()) {
                    //select courtSlot
                    CourtSlot courtSlot = bookingDetailService.selectCourtSlot(fixedBookingRequest.getClubId(), slotId, playingDate);
                    if (courtSlot == null) {
                        String date = df.format(playingDate);
                        String slots = String.format("Slot %dh tại sân này vào ngày %s đã hết", slotId, date);
                        fullyBookeds.add(slots);
                    }
                }
            }
        } else {
            for (LocalDate playingDate : playingDates) {
                for (Long slotId : fixedBookingRequest.getSlotIds()) {
                    Court court = courtRepository.findByCourtId(fixedBookingRequest.getCourtId());
                    CourtSlot courtSlot = bookingDetailService.selectCourtSlotOfCourt(fixedBookingRequest.getCourtId(), slotId, playingDate);
                    if (courtSlot == null) {
                        String date = df.format(playingDate);
                        String slots = String.format("Slot %dh tại %s vào ngày %s đã hết", slotId, court.getCourtName(), date);
                        fullyBookeds.add(slots);
                    }
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
        Club club = clubRepository.findByClubId(fixedBookingRequest.getClubId());
        Promotion promotion =
                promotionService.checkValidPromotion(club.getClubId(), fixedBookingRequest.getPromotionCode());
        double temporaryPrice = 0;
        if (fixedBookingRequest.getClubId() == 0) {
            for (LocalDate playingDate : playingDates) {
                for (Long slotId : fixedBookingRequest.getSlotIds()) {
                    CourtSlot courtSlot = bookingDetailService.selectCourtSlot(fixedBookingRequest.getClubId(), slotId, playingDate);
                    if (courtSlot != null) {
                        temporaryPrice += courtSlot.getPrice();
                    }
                }
            }
        } else {
            for (LocalDate playingDate : playingDates) {
                for (Long slotId : fixedBookingRequest.getSlotIds()) {
                    CourtSlot courtSlot = bookingDetailService.selectCourtSlotOfCourt(fixedBookingRequest.getCourtId(), slotId, playingDate);
                    if (courtSlot != null) {
                        temporaryPrice += courtSlot.getPrice();
                    }
                }
            }
        }
        double discountPrice = (temporaryPrice *
                (discountRuleRepository.findDiscountRuleByClub(club).getFixedPercent() / 100));
        if (promotion != null) {
            discountPrice += promotion.getDiscount();
            return temporaryPrice - discountPrice;
        } else {
            return (temporaryPrice - discountPrice);
        }
    }
}

