package dfomenko.service;


import dfomenko.entity.Film;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface FilmService {

    List<Film> findAllFilms();
    Page<Film> findAllFilms(Pageable pageable);
    Film findFilmById(Long id);
    Film findFilmByName(String filmName);
    void createFilm(Film film);
    void updateFilm(Film film);
    void deleteFilmById(Long filmId);

}
