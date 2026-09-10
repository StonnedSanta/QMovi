package com.qmovi.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "shows")
public class Show {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String showTime;

    @ManyToOne
    @JoinColumn(name = "movie_id")
    private Movie movie;

    public Show() {

    }

    public Show(String showTime, Movie movie) {
        this.showTime = showTime;
        this.movie = movie;
    }

    public Long getId() {
        return id;
    }

    public String getShowTime() {
        return showTime;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setShowTime(String showTime) {
        this.showTime = showTime;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }
}