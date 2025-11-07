package com.example.bookorders.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Example Feign client demonstrating how to call a remote book service.
 * Adjust the URL or provide configuration via properties when using in your environment.
 */
@FeignClient(name = "book-service", url = "${book.service.url:http://book-service}")
public interface BookClient {

    @GetMapping("/books/{id}")
    BookInfo getBookById(@PathVariable("id") String id);

    record BookInfo(String id, String title, String author) {}
}
