package com.qmovi.service;

import com.qmovi.dto.MovieDTO;
import com.qmovi.entity.Movie;
import com.qmovi.repository.MovieRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private MovieServiceImpl movieService;

    @Test
    void addMovie_Success() {
        // Arrange
        MovieDTO movieDTO = new MovieDTO();
        movieDTO.setName("Inception");
        movieDTO.setGenre("Sci-Fi");
        movieDTO.setRating(4.8);

        Movie movieToSave = new Movie("Inception", "Sci-Fi", 4.8);
        Movie savedMovie = new Movie("Inception", "Sci-Fi", 4.8);
        savedMovie.setRating(4.8); // Set rating via setter

        when(movieRepository.save(any(Movie.class))).thenReturn(savedMovie);

        // Act
        Movie result = movieService.addMovie(movieToSave);

        // Assert
        assertNotNull(result);
        assertEquals("Inception", result.getName());
        assertEquals(4.8, result.getRating());
        verify(movieRepository, times(1)).save(any(Movie.class));
    }

    @Test
    void getAllMovies_ReturnsList() {
        // Arrange
        Movie movie1 = new Movie("Movie1", "Action", 4.0);
        Movie movie2 = new Movie("Movie2", "Comedy", 3.5);
        List<Movie> expectedMovies = Arrays.asList(movie1, movie2);

        when(movieRepository.findAll()).thenReturn(expectedMovies);

        // Act
        List<Movie> result = movieService.getAllMovies();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Movie1", result.get(0).getName());
        verify(movieRepository, times(1)).findAll();
    }

    @Test
    void deleteMovie_Success() {
        // Arrange
        Long movieId = 1L;
        doNothing().when(movieRepository).deleteById(movieId);

        // Act
        movieService.deleteMovie(movieId);

        // Assert
        verify(movieRepository, times(1)).deleteById(movieId);
    }
}