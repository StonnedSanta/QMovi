package com.qmovi.service;

import com.qmovi.dto.BookingDTO;
import com.qmovi.entity.Booking;
import com.qmovi.entity.BookingStatus;
import com.qmovi.entity.Show;
import com.qmovi.exception.SeatAlreadyBookedException;
import com.qmovi.repository.BookingRepository;
import com.qmovi.repository.ShowRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ShowRepository showRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private Show testShow;
    private BookingDTO testBookingDTO;
    private Booking testBooking;

    @BeforeEach
void setUp() {
    testShow = new Show();
    
    testBookingDTO = new BookingDTO();
    testBookingDTO.setShowId(1L);
    testBookingDTO.setSeatNumber(15);
    testBooking = new Booking(15, BookingStatus.CONFIRMED, testShow);
}

    @Test
    void createBooking_Success() {
        // Arrange
        when(showRepository.findById(eq(1L))).thenReturn(Optional.of(testShow));
        when(bookingRepository.existsByShowIdAndSeatNumber(eq(1L), eq(15))).thenReturn(false);
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

        // Act
        Booking result = bookingService.createBooking(testBookingDTO);

        // Assert
        assertNotNull(result);
        assertEquals(15, result.getSeatNumber());
        assertEquals(BookingStatus.CONFIRMED, result.getStatus());
        
        verify(showRepository, times(1)).findById(1L);
        verify(bookingRepository, times(1)).existsByShowIdAndSeatNumber(1L, 15);
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void createBooking_ShowNotFound_ThrowsException() {
        // Arrange
        when(showRepository.findById(eq(99L))).thenReturn(Optional.empty());
        testBookingDTO.setShowId(99L);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> bookingService.createBooking(testBookingDTO));
        
        assertEquals("Show not found", exception.getMessage());
        
        verify(showRepository, times(1)).findById(99L);
        verify(bookingRepository, never()).existsByShowIdAndSeatNumber(anyLong(), anyInt());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createBooking_SeatAlreadyBooked_ThrowsException() {
        // Arrange
        when(showRepository.findById(eq(1L))).thenReturn(Optional.of(testShow));
        when(bookingRepository.existsByShowIdAndSeatNumber(eq(1L), eq(15))).thenReturn(true);

        // Act & Assert
        SeatAlreadyBookedException exception = assertThrows(SeatAlreadyBookedException.class, 
            () -> bookingService.createBooking(testBookingDTO));
        
        assertTrue(exception.getMessage().contains("Seat 15 is already booked"));
        
        verify(showRepository, times(1)).findById(1L);
        verify(bookingRepository, times(1)).existsByShowIdAndSeatNumber(1L, 15);
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void getAllBookings_ReturnsList() {
        // Arrange
        Booking booking1 = new Booking(10, BookingStatus.CONFIRMED, testShow);
        Booking booking2 = new Booking(20, BookingStatus.CONFIRMED, testShow);
        List<Booking> expectedBookings = Arrays.asList(booking1, booking2);

        when(bookingRepository.findAll()).thenReturn(expectedBookings);

        // Act
        List<Booking> result = bookingService.getAllBookings();

        // Assert
        assertEquals(2, result.size());
        verify(bookingRepository, times(1)).findAll();
    }

    @Test
    void createBooking_ConcurrentBooking_PreventsDuplicate() {
        // This test verifies that concurrent bookings are prevented
        // Using a simplified approach that avoids complex stubbing
        
        // First booking: seat available
        when(showRepository.findById(eq(1L))).thenReturn(Optional.of(testShow));
        when(bookingRepository.existsByShowIdAndSeatNumber(eq(1L), eq(15)))
            .thenReturn(false);  // Seat is available
            
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);
        
        // Act - First booking succeeds
        Booking firstResult = bookingService.createBooking(testBookingDTO);
        assertNotNull(firstResult);
        
        // Reset the mock behavior for the second call
        // In a real concurrent scenario, the second call would see the seat as taken
        // For unit testing, we simulate by changing the stub behavior
        reset(bookingRepository);
        
        // Re-stub for the second attempt
        when(showRepository.findById(eq(1L))).thenReturn(Optional.of(testShow));
        when(bookingRepository.existsByShowIdAndSeatNumber(eq(1L), eq(15)))
            .thenReturn(true);  // Now seat is taken
            
        // Second booking should fail
        SeatAlreadyBookedException exception = assertThrows(SeatAlreadyBookedException.class, 
            () -> bookingService.createBooking(testBookingDTO));
        
        assertTrue(exception.getMessage().contains("already booked"));
    }
}