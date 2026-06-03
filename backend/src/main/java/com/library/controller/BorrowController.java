package com.library.controller;

import com.library.dto.ApiResponse;
import com.library.dto.BorrowRecordDTO;
import com.library.security.CustomUserDetails;
import com.library.service.BorrowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/borrows")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class BorrowController {

    private final BorrowService borrowService;

    @PostMapping("/books/{bookId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<BorrowRecordDTO>> borrowBook(@PathVariable Integer bookId) {
        try {
            BorrowRecordDTO record = borrowService.borrowBook(bookId, getCurrentUser().getUserId());
            return ResponseEntity.ok(ApiResponse.success("Book borrowed successfully", record));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to borrow book", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to borrow book"));
        }
    }

    @PutMapping("/{recordId}/return")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    public ResponseEntity<ApiResponse<BorrowRecordDTO>> returnBook(@PathVariable Integer recordId) {
        try {
            CustomUserDetails user = getCurrentUser();
            boolean admin = "ROLE_ADMIN".equals(user.getRole());
            BorrowRecordDTO record = borrowService.returnBook(recordId, user.getUserId(), admin);
            return ResponseEntity.ok(ApiResponse.success("Book returned successfully", record));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to return book", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to return book"));
        }
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<List<BorrowRecordDTO>>> getMyRecords() {
        try {
            List<BorrowRecordDTO> records = borrowService.getUserRecords(getCurrentUser().getUserId());
            return ResponseEntity.ok(ApiResponse.success("Borrow records fetched successfully", records));
        } catch (Exception e) {
            log.error("Failed to fetch my borrow records", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to fetch borrow records"));
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<BorrowRecordDTO>>> getAllRecords() {
        try {
            List<BorrowRecordDTO> records = borrowService.getAllRecords();
            return ResponseEntity.ok(ApiResponse.success("Borrow records fetched successfully", records));
        } catch (Exception e) {
            log.error("Failed to fetch borrow records", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to fetch borrow records"));
        }
    }

    private CustomUserDetails getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            throw new IllegalArgumentException("Not authenticated");
        }
        return userDetails;
    }
}
