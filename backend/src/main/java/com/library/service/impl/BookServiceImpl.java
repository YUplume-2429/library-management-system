package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.library.dto.BookDTO;
import com.library.entity.Book;
import com.library.mapper.BookMapper;
import com.library.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookServiceImpl implements BookService {

    private final BookMapper bookMapper;

    @Override
    public List<BookDTO> getAllBooks() {
        List<Book> books = bookMapper.selectList(new QueryWrapper<Book>().orderByDesc("created_at"));
        return books.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public BookDTO getBookById(Integer bookId) {
        Book book = bookMapper.selectById(bookId);
        return book == null ? null : convertToDTO(book);
    }

    @Override
    public List<BookDTO> searchByTitle(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return getAllBooks();
        }
        List<Book> books = bookMapper.selectByTitleLike(keyword.trim());
        return books.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public BookDTO createBook(BookDTO bookDTO) {
        validateBook(bookDTO);
        ensureIsbnNotExists(bookDTO.getIsbn(), null);

        Book book = convertToEntity(bookDTO);
        book.setBookId(null);
        book.setCreatedAt(LocalDateTime.now());
        book.setUpdatedAt(LocalDateTime.now());
        if (book.getPrice() == null) {
            book.setPrice(BigDecimal.ZERO);
        }
        if (book.getIsAvailable() == null) {
            book.setIsAvailable(true);
        }
        if (book.getStock() == null) {
            book.setStock(0);
        }

        bookMapper.insert(book);
        return convertToDTO(book);
    }

    @Override
    @Transactional
    public List<BookDTO> importBooks(List<BookDTO> bookDTOList) {
        if (bookDTOList == null || bookDTOList.isEmpty()) {
            throw new IllegalArgumentException("Import data cannot be empty");
        }

        Set<String> importedIsbns = new HashSet<>();
        for (int index = 0; index < bookDTOList.size(); index++) {
            BookDTO bookDTO = bookDTOList.get(index);
            validateBook(bookDTO);
            String normalizedIsbn = bookDTO.getIsbn().trim();
            if (!importedIsbns.add(normalizedIsbn)) {
                throw new IllegalArgumentException("Duplicate ISBN in import data at row " + (index + 1) + ": " + normalizedIsbn);
            }
            ensureIsbnNotExists(normalizedIsbn, null);
            bookDTO.setIsbn(normalizedIsbn);
        }

        return bookDTOList.stream()
                .map(this::createBookWithoutDuplicateCheck)
                .collect(Collectors.toList());
    }

    @Override
    public BookDTO updateBook(BookDTO bookDTO) {
        if (bookDTO.getBookId() == null) {
            throw new IllegalArgumentException("Book ID cannot be empty");
        }
        validateBook(bookDTO);

        Book existing = bookMapper.selectById(bookDTO.getBookId());
        if (existing == null) {
            throw new IllegalArgumentException("Book does not exist");
        }
        ensureIsbnNotExists(bookDTO.getIsbn(), bookDTO.getBookId());

        Book merged = mergeBook(existing, bookDTO);
        merged.setUpdatedAt(LocalDateTime.now());
        bookMapper.updateById(merged);
        return convertToDTO(merged);
    }

    @Override
    public boolean deleteBook(Integer bookId) {
        return bookMapper.deleteById(bookId) > 0;
    }

    @Override
    public List<BookDTO> getAvailableBooks() {
        List<Book> books = bookMapper.selectAllAvailable();
        return books.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<BookDTO> getBooksByCategory(String category) {
        if (!StringUtils.hasText(category)) {
            return getAllBooks();
        }
        List<Book> books = bookMapper.selectByCategory(category.trim());
        return books.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<String> getCategories() {
        return bookMapper.selectDistinctCategories();
    }

    private void validateBook(BookDTO bookDTO) {
        if (bookDTO == null) {
            throw new IllegalArgumentException("Book information cannot be empty");
        }
        if (!StringUtils.hasText(bookDTO.getTitle())
                || !StringUtils.hasText(bookDTO.getAuthor())
                || !StringUtils.hasText(bookDTO.getPublisher())
                || !StringUtils.hasText(bookDTO.getIsbn())) {
            throw new IllegalArgumentException("Title, author, publisher and ISBN are required");
        }
        if (bookDTO.getStock() != null && bookDTO.getStock() < 0) {
            throw new IllegalArgumentException("Stock cannot be less than 0");
        }
        if (bookDTO.getPrice() != null && bookDTO.getPrice().signum() < 0) {
            throw new IllegalArgumentException("Price cannot be less than 0");
        }
    }

    private void ensureIsbnNotExists(String isbn, Integer currentBookId) {
        Book existing = bookMapper.selectOne(
                new QueryWrapper<Book>()
                        .eq("isbn", isbn)
                        .last("LIMIT 1")
        );
        if (existing != null && !Objects.equals(existing.getBookId(), currentBookId)) {
            throw new IllegalArgumentException("ISBN already exists: " + isbn);
        }
    }

    private BookDTO createBookWithoutDuplicateCheck(BookDTO bookDTO) {
        Book book = convertToEntity(bookDTO);
        book.setBookId(null);
        book.setCreatedAt(LocalDateTime.now());
        book.setUpdatedAt(LocalDateTime.now());
        if (book.getPrice() == null) {
            book.setPrice(BigDecimal.ZERO);
        }
        if (book.getIsAvailable() == null) {
            book.setIsAvailable(true);
        }
        if (book.getStock() == null) {
            book.setStock(0);
        }

        bookMapper.insert(book);
        return convertToDTO(book);
    }

    private Book mergeBook(Book existing, BookDTO bookDTO) {
        existing.setIsbn(bookDTO.getIsbn());
        existing.setTitle(bookDTO.getTitle());
        existing.setAuthor(bookDTO.getAuthor());
        existing.setPublisher(bookDTO.getPublisher());
        existing.setPublishDate(bookDTO.getPublishDate());
        existing.setCategory(bookDTO.getCategory());
        existing.setPrice(Objects.requireNonNullElse(bookDTO.getPrice(), existing.getPrice()));
        existing.setStock(Objects.requireNonNullElse(bookDTO.getStock(), existing.getStock()));
        existing.setDescription(bookDTO.getDescription());
        existing.setIsAvailable(Objects.requireNonNullElse(bookDTO.getIsAvailable(), existing.getIsAvailable()));
        return existing;
    }

    private BookDTO convertToDTO(Book book) {
        return BookDTO.builder()
                .bookId(book.getBookId())
                .isbn(book.getIsbn())
                .title(book.getTitle())
                .author(book.getAuthor())
                .publisher(book.getPublisher())
                .publishDate(book.getPublishDate())
                .category(book.getCategory())
                .price(book.getPrice())
                .stock(book.getStock())
                .description(book.getDescription())
                .isAvailable(book.getIsAvailable())
                .createdAt(book.getCreatedAt())
                .updatedAt(book.getUpdatedAt())
                .build();
    }

    private Book convertToEntity(BookDTO bookDTO) {
        Book book = new Book();
        book.setBookId(bookDTO.getBookId());
        book.setIsbn(bookDTO.getIsbn());
        book.setTitle(bookDTO.getTitle());
        book.setAuthor(bookDTO.getAuthor());
        book.setPublisher(bookDTO.getPublisher());
        book.setPublishDate(bookDTO.getPublishDate());
        book.setCategory(bookDTO.getCategory());
        book.setPrice(bookDTO.getPrice());
        book.setStock(bookDTO.getStock());
        book.setDescription(bookDTO.getDescription());
        book.setIsAvailable(bookDTO.getIsAvailable());
        return book;
    }
}
