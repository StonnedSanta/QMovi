package com.qmovi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qmovi.dto.MovieDTO;
import com.qmovi.entity.Movie;
import com.qmovi.service.MovieService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MovieController.class)
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MovieService movieService;

    @Test
    void addMovie_ValidInput_ReturnsMovie() throws Exception {
        // Arrange
        MovieDTO movieDTO = new MovieDTO();
        movieDTO.setName("Inception");
        movieDTO.setGenre("Sci-Fi");
        movieDTO.setRating(4.8);

        Movie savedMovie = new Movie("Inception", "Sci-Fi", 4.8);

        when(movieService.addMovie(any(Movie.class))).thenReturn(savedMovie);

        // Act & Assert
        mockMvc.perform(post("/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(movieDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Inception"))
                .andExpect(jsonPath("$.genre").value("Sci-Fi"))
                .andExpect(jsonPath("$.rating").value(4.8));

        verify(movieService, times(1)).addMovie(any(Movie.class));
    }

    @Test
    void addMovie_InvalidRating_ReturnsBadRequest() throws Exception {
        // Arrange
        MovieDTO movieDTO = new MovieDTO();
        movieDTO.setName("Inception");
        movieDTO.setGenre("Sci-Fi");
        movieDTO.setRating(6.0); // Invalid > 5

        // Act & Assert
        mockMvc.perform(post("/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(movieDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addMovie_EmptyName_ReturnsBadRequest() throws Exception {
        // Arrange
        MovieDTO movieDTO = new MovieDTO();
        movieDTO.setName(""); // Empty
        movieDTO.setGenre("Sci-Fi");
        movieDTO.setRating(4.0);

        // Act & Assert
        mockMvc.perform(post("/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(movieDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllMovies_ReturnsList() throws Exception {
        // Arrange
        Movie movie1 = new Movie("Movie1", "Action", 4.0);
        Movie movie2 = new Movie("Movie2", "Comedy", 3.5);
        
        when(movieService.getAllMovies()).thenReturn(Arrays.asList(movie1, movie2));

        // Act & Assert
        mockMvc.perform(get("/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Movie1"))
                .andExpect(jsonPath("$[1].name").value("Movie2"));

        verify(movieService, times(1)).getAllMovies();
    }

    @Test
    void deleteMovie_ValidId_ReturnsSuccessMessage() throws Exception {
        // Arrange
        Long movieId = 1L;
        doNothing().when(movieService).deleteMovie(movieId);

        // Act & Assert
        mockMvc.perform(delete("/movies/{id}", movieId))
                .andExpect(status().isOk())
                .andExpect(content().string("Movie deleted successfully"));

        verify(movieService, times(1)).deleteMovie(movieId);
    }
}