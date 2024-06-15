package com.group3979.badmintonbookingbe.service;

import com.group3979.badmintonbookingbe.eNum.BookingDetailStatus;
import com.group3979.badmintonbookingbe.entity.*;
import com.group3979.badmintonbookingbe.model.request.BookingDetailRequest;
import com.group3979.badmintonbookingbe.model.request.DailyBookingRequest;
import com.group3979.badmintonbookingbe.model.request.FixedBookingRequest;
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
    private ICourtSlotRepository courtSlotRepository;

    @Autowired
    private IBookingRepository bookingRepository;

    @Autowired
    private IClubRepository clubRepository;

    @Autowired
    private ICourtRepository courtRepository;

    @Autowired
    private ISlotRepository slotRepository;

    public void createDailyBookingDetail(Booking booking, DailyBookingRequest dailyBookingRequest) {
        float totalprice = 0;
        for (BookingDetailRequest bookingDetailRequest : dailyBookingRequest.getBookingDetailRequests()) {
            BookingDetail bookingDetail = new BookingDetail();
            CourtSlot courtSlot = courtSlotRepository
                    .findCourtSlotByCourtSlotId(bookingDetailRequest.getCourtSlotId());
            totalprice += courtSlot.getPrice();
            bookingDetail.setBooking(booking);
            bookingDetail.setCourtSlot(courtSlot);
            bookingDetail.setPrice(courtSlot.getPrice());
            bookingDetail.setStatus(BookingDetailStatus.UNFINISHED);
            bookingDetail.setPlayingDate(this.truncateTime(bookingDetailRequest.getPlayingDate()));
            bookingDetailRepository.save(bookingDetail);

        }
        booking.setTotalPrice(totalprice);
        bookingRepository.save(booking);
    }

    public void createFlexibleBookingDetail(Booking flexibleBooking, DailyBookingRequest dailyBookingRequest) {
        float totalprice = 0;
        int amountTime = flexibleBooking.getAmountTime();
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
                totalprice += courtSlot.getPrice();
            }
            amountTime--;
        }
        if (amountTime <= 0) {
            flexibleBooking.setAmountTime(0);
        } else {
            flexibleBooking.setAmountTime(amountTime);
        }
        flexibleBooking.setTotalPrice(totalprice);
        bookingRepository.save(flexibleBooking);
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

    public void createFixedBookingDetail(Booking fixedBooking, FixedBookingRequest fixedBookingRequest) {
        List<Date> playingDates = this.getAllDayOfBooking(fixedBookingRequest.getYear(),
                fixedBookingRequest.getMonth() , fixedBookingRequest.getDayOfWeeks());
        float totalPrice = 0;
        for(Date date:playingDates){
            System.out.println(date.getTime());
        }
        for(Date playingDate: playingDates){
            for(Long slotId: fixedBookingRequest.getSlotIds()){
                CourtSlot courtSlot = this.selectCourtSlot(fixedBookingRequest.getClubId(), slotId,playingDate);
                //System.out.println(courtSlot);
                if(courtSlot != null) {
                    System.out.println(courtSlot);
                    BookingDetail bookingDetail = new BookingDetail();
                    bookingDetail.setPlayingDate(playingDate);
                    bookingDetail.setCourtSlot(courtSlot);
                    bookingDetail.setPrice(courtSlot.getPrice());
                    bookingDetail.setBooking(fixedBooking);
                    totalPrice += courtSlot.getPrice();
                    bookingDetail.setStatus(BookingDetailStatus.UNFINISHED);
                    bookingDetail =  bookingDetailRepository.save(bookingDetail);
                    System.out.println(bookingDetail.getBookingDetailId());
                }
            }

        }
        fixedBooking.setTotalPrice(totalPrice);
        bookingRepository.save(fixedBooking);
    }

    public CourtSlot selectCourtSlot(long clubId,Long slotId, Date playingDate){
        Club club = clubRepository.findByClubId(clubId);
        Slot slot = slotRepository.findSlotBySlotId(slotId);
        List<Court> courts = courtRepository.findByClub(club);
        for (Court court: courts){
            CourtSlot existedCourtSlot =
                    courtSlotRepository.findCourtSlotByPlayingDateAndSlot(playingDate,court,slot);
            //System.out.println(existedCourtSlot);
            if(existedCourtSlot == null){
                System.out.println(courtSlotRepository.findCourtSlotByCourtAndSlot(court, slot));
                return courtSlotRepository.findCourtSlotByCourtAndSlot(court, slot);
            }
        }
        return null;
    }
    public List<Date> getAllDayOfBooking(int year, int month, List<Integer> dayOfWeeks) {
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
    public Date getCurrentDateWithoutTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0); // Đặt giờ về 0
        calendar.set(Calendar.MINUTE, 0);      // Đặt phút về 0
        calendar.set(Calendar.SECOND, 0);      // Đặt giây về 0
        calendar.set(Calendar.MILLISECOND, 0); // Đặt mili giây về 0
        return calendar.getTime();
    }
}

