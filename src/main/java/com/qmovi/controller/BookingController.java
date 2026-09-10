package com.qmovi.controller;

import com.qmovi.dto.BookingDTO;
import com.qmovi.entity.Booking;
import com.qmovi.entity.BookingStatus;
import com.qmovi.entity.Show;
import com.qmovi.repository.ShowRepository;
import com.qmovi.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final ShowRepository showRepository;

    public BookingController(
            BookingService bookingService,
            ShowRepository showRepository
    ) {
        this.bookingService = bookingService;
        this.showRepository = showRepository;
    }

    @PostMapping
    public Booking createBooking(
            @Valid @RequestBody BookingDTO bookingDTO
    ) {

        Show show = showRepository
                .findById(bookingDTO.getShowId())
                .orElseThrow(() ->
                        new RuntimeException("Show not found"));

        Booking booking = new Booking(
                bookingDTO.getSeatNumber(),
                BookingStatus.CONFIRMED,
                show
        );

        return bookingService.createBooking(bookingDTO);
    }

    @GetMapping
    public List<Booking> getAllBookings() {
        return bookingService.getAllBookings();
    }
}