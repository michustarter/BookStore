package pl.jstk.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import pl.jstk.constants.ModelConstants;
import pl.jstk.constants.ViewNames;

/**
 * Klasa zawierająca metody obsługujące zapytania wysyłane poprzez przeglądarkę
 * od użytkowników. Zapytania dotyczą strony startowej aplikacji oraz logowania
 * do aplikacji.
 * 
 * @author mratajcz
 *
 */
@Controller
public class HomeController {

	protected static final String INFO_TEXT = "Here You shall display information containing information about newly created TO";
	protected static final String LOGGED_CORRECTLY = "You have logged correctly.";
	protected static final String WELCOME = "This is a welcome page";
/**
 * Controller wyswietlajacy strone startową
 * @param model
 * @return
 */
	@GetMapping(value = "/")
	public String welcome(Model model) {
		model.addAttribute(ModelConstants.MESSAGE, WELCOME);
		model.addAttribute(ModelConstants.INFO, INFO_TEXT);
		return ViewNames.WELCOME;
	}
/**
 * Controller odpowiedzialny za logowanie do aplikacji
 * @param model
 * @return
 */
	@GetMapping(value = "/login")
	public String login(Model model) {
		model.addAttribute(ModelConstants.MESSAGE, LOGGED_CORRECTLY);
		return ViewNames.LOGGED_IN;
	}
}
