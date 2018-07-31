package pl.jstk.service;

import java.util.List;

import pl.jstk.to.BookTo;

public interface BookService {

	 List<BookTo> findAllBooks();
	    List<BookTo> findBooksByTitle(String title);
	    List<BookTo> findBooksByAuthor(String author);
	    BookTo getBook(Long id);
	    
	    List<BookTo> findBooksByAuthorAndTitle(BookTo wantedBooks);
	    BookTo saveBook(BookTo book);
	    void deleteBook(Long id);
}
