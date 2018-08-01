package pl.jstk.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import pl.jstk.constants.ModelConstants;
import pl.jstk.constants.ViewNames;
import pl.jstk.service.BookService;
import pl.jstk.to.BookTo;

@Controller
public class BookController {
	private static final String INFO_TEXT_0 = "There are displayed books in the library";
	private static final String INFO_TEXT_1 = "Searching result:";
	private static final String INFO_TEXT_2 = "This is a page where you can add new book";
	private static final String INFO_TEXT_3 = "Removed book title: ";
	private static final String INFO_TEXT_4 = "You can search for a book";
	private static final String INFO_TEXT_5 = "Book was successfully added!";
	private static final String ACCESS_DENIED="Only admin can remove book!";
	protected static final String ALL_BOOKS = "Library page";
	protected static final String BOOK_DETAILS = "Information about chosen book:";
	protected static final String REMOVE_BOOKS = "This is a page where you remove chosen book";
	protected static final String SUCCESSFULLY_ADDED = "The book was successfully added to the library.";
	

	BookService bookService;

	@Autowired
	public BookController(BookService bookService) {
		this.bookService = bookService;
	}

	@GetMapping(value = "/books")
	public String showAllBooks(Model model) {
		List<BookTo> booksList = bookService.findAllBooks();
		model.addAttribute(ModelConstants.MESSAGE, ALL_BOOKS);
		model.addAttribute(ModelConstants.INFO, INFO_TEXT_0);
		model.addAttribute(ModelConstants.BOOKS_LIST, booksList);

		return ViewNames.BOOKS;
	}

	@GetMapping(value = "/books/book")
	public String showBook(@RequestParam(value = "id", defaultValue = "") Long id, Model model) {

		BookTo wantedBook = bookService.getBook(id);
		model.addAttribute(ModelConstants.INFO, BOOK_DETAILS);
		model.addAttribute(ModelConstants.BOOK, wantedBook);

		return ViewNames.BOOK;
	}

	@Secured({ "ROLE_USER", "ROLE_ADMIN" }) 
	@GetMapping(value = "/books/add")
	public String createBook(Model model) {
		model.addAttribute("newBook", new BookTo());
		model.addAttribute(ModelConstants.INFO, INFO_TEXT_2);

		return ViewNames.ADD_BOOK;
	}

	@PostMapping(value = "/greeting")
	public String addBook(@ModelAttribute("newBook") BookTo newBook, Model model) {

		bookService.saveBook(newBook);
		model.addAttribute("newBook", new BookTo());
		model.addAttribute(ModelConstants.INFO, INFO_TEXT_5);
		
		return ViewNames.BOOK_ADDED;
	}

	@Secured("ROLE_ADMIN")
	@DeleteMapping(value = "/books/removedBook")
	public String removeBook(@RequestParam(value = "id", defaultValue = "") Long id, Model model) {

		model.addAttribute(ModelConstants.INFO, INFO_TEXT_3+bookService.getBook(id).getTitle());
		bookService.deleteBook(id);
		return "removedBook";
	}
	@ExceptionHandler({AccessDeniedException.class})
    public String handleException(Model model) {
        model.addAttribute(ModelConstants.INFO,ACCESS_DENIED);
        return ViewNames.ERROR_403;
    }

	@GetMapping(value = "/books/search")
	public String findBook(Model model) {
		model.addAttribute("newBook", new BookTo());
		model.addAttribute(ModelConstants.INFO, INFO_TEXT_4);

		return ViewNames.FIND_BOOK;
	}

	@GetMapping(value = "/searching")
	public String searchBook(@ModelAttribute("wantedBooks") BookTo wantedBooks, Model model) {
		List<BookTo> books = bookService.findBooksByAuthorAndTitle(wantedBooks);
		
		model.addAttribute(ModelConstants.INFO, INFO_TEXT_1);
		model.addAttribute(ModelConstants.BOOKS_LIST, books);
		return ViewNames.FOUND_BOOKS;
	}

}