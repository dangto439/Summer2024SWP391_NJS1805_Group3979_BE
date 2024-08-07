package com.group3979.badmintonbookingbe.api;

import com.group3979.badmintonbookingbe.model.request.DailyBookingRequest;
import com.group3979.badmintonbookingbe.model.request.FixedBookingRequest;
import com.group3979.badmintonbookingbe.model.request.FlexibleBookingRequest;
import com.group3979.badmintonbookingbe.model.response.BookingDetailResponse;
import com.group3979.badmintonbookingbe.model.response.BookingResponse;
import com.group3979.badmintonbookingbe.model.response.CheckedBookingDetailResponse;
import com.group3979.badmintonbookingbe.model.response.RevenueResponse;
import com.group3979.badmintonbookingbe.service.BookingDetailService;
import com.group3979.badmintonbookingbe.service.BookingService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "api")
@CrossOrigin("*")
public class BookingAPI {
    @Autowired
    BookingService bookingService;

    @Autowired
    BookingDetailService bookingDetailService;

    //dat lich ngay
    @PostMapping("/booking/daily")
    public ResponseEntity<BookingResponse> createDailyBooking(@RequestBody DailyBookingRequest dailyBookingRequest) {
        BookingResponse booking = bookingService.createDailyBooking(dailyBookingRequest);
        return ResponseEntity.ok().body(booking);
    }
    @GetMapping("/bookings")
    public ResponseEntity<List<BookingResponse>> getAllBooking(){
        List<BookingResponse> responses = bookingService.getAllBooking();
        return ResponseEntity.ok().body(responses);
    }
    @GetMapping("/booking/flexible")
    public ResponseEntity<List<BookingResponse>> getFlexibleBookingCurrentAccount(){
        List<BookingResponse> responses = bookingService.getFlexibleBookingCurrentAccount();
        return ResponseEntity.ok().body(responses);
    }
    @GetMapping("/bookings/current-account")
    public ResponseEntity<List<BookingResponse>> getBookingCurrentAccount(){
        List<BookingResponse> responses = bookingService.getBookingResponseCurrentAccount();
        return ResponseEntity.ok().body(responses);
    }
    @PutMapping("/booking/{bookingId}")
    public ResponseEntity<BookingResponse> cancelBooking(@PathVariable long bookingId) throws NotFoundException {
        BookingResponse bookingResponse = bookingService.cancelBookingClubId(bookingId);
        return ResponseEntity.ok(bookingResponse);
    }
    @GetMapping("/booking/{id}")
    public ResponseEntity<BookingResponse> getBookingById(@PathVariable long id){
        BookingResponse response = bookingService.getBookingById(id);
        return ResponseEntity.ok().body(response);
    }
    //dat lich linh hoat
    @PostMapping("/booking/flexible")
    public ResponseEntity<BookingResponse> createFlexibleBooking(@RequestBody FlexibleBookingRequest flexibleBookingRequest) {
        BookingResponse flexibleBooking = bookingService.createFlexibleBooking(flexibleBookingRequest);
        return ResponseEntity.ok().body(flexibleBooking);
    }
    // dat lich co dinh
    @PostMapping("/booking/fixed")
    public ResponseEntity<BookingResponse> createFixedBooking(@RequestBody FixedBookingRequest fixedBookingRequest){
        BookingResponse fixedBooking = bookingService.createFixedBooking(fixedBookingRequest);
        return ResponseEntity.ok().body(fixedBooking);
    }
    @DeleteMapping("/booking/booking-detail/{bookingDetailId}")
    public ResponseEntity<BookingDetailResponse> cancelBookingDetail(@PathVariable long bookingDetailId) throws NotFoundException {
        BookingDetailResponse bookingDetailResponse = bookingDetailService.cancelBookingDetail(bookingDetailId);
        return ResponseEntity.ok().body(bookingDetailResponse);
    }
    @GetMapping("/bookings/{clubId}")
    public ResponseEntity<List<BookingResponse>> getBookingByClubId(@PathVariable long clubId) {
        List<BookingResponse> bookingResponses = bookingService.getBookingByClubId(clubId);
        return ResponseEntity.ok(bookingResponses);
    }
    @GetMapping("/booking/booking-detail/{bookingId}")
    public ResponseEntity<List<BookingDetailResponse>> getBookingDetailByBookingId(@PathVariable long bookingId){
        List<BookingDetailResponse> bookingDetailResponses = bookingDetailService.getBookingDetailByBookingId(bookingId);
        return ResponseEntity.ok().body(bookingDetailResponses);
    }
    @PostMapping("/booking/fixed/check")
    public ResponseEntity<List<String>> getFullyFixedBooked(@RequestBody FixedBookingRequest fixedBookingRequest){
        List<String> responses = bookingService.notifyFullSlot(fixedBookingRequest);
        return ResponseEntity.ok().body(responses);
    }

    @PostMapping("/booking/fixed/price")
    public ResponseEntity<Double> getPriceFixedBooking(@RequestBody FixedBookingRequest fixedBookingRequest){
        Double responses = bookingService.getPriceFixedBooking(fixedBookingRequest);
        return ResponseEntity.ok().body(responses);
    }
    @GetMapping("/booking/booking-detail-response/{checkInCode}")
    public ResponseEntity<CheckedBookingDetailResponse> getBookingDetailByCheckInCode(@PathVariable String checkInCode){
        CheckedBookingDetailResponse bookingDetailResponse = bookingDetailService.getBookingDetailByCheckInCode(checkInCode);
        return ResponseEntity.ok().body(bookingDetailResponse);
    }
    @GetMapping("/dashboard-admin-bar-chart/{year}")
    public List<RevenueResponse> getRevenueResponse(@PathVariable int year) {
        return bookingService.getRevenueBookingResponse(year);
    }
    @GetMapping("/dashboard-club-chart/{clubId}")
    public List<RevenueResponse> getMonthlyBookingsByClubs(@PathVariable long clubId) {
        return bookingService.getMonthlyBookingsByClubId(clubId);
    }
    @GetMapping("/dashboard-club-chart-account/{accountId}")
    public List<RevenueResponse> getMonthlyBookingsByAccount(@PathVariable long accountId){
        return bookingService.getMonthlyBookingByAccountId(accountId);
    }
}
