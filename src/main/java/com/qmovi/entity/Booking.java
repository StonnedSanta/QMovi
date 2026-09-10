package com.qmovi.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer seatNumber;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @ManyToOne
    @JoinColumn(name = "show_id")
    private Show show;

    public Booking() {

    }

    public Booking (Integer seatNumber, BookingStatus status, Show show) {
        this.seatNumber = seatNumber;
        this.status = status;
        this.show = show;

    }

    public Long getId() {
        return id;
    }

    public Integer getSeatNumber() {
        return seatNumber;
    }
    
    public BookingStatus getStatus() {
        return status;
    }

    public Show getShow() {
        return show;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setSeatNumber(Integer seatNumber) {
        this.seatNumber = seatNumber;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public void setShow(Show show) {
        this.show = show;
    }


}