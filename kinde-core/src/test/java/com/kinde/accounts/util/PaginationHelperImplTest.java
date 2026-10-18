package com.kinde.accounts.util;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.*;

class PaginationHelperImplTest {

    private final PaginationHelperImpl helper = new PaginationHelperImpl();

    @Test
    void paginatesUntilNextPageTokenIsAbsent() {
        List<String> cursors = new ArrayList<>();

        List<String> values = helper.paginateAsync((cursor, size) -> {
            assertEquals(100, size);
            cursors.add(cursor);
            return cursor == null
                    ? new Page(List.of("first"), new Metadata("next"))
                    : new Page(List.of("second"), new Metadata(null));
        }, Page::getData, items -> items.stream().map(String::toUpperCase).toList(), page -> page.getMetadata().getNextPageStartingAfter())
                .join();

        assertEquals(java.util.Arrays.asList(null, "next"), cursors);
        assertEquals(List.of("FIRST", "SECOND"), values);
    }

    @Test
    void defaultExtractorReadsMetadataAndNullPageDataIsIgnored() {
        List<String> values = helper.paginateAsync(
                (cursor, size) -> cursor == null
                        ? new Page(null, new Metadata("next"))
                        : new Page(List.of("last"), new Metadata(null)),
                Page::getData,
                List::copyOf)
                .join();

        assertEquals(List.of("last"), values);
    }

    @Test
    void apiFailureCompletesFutureExceptionallyWithPaginationContext() {
        CompletionException exception = assertThrows(CompletionException.class, () ->
                helper.paginateAsync(
                        (cursor, size) -> {
                            throw new IllegalStateException("offline");
                        },
                        value -> List.of(value),
                        List::copyOf,
                        value -> null)
                        .join());

        assertEquals("Failed to retrieve paginated data", exception.getCause().getMessage());
        assertInstanceOf(IllegalStateException.class, exception.getCause().getCause());
    }

    private static final class Page {
        private final List<String> data;
        private final Metadata metadata;

        private Page(List<String> data, Metadata metadata) {
            this.data = data;
            this.metadata = metadata;
        }

        public List<String> getData() {
            return data;
        }

        public Metadata getMetadata() {
            return metadata;
        }
    }

    private static final class Metadata {
        private final String nextPageStartingAfter;

        private Metadata(String nextPageStartingAfter) {
            this.nextPageStartingAfter = nextPageStartingAfter;
        }

        public String getNextPageStartingAfter() {
            return nextPageStartingAfter;
        }
    }
}
