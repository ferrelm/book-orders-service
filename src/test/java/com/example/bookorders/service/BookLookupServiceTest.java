package com.example.bookorders.service;

import com.example.bookorders.client.BookClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class BookLookupServiceTest {

    @Mock
    BookClient bookClient;

    @InjectMocks
    BookLookupService service;

    @Test
    void getBookTitle_returnsTitle_whenBookFound() {
        given(bookClient.getBookById("b1")).willReturn(new BookClient.BookInfo("b1", "1984", "Orwell"));

        String title = service.getBookTitle("b1");

        assertThat(title).isEqualTo("1984");
    }

    @Test
    void getBookTitle_returnsNull_whenNotFound() {
        given(bookClient.getBookById("missing")).willReturn(null);

        String title = service.getBookTitle("missing");

        assertThat(title).isNull();
    }
}
