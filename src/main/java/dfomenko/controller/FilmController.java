package dfomenko.controller;

import dfomenko.entity.Film;
import dfomenko.service.FilesStorageService;
import dfomenko.service.FilmService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Locale;


@Controller
@AllArgsConstructor
public class FilmController {

    private final FilmService filmService;
    private final FilesStorageService imagesStorageService;
    private MessageSource messageSource;


    @GetMapping("/films")
    public String listFilms(@ModelAttribute("error") String error,
                            Model model,
                            HttpSession session) {

        model.addAttribute("films", filmService.findAllFilms());

        return "films";
    }

    @GetMapping("/create_film_form")
    public String createFilmForm(@ModelAttribute("error") String error,
                                 @ModelAttribute("film") Film film,
                                 Model model) {

        model.addAttribute("film", film);

        return "create_film_form";
    }

    @PostMapping("/create_film")
    public String createFilm(@ModelAttribute("film") Film film,
                             @RequestParam("imageFile") MultipartFile imageFile,
                             RedirectAttributes redirectAttributes,
                             Model model) {

        String newFileName = String.valueOf(System.currentTimeMillis());

        film.setImageName(newFileName);

        try {
            filmService.createFilm(film);
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addAttribute("error", messageSource.getMessage("errorMessageFilmNameExists", null,  Locale.getDefault()));
                                            //"Фільм з такою назвою вже існує");
            redirectAttributes.addFlashAttribute("film", film);
            return "redirect:/create_film_form";
        }

        try {
            imagesStorageService.save(imageFile, newFileName);
        } catch (Exception e) {
            filmService.deleteFilmById(filmService.findFilmByName(film.getName()).getId());
            redirectAttributes.addAttribute("error", messageSource.getMessage("errorMessageImageLoad", null,  Locale.getDefault()));
                                            //"Неможливо завантажити зображення");
            redirectAttributes.addFlashAttribute("film", film);
            return "redirect:/create_film_form";
        }
        return "redirect:/films";
    }


    @GetMapping("/update_film_form")
    public String updateFilmForm(@ModelAttribute("filmId") Long filmId,
                                 @ModelAttribute("film") Film film,
                                 @ModelAttribute("error") String error,
                                 Model model) {
        if (film.getName() != null) {
            model.addAttribute("film", film);
        } else {
            model.addAttribute("film", filmService.findFilmById(filmId));
        }
        return "update_film_form";
    }


    @PostMapping("/update_film")
    public String updateFilm(@ModelAttribute("film") Film film,
                             @RequestParam("imageFile") MultipartFile imageFile,
                             @ModelAttribute("filmId") Long filmId,
                             RedirectAttributes redirectAttributes) {

        Film existingFilm = filmService.findFilmById(filmId);
        Film filmBackup = filmService.findFilmById(filmId);

        existingFilm.setName(film.getName());
        existingFilm.setDuration(film.getDuration());
        existingFilm.setDescription(film.getDescription());
        existingFilm.setYear(film.getYear());

        if (!imageFile.isEmpty()) {
            String newFileName = String.valueOf(System.currentTimeMillis());
            String oldFileName = existingFilm.getImageName();
            try {
                existingFilm.setImageName(newFileName);
                filmService.updateFilm(existingFilm);
            } catch (DataIntegrityViolationException e) {
                redirectAttributes.addAttribute("error", messageSource.getMessage("errorMessageFilmNameExists", null,  Locale.getDefault()));
                                                //"Фільм з такою назвою вже існує");
                redirectAttributes.addAttribute("filmId", filmId);
                redirectAttributes.addFlashAttribute("film", film);
                return "redirect:/update_film_form";
            }
            try {
                imagesStorageService.save(imageFile, newFileName);
            } catch (Exception e) {
                // Rollback update
                filmService.updateFilm(filmBackup);
                redirectAttributes.addAttribute("error", messageSource.getMessage("errorMessageImageLoad", null,  Locale.getDefault()));
                                                //"Неможливо завантажити зображення");
                redirectAttributes.addAttribute("filmId", filmId);
                redirectAttributes.addFlashAttribute("film", film);
                return "redirect:/update_film_form";
            }
            imagesStorageService.delete(oldFileName);
        } else {
            try {
                filmService.updateFilm(existingFilm);
            } catch (DataIntegrityViolationException e) {
                redirectAttributes.addAttribute("error", messageSource.getMessage("errorMessageFilmNameExists", null,  Locale.getDefault()));
                                                //"Фільм з такою назвою вже існує");
                redirectAttributes.addAttribute("filmId", filmId);
                redirectAttributes.addFlashAttribute("film", film);
                return "redirect:/update_film_form";
            }
        }

        return "redirect:/films";
    }


    @PostMapping("/delete_film")
    public String deleteFilm(@ModelAttribute("filmId") Long filmId,
                             RedirectAttributes redirectAttributes) throws IOException {

        Film existingFilm = filmService.findFilmById(filmId);

        try {
            filmService.deleteFilmById(existingFilm.getId());
            imagesStorageService.delete(existingFilm.getImageName());
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addAttribute("error", messageSource.getMessage("errorMessageCannotDeleteFilmWithRelatedSeances", null, Locale.getDefault()));
                                            //"Цей фільм неможливо видалити, бо він використовується в існуючому сеансі. " +
                                            //        "Фільм можна буде видалити після видалення сеансів, до яких він належить.");
        }
        return "redirect:/films";
    }


}
