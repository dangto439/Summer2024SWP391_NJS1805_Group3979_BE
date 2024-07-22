package com.group3979.badmintonbookingbe.service;

import com.group3979.badmintonbookingbe.eNum.*;
import com.group3979.badmintonbookingbe.entity.*;
import com.group3979.badmintonbookingbe.repository.*;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class ScheduledTasks {
    @Autowired
    IBookingRepository bookingRepository;
    @Autowired
    IBookingDetailRepository bookingDetailRepository;
    @Autowired
    ITransactionRepository transactionRepository;
    @Autowired
    IPromotionRepository promotionRepository;
    @Autowired
    WalletService walletService;
    @Autowired
    IContestRepository contestRepository;
    @Autowired
    ContestService contestService;

    @Scheduled(cron = "0 0 0 * * ?") // Runs daily at midnight
    @Transactional
    public void inactivatePromotions() {
        LocalDate today = LocalDate.now();
        List<Promotion> promotionList = promotionRepository.findAll();
        promotionList.removeIf(promotion -> !(promotion.getPromotionStatus().equals(PromotionStatus.EXPIRED)));


        for (Promotion promotion : promotionList) {
            if (promotion.getEndDate().isEqual(today)) {
                promotion.setPromotionStatus(PromotionStatus.EXPIRED);
                promotionRepository.save(promotion);
            }
        }
    }

    @Scheduled(cron = "0 0/4 * * * ?")
    @Transactional
    public void removeTransactionPending(){
        LocalDateTime dateTime = LocalDateTime.now();
        List<Transaction> transactionList = transactionRepository.findAll();
        transactionList.removeIf(transaction -> !(transaction.getType().equals(TransactionType.PENDING)));
        for(Transaction transaction : transactionList){
            Duration duration = Duration.between(transaction.getTimestamp(), dateTime);
            if(duration.toMinutes() >= 15){
                transaction.setType(TransactionType.CANCEL);
                transaction.setDescription(TransactionType.CANCEL.getDescription());
                transactionRepository.save(transaction);
            }
        }
    }


    @Scheduled(cron = "0 0/4 * * * ?")
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
    @Scheduled(cron = "0 1 * * * ?")
    @Transactional
    public void changeBookingDetailStatus(){
        LocalDate dateTime = LocalDate.now();
        List<BookingDetail> bookingDetails = bookingDetailRepository.findBookingDetailByStatus(BookingDetailStatus.UNFINISHED);
        int currentTime = LocalDateTime.now().getHour();
        for(BookingDetail bookingDetail:bookingDetails){

            if(dateTime.isEqual(bookingDetail.getPlayingDate()) &&
                    currentTime == bookingDetail.getCourtSlot().getSlot().getTime()){
                bookingDetail.setStatus(BookingDetailStatus.FINISHED);
            }
        }
        bookingDetailRepository.saveAll(bookingDetails);
    }
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void cancelContestOverTime() throws NotFoundException {
        LocalDate dateTime = LocalDate.now();
        List<Contest> contests = contestRepository.findContestsByContestStatus(ContestStatus.ACTIVE);
        for(Contest contest: contests){
            if(contest.getStartDate().isEqual(dateTime)){
                contestService.cancelContest(contest.getContestId());
            }
        }
    }

}
