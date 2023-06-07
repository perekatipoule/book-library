## E-Library
***
Library simulator for managing books, readers, and their interactions

**Technologies:** Java 17, Spring Boot, Spring MVC, Apache Maven, PostgreSQL, Hibernate, Thymeleaf, HTML, CSS, JS.

### Description
***
• Imitation of an electronic library, which provides the ability to manage the circulation of books, readers, as well as build connections between them and display it on the screen accordingly:

<img src="src/main/resources/static/readme_data/main_page.gif" width="800">

• All necessary functionality for adding, editing, assigning a book to a reader, deleting, etc. is implemented. Validation of entered data is also present:

<img src="src/main/resources/static/readme_data/operation.gif" width="600">

• If the reader took the book and did not return it after 10 days (the overdue period can be edited), the book lights up in red:

<img src="src/main/resources/static/readme_data/overdue_book.png" width="400">

• On the page with books, the user can customize the display of books according to several parameters: page number, the number of books on the page, and the presence of sorting:

<img src="src/main/resources/static/readme_data/page_settings.gif" width="600">

• Finally, the function of searching for a book by title or fragment of the title has been implemented:

<img src="src/main/resources/static/readme_data/search_book.gif" width="400">


### Setup
***
 - To run E-Library you should connect PostgreSQL database to project and specify required data in application.properties.
 - Also there is SQL code in src/main/sql folder to create two tables "book" and "person" and add some data to test application.
 - To start app you should run LibraryBootApplication class.
