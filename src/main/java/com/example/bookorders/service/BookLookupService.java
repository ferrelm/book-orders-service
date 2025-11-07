package com.example.bookorders.service;

import com.example.bookorders.client.BookClient;
import org.springframework.stereotype.Service;

/**
 * Small example service that demonstrates using a Feign client.
 */
@Service
public class BookLookupService {

    private final BookClient bookClient;

    public BookLookupService(BookClient bookClient) {
        this.bookClient = bookClient;
    }

    /**
     * Return the title for a book id or null when not found.
     */
    public String getBookTitle(String id) {
        var info = bookClient.getBookById(id);
        return info == null ? null : info.title();
    }
}
