package pl.jstk.constants;

import java.io.Serializable;

public final class ViewNames implements Serializable {

	private static final long serialVersionUID = 1L;

	/* 
	 Error resolving template "dupa", template might not exist or might not be accessible by any of the configured Template Resolvers
	at org.thymeleaf.engine.TemplateManager.resolveTemplate(TemplateManager.java:870)
	
	--czyli nazwa musi być zgodna z plikami w web/scr/main/resources !!!
	 */
	public static final String BOOKS= "books";
	public static final String LOGIN = "login";
	public static final String WELCOME = "welcome";
	public static final String BOOK= "book";
	public static final String ADD_BOOK="addBook";
	public static final String FIND_BOOK="findBook";
	public static final String BOOK_ADDED="successfullyAdded";
	public static final String FOUND_BOOKS="foundBooks";
	public static final String ERROR_403="403";
}
