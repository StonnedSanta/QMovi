package com.qmovi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qmovi.dto.BookingDTO;
import com.qmovi.entity.Booking;
import com.qmovi.entity.BookingStatus;
import com.qmovi.entity.Show;
import com.qmovi.service.BookingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookingService bookingService; // Will be mocked by @WebMvcTest

    private Show testShow = new Show();

    @Test
    void createBooking_ValidInput_ReturnsBooking() throws Exception {
        // Arrange
        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setShowId(1L);
        bookingDTO.setSeatNumber(15);

        Booking savedBooking = new Booking(15, BookingStatus.CONFIRMED, testShow);
        savedBooking.setId(1L);

        when(bookingService.createBooking(any(BookingDTO.class))).thenReturn(savedBooking);

        // Act & Assert
        mockMvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.seatNumber").value(15));

        verify(bookingService, times(1)).createBooking(any(BookingDTO.class));
    }

    @Test
    void getAllBookings_ReturnsList() throws Exception {
        // Arrange
        Booking booking1 = new Booking(10, BookingStatus.CONFIRMED, testShow);
        Booking booking2 = new Booking(20, BookingStatus.CONFIRMED, testShow);
        
        when(bookingService.getAllBookings()).thenReturn(Arrays.asList(booking1, booking2));

        // Act & Assert
        mockMvc.perform(get("/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(bookingService, times(1)).getAllBookings();
    }
}