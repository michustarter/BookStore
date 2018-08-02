package pl.jstk.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import pl.jstk.constants.ModelConstants;
import pl.jstk.constants.ViewNames;
import pl.jstk.service.BookService;
import pl.jstk.to.BookTo;
/**
 * Klasa zawierająca metody obsługujące zapytania wysyłane poprzez przeglądarkę od użytkowników.
 * Zapytania dotyczą zasobu książek - wyświetlania, dodawania, usuwania, wyszukwiwania. 
 * @author mratajcz
 *
 */
@Controller
public class BookController {

	protected static final String ALL_BOOKS_INFO = "There are displayed books in the library";
	protected static final String SEARCH_RESULT_INFO = "Searching result:";
	protected static final String ADD_BOOK_INFO = "This is a page where you can add new book";
	protected static final String REMOVED_BOOK_TITLE = "Removed book title: ";
	protected static final String SEARCH_INFO = "You can search for a book";
	protected static final String BOOK_ADDED_SUCCESSFULLY = "Book was successfully added!";
	protected static final String ACCESS_DENIED = "Only admin can remove book!";
	protected static final String ALL_BOOKS = "Library page";
	protected static final String BOOK_DETAILS = "Information about chosen book:";
	protected static final String REMOVE_BOOKS = "This is a page where you remove chosen book";
	protected static final String SUCCESSFULLY_ADDED = "The book was successfully added to the library.";

	private final BookService bookService;

	@Autowired
	public BookController(BookService bookService) {
		this.bookService = bookService;
	}
/**
 * Metoda wyświetla ksiązki znajdujące się w bibliotece.
 * Zwraca widok html na którym wyświetla ksiązki.
 * @param model
 * @return
 */
	@GetMapping(value = "/books")
	public String showAllBooks(Model model) {
		List<BookTo> booksList = bookService.findAllBooks();
		model.addAttribute(ModelConstants.MESSAGE, ALL_BOOKS);
		model.addAttribute(ModelConstants.INFO, ALL_BOOKS_INFO);
		model.addAttribute(ModelConstants.BOOKS_LIST, booksList);
		return ViewNames.BOOKS;
	}
/**
 * Metoda wyświetlająca szczegółowe dane wybranej ksiązki.
 * @param id
 * @param model
 * @return
 */
	@GetMapping(value = "/books/book")
	public String showBook(@RequestParam(value = "id", defaultValue = "") Long id, Model model) {
		BookTo wantedBook = bookService.getBook(id);
		model.addAttribute(ModelConstants.INFO, BOOK_DETAILS);
		model.addAttribute(ModelConstants.BOOK, wantedBook);
		return ViewNames.BOOK;
	}
/**
 * Metoda przekierowująca do formularza, gdzie wprowadza się dane nowej ksiazki.
 * Dostępna jedynie dla zalogowanego usera i admina.
 * @param newBook
 * @param model
 * @return
 */
	@Secured({ "ROLE_USER", "ROLE_ADMIN" }) 
	@GetMapping(value = "/books/add")
	public String showAddingBookPage(@ModelAttribute("newBook") BookTo newBook, Model model) {
		model.addAttribute(ModelConstants.INFO, ADD_BOOK_INFO);
		return ViewNames.ADD_BOOK;
	}
/**
 * Controller ktory umozliwia dodanie ksiazki do biblioteki. Na podstawie wprowadoznych danych
 *  tworzona jest nowa ksiazka i dodana do biblioteki.
 * @param newBook
 * @param model
 * @return
 */
	@Secured({ "ROLE_USER","ROLE_ADMIN" })
	@PostMapping(value = "/greeting")
	public String addBook(@RequestAttribute("newBook") BookTo newBook, Model model) {
		bookService.saveBook(newBook);
		model.addAttribute(ModelConstants.INFO, BOOK_ADDED_SUCCESSFULLY);
		return ViewNames.BOOK_ADDED;
	}
/**
 * Metoda umozliwiajaca usuniecie wybranej ksiazki z listy ksiazek.
 * @param id
 * @param model
 * @return
 */
	@Secured({ "ROLE_ADMIN" })
	@DeleteMapping(value = "/books/removedBook")
	public String removeBook(@RequestParam(value = "id", defaultValue = "") Long id, Model model) {
		model.addAttribute(ModelConstants.INFO, REMOVED_BOOK_TITLE +bookService.getBook(id).getTitle());
		bookService.deleteBook(id);
		return ViewNames.REMOVED_BOOK;
	}
/**
 * Controller przekierowujacy do wyszukiwarki ksiazek po parametrach.
 * @param model
 * @return
 */
	@GetMapping(value = "/books/search")
	public String showSearchPage(Model model) {
		model.addAttribute("newBook", new BookTo());
		model.addAttribute(ModelConstants.INFO, SEARCH_INFO);
		return ViewNames.FIND_BOOK;
	}
/**
 * Controller pobierajacy wprowadzone dane z formularza i wyswietlajacy liste ksiazek
 *  o przekazanych parametrach
 * @param wantedBooks
 * @param model
 * @return
 */
	@GetMapping(value = "/searching")
	public String searchBook(@ModelAttribute("wantedBooks") BookTo wantedBooks, Model model) {
		List<BookTo> books = bookService.findBooksByAuthorAndTitle(wantedBooks);
		model.addAttribute(ModelConstants.INFO, SEARCH_RESULT_INFO);
		model.addAttribute(ModelConstants.BOOKS_LIST, books);
		return ViewNames.FOUND_BOOKS;
	}

	/**
	 * Controller obslugujacy wyjątek AccessDenied - gdy nieuprawiony uzytkownik (user 
	 * lub niezalogowany uzytkownik) probuje usunac ksiazke z bliblioteki
	 * @param model
	 * @return
	 */
	@ExceptionHandler({ AccessDeniedException.class })
	public String handleException(Model model) {
		model.addAttribute(ModelConstants.INFO, ACCESS_DENIED);
		return ViewNames.ERROR_403;
	}
}