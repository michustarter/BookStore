package pl.jstk.controller;
import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import pl.jstk.constants.ModelConstants;
import pl.jstk.constants.ViewNames;
import pl.jstk.enumerations.BookStatus;
import pl.jstk.service.BookService;
import pl.jstk.to.BookTo;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class BookControllerTest {

	@Autowired
	private WebApplicationContext context;

	@MockBean
	private BookService mockBookService;

	private MockMvc mockMvc;
	private List<BookTo> booksList;
	private BookTo firstBook;
	private BookTo secondBook;

	@Before
	public void setUp() throws Exception {
		booksList = new ArrayList<>();
		firstBook = new BookTo(12L,"title 12","author 12", BookStatus.LOAN);
		secondBook = new BookTo(13L,"title 13","author 13", BookStatus.FREE);

		mockMvc = MockMvcBuilders.webAppContextSetup(context)
				.apply(SecurityMockMvcConfigurers.springSecurity())
				.build();
	}

	@Test
	public void shouldShowAllBooks() throws Exception {
		// given
		booksList.add(firstBook);
		booksList.add(secondBook);
		when(mockBookService.findAllBooks()).thenReturn(booksList);

		// when
		ResultActions resultActions = mockMvc.perform(get("/books"));

		// then
		resultActions
			.andExpect(status().isOk())
			.andExpect(view().name(ViewNames.BOOKS))
			.andDo(print())
			.andExpect(model().attribute(ModelConstants.MESSAGE, BookController.ALL_BOOKS))
			.andDo(print())
			.andExpect(model().attribute(ModelConstants.INFO, BookController.ALL_BOOKS_INFO))
			.andExpect(model().attribute(ModelConstants.BOOKS_LIST, booksList));

		verify(mockBookService).findAllBooks();
		verifyNoMoreInteractions(mockBookService);
	}

	@Test
	public void shouldShowEmptyListBooks() throws Exception {
		// given
		when(mockBookService.findAllBooks()).thenReturn(Collections.emptyList());

		// when
		ResultActions resultActions = mockMvc.perform(get("/books"));

		// then
		resultActions
			.andExpect(status().isOk())
			.andExpect(view().name(ViewNames.BOOKS))
			.andDo(print())
			.andExpect(model().attribute(ModelConstants.MESSAGE, BookController.ALL_BOOKS))
			.andDo(print())
			.andExpect(model().attribute(ModelConstants.INFO, BookController.ALL_BOOKS_INFO))
			.andExpect(model().attribute(ModelConstants.BOOKS_LIST, booksList));

		verify(mockBookService, times(1)).findAllBooks();
		verifyNoMoreInteractions(mockBookService);
	}

	@Test
	public void shouldShowBookDetails() throws Exception {
		// given
		when(mockBookService.getBook(eq(firstBook.getId()))).thenReturn(firstBook);

		// when
		ResultActions resultActions = mockMvc.perform(
				get("/books/book")
					.param("id", firstBook.getId().toString()));

		// then
		resultActions
			.andExpect(status().isOk())
			.andExpect(view().name(ViewNames.BOOK))
			.andDo(print())
			.andExpect(model().attribute(ModelConstants.INFO, BookController.BOOK_DETAILS))
			.andDo(print())
			.andExpect(model().attribute(ModelConstants.BOOK, firstBook));

		verify(mockBookService, times(1)).getBook(firstBook.getId());
		verifyNoMoreInteractions(mockBookService);
	}

	@Test
	@WithMockUser(username = "admin", roles = {"ADMIN"})
	public void shouldDisplayAddBookPage() throws Exception {
		// when
		ResultActions resultActions = mockMvc.perform(get("/books/add"));

		// then
		resultActions
			.andExpect(status().isOk())
			.andExpect(view().name(ViewNames.ADD_BOOK))
			.andDo(print())
			.andExpect(content().string(containsString("")))
			.andExpect(model().attribute(ModelConstants.INFO, BookController.ADD_BOOK_INFO));

		verifyNoMoreInteractions(mockBookService);
	}

	@Test
	@WithMockUser(username = "admin", roles = {"ADMIN"})
	public void shouldAddNewBook() throws Exception {
		// given
		BookTo newBook = new BookTo(3L, "title 3", "author 3", BookStatus.FREE);

		when(mockBookService.saveBook(eq(newBook))).thenReturn(newBook);

		// when
		ResultActions resultActions = mockMvc.perform(
				post("/greeting")
					.with(csrf())
					.requestAttr("newBook", newBook));

		// then
		resultActions
			.andExpect(status().isOk())
			.andExpect(view().name(ViewNames.BOOK_ADDED))
			.andDo(print())
			.andExpect(model().attribute(ModelConstants.INFO, BookController.BOOK_ADDED_SUCCESSFULLY));

		verify(mockBookService, times(1)).saveBook(newBook);
		verifyNoMoreInteractions(mockBookService);
	}

	@Test
	@WithMockUser(username = "admin", roles = {"ADMIN"})
	public void removeBook() throws Exception {
		// given
		when(mockBookService.getBook(eq(12L))).thenReturn(firstBook);

		// when
		ResultActions resultActions = mockMvc.perform(
				delete("/books/removedBook")
						.with(csrf())
						.param("id", "12"));

		// then
		resultActions
				.andExpect(status().isOk())
				.andExpect(view().name(ViewNames.REMOVED_BOOK))
				.andDo(print())
				.andExpect(model().attribute(ModelConstants.INFO, BookController.REMOVED_BOOK_TITLE + firstBook.getTitle()));

		verify(mockBookService, times(1)).getBook(12L);
		verify(mockBookService, times(1)).deleteBook(12L);
		verifyNoMoreInteractions(mockBookService);
	}
	
	@Test
	public void shouldFindBook() throws Exception {
		// given
		booksList.add(firstBook);
		booksList.add(secondBook);
		BookTo searchedBook= firstBook;
		List<BookTo>foundedBooks= new ArrayList<>();
		foundedBooks.add(searchedBook);
		
		
		when(mockBookService.findBooksByAuthorAndTitle(eq(firstBook))).thenReturn(foundedBooks);
		// when
		ResultActions resultActions = mockMvc.perform(
				get("/searching")
					.with(csrf())
					.requestAttr("searchedBook", searchedBook));

		// then
		resultActions
			.andExpect(status().isOk())
			.andExpect(view().name(ViewNames.FOUND_BOOKS))
			.andDo(print())
			.andExpect(model().attribute(ModelConstants.INFO, BookController.SEARCH_RESULT_INFO))
			.andExpect(model().attribute(ModelConstants.BOOKS_LIST, foundedBooks));

		verify(mockBookService, times(1)).getBook(12L);
		verifyNoMoreInteractions(mockBookService);
	}
}
