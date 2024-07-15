package com.group3979.badmintonbookingbe.service;

import com.group3979.badmintonbookingbe.eNum.BookingDetailStatus;
import com.group3979.badmintonbookingbe.eNum.BookingStatus;
import com.group3979.badmintonbookingbe.eNum.ExpirationStatus;
import com.group3979.badmintonbookingbe.entity.Booking;
import com.group3979.badmintonbookingbe.entity.BookingDetail;
import com.group3979.badmintonbookingbe.repository.IBookingDetailRepository;
import com.group3979.badmintonbookingbe.repository.IBookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class ScheduledTasks {
    @Autowired
    IBookingRepository bookingRepository;
    @Autowired
    IBookingDetailRepository bookingDetailRepository;

    @Scheduled(cron = "0 0/8 * * * ?")
    @Transactional
    public void removeBookingPending(){
        LocalDateTime dateTime = LocalDateTime.now();
        List<Booking> bookingList = bookingRepository.findAll();
        bookingList.removeIf(booking -> !(booking.getBookingStatus().equals(BookingStatus.PENDING)));
        for(Booking booking:bookingList){
            Duration duration = Duration.between(booking.getBookingDate(), dateTime);
            if(duration.toMinutes() >= 15){
                booking.setBookingStatus(BookingStatus.CANCEL);
                bookingRepository.save(booking);
                List<BookingDetail> bookingDetails = booking.getBookingDetails();
                for(BookingDetail bookingDetail: bookingDetails){
                    bookingDetail.setStatus(BookingDetailStatus.CANCEL);
                }
                bookingDetailRepository.saveAll(bookingDetails);
            }
        }
    }
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void changeBookingDetail(){
        LocalDateTime dateTime = LocalDateTime.now();
        List<Booking> bookingList = bookingRepository.getFlexibleBookingByYear();
        for(Booking booking: bookingList){
            Duration duration = Duration.between(booking.getBookingDate(),dateTime);
            if(duration.toDays() >= 30){
                booking.setExpirationStatus(ExpirationStatus.EXPIRED);
                bookingRepository.save(booking);
            }
        }
    }
}
