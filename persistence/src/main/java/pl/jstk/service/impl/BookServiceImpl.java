package pl.jstk.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pl.jstk.entity.BookEntity;
import pl.jstk.mapper.BookMapper;
import pl.jstk.repository.BookRepository;
import pl.jstk.service.BookService;
import pl.jstk.to.BookTo;

@Service
@Transactional(readOnly = true)
public class BookServiceImpl implements BookService {

	private BookRepository bookRepository;

	@Autowired
	public BookServiceImpl(BookRepository bookRepository) {
		this.bookRepository = bookRepository;
	}

	@Override
	public List<BookTo> findAllBooks() {
		return BookMapper.map2To(bookRepository.findAll());
	}

	@Override
	public List<BookTo> findBooksByTitle(String title) {
		return BookMapper.map2To(bookRepository.findBookByTitle(title));
	}

	@Override
	public List<BookTo> findBooksByAuthor(String author) {
		return BookMapper.map2To(bookRepository.findBookByAuthor(author));

	}

	public List<BookTo> findBooksByAuthorAndTitle(BookTo searchedBooks) {
		List<BookTo> searchedBooksList = new ArrayList<>();
		

		String title = searchedBooks.getTitle().toLowerCase();
		String authors = searchedBooks.getAuthors().toLowerCase();
		
		if (title.isEmpty() && authors.isEmpty()) {
			return searchedBooksList;
		}
		if (title=="" && authors=="") {
				searchedBooksList = null;
			}
		if (title.equals("") && !authors.equals("")) {
			searchedBooksList = findBooksByAuthor(authors);
		}
		if (!title.equals("") && authors.equals("")) {
			searchedBooksList = findBooksByTitle(title);
		}
		if (!title.equals("") && !authors.equals("")) {
			searchedBooksList = findBooksByTitle(title);
			searchedBooksList = searchedBooksList.stream()
												.filter(book -> book.getAuthors().toLowerCase().contains(authors))
												.collect(Collectors.toList());

		}
		return searchedBooksList;

	}

	@Override
	public BookTo getBook(Long id) {

		return BookMapper.map(bookRepository.getOne(id));
	}

	@Override
	@Transactional
	public BookTo saveBook(BookTo book) {
		BookEntity entity = BookMapper.map(book);
		entity = bookRepository.save(entity);
		return BookMapper.map(entity);
	}

	@Override
	@Transactional
	public void deleteBook(Long id) {
		bookRepository.deleteById(id);

	}
}