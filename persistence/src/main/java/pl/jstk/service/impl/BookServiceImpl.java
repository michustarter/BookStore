package pl.jstk.service.impl;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collector;
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

	public List<BookTo> findBooksByAuthorAndTitle(BookTo wantedBooks) {

		if (wantedBooks == null) {
			return BookMapper.map2To(bookRepository.findAll());
		}

		String title = wantedBooks.getTitle();
		String author = wantedBooks.getAuthors();
		List<BookTo> listByTitle = findBooksByTitle(title);
		List<BookTo> listByAuthor = findBooksByAuthor(author);
		List<BookTo> all= findAllBooks();

		if (title.equals("")) {
			return findBooksByAuthor(author);
		}
		if (author.equals("")) {
			return findBooksByTitle(title);
		} else if  (!title.equals("") && !author.equals("")) {
		return listByTitle = listByTitle.stream().filter(book -> listByAuthor.contains(author))
					.collect(Collectors.toList());
			/*return all=all.stream().filter(book -> listByAuthor.contains(book) && listByTitle.contains(book))
					.collect(Collectors.toList());*/
		}
		return null;
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