package com.qmovi.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qmovi.dto.ShowDTO;
import com.qmovi.entity.Movie;
import com.qmovi.entity.Show;
import com.qmovi.repository.MovieRepository;
import com.qmovi.service.ShowService;

import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/shows")
public class ShowController {

    private final ShowService showService;

    private final MovieRepository movieRepository;

    public ShowController(ShowService showService, MovieRepository movieRepository) {
        this.showService = showService;
        this.movieRepository = movieRepository;
    }

    @PostMapping
    public Show addShow(@Valid @RequestBody ShowDTO showDTO) {

        System.out.println(showDTO.getShowTime());
        System.out.println(showDTO.getMovieId());
        
        Movie movie = movieRepository.findById(showDTO.getMovieId())
                .orElseThrow(() ->
                        new RuntimeException("Movie not found"));

        Show show = new Show(
                showDTO.getShowTime(),
                movie
        );

        return showService.addShow(show);
    }

    @GetMapping
    public List<Show> getAllShows() {
        return showService.getAllShows();
    }

}