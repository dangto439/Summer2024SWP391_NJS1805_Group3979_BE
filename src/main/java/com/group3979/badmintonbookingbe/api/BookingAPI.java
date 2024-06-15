package com.group3979.badmintonbookingbe.api;

import com.group3979.badmintonbookingbe.entity.Booking;
import com.group3979.badmintonbookingbe.model.request.DailyBookingRequest;
import com.group3979.badmintonbookingbe.model.request.FixedBookingRequest;
import com.group3979.badmintonbookingbe.model.request.FlexibleBookingRequest;
import com.group3979.badmintonbookingbe.service.BookingService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/booking")
@SecurityRequirement(name = "api")
@CrossOrigin("*")
public class BookingAPI {
    @Autowired
    BookingService bookingService;
    //dat lich ngay
    @PostMapping("/daily")
    public ResponseEntity<Booking> createDailyBooking(@RequestBody DailyBookingRequest dailyBookingRequest) {
        Booking booking = bookingService.createDailyBooking(dailyBookingRequest);
        return ResponseEntity.ok().body(booking);
    }
    //dat lich linh hoat
    @PostMapping("/flexible")
    public ResponseEntity<Booking> createFlexibleBooking(@RequestBody FlexibleBookingRequest flexibleBookingRequest) {
        Booking flexibleBooking = bookingService.createFlexibleBooking(flexibleBookingRequest);
        return ResponseEntity.ok().body(flexibleBooking);
    }
    // dat lich co dinh
    @PostMapping("/fixed")
    public ResponseEntity<Booking> createFixedBooking(@RequestBody FixedBookingRequest fixedBookingRequest){
        Booking fixedBooking = bookingService.createFixedBooking(fixedBookingRequest);
        return ResponseEntity.ok().body(fixedBooking);
    }
}
