package dfomenko.repository;


import dfomenko.entity.Film;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface FilmRepository extends JpaRepository<Film, Long> {

    Film findFilmById(Long filmId);
    Film findFilmByName(String filmName);
    void deleteFilmById(Long filmId);

}
