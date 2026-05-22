package com.qmovi.service;

import com.qmovi.dto.BookingDTO;
import com.qmovi.entity.Booking;
import com.qmovi.entity.BookingStatus;
import com.qmovi.entity.Show;
import com.qmovi.exception.SeatAlreadyBookedException;
import com.qmovi.repository.BookingRepository;
import com.qmovi.repository.ShowRepository;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ShowRepository showRepository;

    public BookingServiceImpl(BookingRepository bookingRepository,
                            ShowRepository showRepository) {

        this.bookingRepository = bookingRepository;
        this.showRepository = showRepository;
    }

    @Override
    @Transactional
    public Booking createBooking(BookingDTO bookingDTO) {

        Show show = showRepository.findById(bookingDTO.getShowId())
                        .orElseThrow(() -> new RuntimeException("Show not found"));

        boolean alreadyBooked = 
            bookingRepository.existsByShowIdAndSeatNumber(
                    show.getId(), 
                    bookingDTO.getSeatNumber()
                );
    
        if (alreadyBooked) {
            throw new SeatAlreadyBookedException(
                "Seat " + bookingDTO.getSeatNumber() + " is already booked for this show"
            );
        }

        Booking newBooking = new Booking(
            bookingDTO.getSeatNumber(),
            BookingStatus.CONFIRMED,
            show
        );

        return bookingRepository.save(newBooking);

    }

    @Override
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }
}