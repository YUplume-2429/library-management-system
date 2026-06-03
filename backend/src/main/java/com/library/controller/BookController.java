package com.library.controller;

import com.library.dto.ApiResponse;
import com.library.dto.BookDTO;
import com.library.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class BookController {

    private final BookService bookService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<BookDTO>>> getAllBooks() {
        try {
            List<BookDTO> books = bookService.getAllBooks();
            return ResponseEntity.ok(ApiResponse.success("Books fetched successfully", books));
        } catch (Exception e) {
            log.error("Failed to fetch books", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to fetch books"));
        }
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<ApiResponse<BookDTO>> getBookById(@PathVariable Integer bookId) {
        try {
            BookDTO book = bookService.getBookById(bookId);
            if (book == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(404, "Book not found"));
            }
            return ResponseEntity.ok(ApiResponse.success("Book fetched successfully", book));
        } catch (Exception e) {
            log.error("Failed to fetch book detail", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to fetch book detail"));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<BookDTO>>> searchBooks(@RequestParam(required = false) String keyword) {
        try {
            List<BookDTO> books = bookService.searchByTitle(keyword);
            return ResponseEntity.ok(ApiResponse.success("Search successful", books));
        } catch (Exception e) {
            log.error("Failed to search books", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to search books"));
        }
    }

    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<BookDTO>>> getAvailableBooks() {
        try {
            List<BookDTO> books = bookService.getAvailableBooks();
            return ResponseEntity.ok(ApiResponse.success("Available books fetched successfully", books));
        } catch (Exception e) {
            log.error("Failed to fetch available books", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to fetch available books"));
        }
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<String>>> getCategories() {
        try {
            List<String> categories = bookService.getCategories();
            return ResponseEntity.ok(ApiResponse.success("Categories fetched successfully", categories));
        } catch (Exception e) {
            log.error("Failed to fetch categories", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to fetch categories"));
        }
    }

    @GetMapping("/category")
    public ResponseEntity<ApiResponse<List<BookDTO>>> getBooksByCategory(@RequestParam(required = false) String category) {
        try {
            List<BookDTO> books = bookService.getBooksByCategory(category);
            return ResponseEntity.ok(ApiResponse.success("Books fetched successfully", books));
        } catch (Exception e) {
            log.error("Failed to fetch books by category", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to fetch books by category"));
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BookDTO>> createBook(@RequestBody BookDTO bookDTO) {
        try {
            BookDTO createdBook = bookService.createBook(bookDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Book created successfully", createdBook));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to create book", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to create book"));
        }
    }

    @PostMapping("/import")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<BookDTO>>> importBooks(@RequestBody List<BookDTO> bookDTOList) {
        try {
            List<BookDTO> importedBooks = bookService.importBooks(bookDTOList);
            return ResponseEntity.ok(ApiResponse.success("Books imported successfully", importedBooks));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to import books", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to import books"));
        }
    }

    @PutMapping("/{bookId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BookDTO>> updateBook(@PathVariable Integer bookId, @RequestBody BookDTO bookDTO) {
        try {
            bookDTO.setBookId(bookId);
            BookDTO updatedBook = bookService.updateBook(bookDTO);
            return ResponseEntity.ok(ApiResponse.success("Book updated successfully", updatedBook));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to update book", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to update book"));
        }
    }

    @DeleteMapping("/{bookId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteBook(@PathVariable Integer bookId) {
        try {
            boolean deleted = bookService.deleteBook(bookId);
            if (!deleted) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(404, "Book not found"));
            }
            return ResponseEntity.ok(ApiResponse.success("Book deleted successfully", null));
        } catch (Exception e) {
            log.error("Failed to delete book", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to delete book"));
        }
    }
}
