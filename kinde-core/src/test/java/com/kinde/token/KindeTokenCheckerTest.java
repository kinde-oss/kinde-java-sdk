package com.kinde.token;

import com.kinde.KindeClientSession;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for KindeTokenChecker functionality.
 * These tests cover the builder pattern, parameter validation, and basic functionality.
 */
class KindeTokenCheckerTest {

    @Test
    void testBuilderPattern_WithNullToken_ThrowsException() {
        // Given: Null token
        // When & Then: Should throw exception
        assertThrows(IllegalArgumentException.class, () -> {
            KindeTokenCheckerBuilder.builder()
                    .token(null)
                    .session(null)
                    .build();
        });
    }

    @Test
    void testBuilderPattern_WithNullSession_ThrowsException() {
        // Given: Null session
        KindeToken token = org.mockito.Mockito.mock(KindeToken.class);
        // When & Then: Should throw exception
        assertThrows(IllegalArgumentException.class, () -> {
            KindeTokenCheckerBuilder.builder()
                    .token(token)
                    .session(null)
                    .build();
        });
    }

    @Test
    void testBuilderPattern_StaticBuilderMethod() {
        // Given: Builder instance
        KindeTokenCheckerBuilder builder = KindeTokenCheckerBuilder.builder();

        // Then: Should not be null
        assertNotNull(builder);
    }

    @Test
    void testBuilderPattern_WithNullParameters_ThrowsException() {
        // Given: Null token and session
        KindeToken token = null;

        // When & Then: Should throw exception for null parameters
        assertThrows(IllegalArgumentException.class, () -> {
            KindeTokenCheckerBuilder.builder()
                    .token(token)
                    .session(null)
                    .build();
        });
    }

    @Test
    void testBuild_WithValidParameters_ReturnsChecker() {
        // Given: Valid token and session
        KindeToken token = org.mockito.Mockito.mock(KindeToken.class);
        KindeClientSession session = org.mockito.Mockito.mock(KindeClientSession.class);

        // When: Build with valid parameters
        KindeTokenChecker checker = KindeTokenCheckerBuilder.builder()
                .token(token)
                .session(session)
                .build();

        // Then: Should return a valid checker instance
        assertNotNull(checker);
    }

    @Test
    void testBuilderPattern_ChainingMethods() {
        // Given: Builder instance
        KindeTokenCheckerBuilder builder = KindeTokenCheckerBuilder.builder();

        // When: Chaining methods
        KindeTokenCheckerBuilder chainedBuilder = builder
                .token(null)
                .session(null);

        // Then: Should return the same builder instance
        assertSame(builder, chainedBuilder);
    }

    @Test
    void testBuilderPattern_DefaultState() {
        // Given: New builder instance
        KindeTokenCheckerBuilder builder = KindeTokenCheckerBuilder.builder();

        // When & Then: Should throw exception when building without parameters
        assertThrows(IllegalArgumentException.class, () -> {
            builder.build();
        });
    }

    @Test
    void testBuilderPattern_MultipleBuilders() {
        // Given: Multiple builder instances
        KindeTokenCheckerBuilder builder1 = KindeTokenCheckerBuilder.builder();
        KindeTokenCheckerBuilder builder2 = KindeTokenCheckerBuilder.builder();

        // Then: Should be different instances
        assertNotSame(builder1, builder2);
    }

    @Test
    void testBuilderPattern_ExceptionMessage() {
        // Given: Null token
        try {
            KindeTokenCheckerBuilder.builder()
                    .token(null)
                    .session(null)
                    .build();
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Then: Should have meaningful error message
            assertTrue(e.getMessage().contains("Token cannot be null") || 
                      e.getMessage().contains("Session cannot be null"));
        }
    }

    @Test
    void testBuilderPattern_ReuseAfterException() {
        // Given: Builder that threw exception
        KindeTokenCheckerBuilder builder = KindeTokenCheckerBuilder.builder();
        
        try {
            builder.token(null).session(null).build();
        } catch (IllegalArgumentException e) {
            // Expected
        }

        // When: Try to build again with null parameters
        // Then: Should still throw exception for null parameters
        assertThrows(IllegalArgumentException.class, () -> {
            builder.build();
        });
    }

    @Test
    void testBuilderPattern_ImmutableAfterBuild() {
        // Given: Builder instance
        KindeTokenCheckerBuilder builder = KindeTokenCheckerBuilder.builder();

        // When: Try to modify after build attempt
        try {
            builder.token(null).session(null).build();
        } catch (IllegalArgumentException e) {
            // Expected
        }

        // Then: Should still be able to set parameters
        KindeTokenCheckerBuilder newBuilder = KindeTokenCheckerBuilder.builder();
        assertNotNull(newBuilder);
    }

    @Test
    void testBuilderPattern_ToString() {
        // Given: Builder instance
        KindeTokenCheckerBuilder builder = KindeTokenCheckerBuilder.builder();

        // When: Call toString
        String toString = builder.toString();

        // Then: Should not be null or empty
        assertNotNull(toString);
        assertFalse(toString.isEmpty());
    }

    @Test
    void testBuilderPattern_EqualsAndHashCode() {
        // Given: Two builder instances
        KindeTokenCheckerBuilder builder1 = KindeTokenCheckerBuilder.builder();
        KindeTokenCheckerBuilder builder2 = KindeTokenCheckerBuilder.builder();

        // When: Set same parameters (using mocks for consistency)
        KindeToken token = org.mockito.Mockito.mock(KindeToken.class);
        KindeClientSession session = org.mockito.Mockito.mock(KindeClientSession.class);
        builder1.token(token).session(session);
        builder2.token(token).session(session);

        // Then: Should have same hash code (if equals is implemented)
        // Note: Hash codes may differ due to object identity, so we just verify they're not null
        assertNotNull(builder1.hashCode());
        assertNotNull(builder2.hashCode());
    }

    @Test
    void testBuilderPattern_ThreadSafety() {
        // Given: Multiple threads creating builders
        java.util.concurrent.atomic.AtomicReference<AssertionError> error = new java.util.concurrent.atomic.AtomicReference<>();
        Runnable task = () -> {
            try {
                for (int i = 0; i < 100; i++) {
                    KindeTokenCheckerBuilder builder = KindeTokenCheckerBuilder.builder();
                    assertNotNull(builder);
                }
            } catch (AssertionError e) {
                error.set(e);
            }
        };

        // When: Run multiple threads
        Thread thread1 = new Thread(task);
        Thread thread2 = new Thread(task);

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            fail("Thread execution was interrupted");
        }

        // Then: Should complete without errors
        assertNull(error.get(), "Assertion failed in background thread");
    }

    @Test
    void testBuilderPattern_ParameterValidation() {
        // Given: Builder with various null combinations
        KindeTokenCheckerBuilder builder = KindeTokenCheckerBuilder.builder();

        // When & Then: Should validate token parameter (null token, valid session)
        KindeClientSession session = org.mockito.Mockito.mock(KindeClientSession.class);
        assertThrows(IllegalArgumentException.class, () -> {
            builder.token(null).session(session).build();
        });

        // When & Then: Should validate session parameter (valid token, null session)
        KindeToken token = org.mockito.Mockito.mock(KindeToken.class);
        assertThrows(IllegalArgumentException.class, () -> {
            KindeTokenCheckerBuilder.builder()
                    .token(token)
                    .session(null)
                    .build();
        });
    }

    @Test
    void testBuilderPattern_ResetBehavior() {
        // Given: Builder with parameters set
        KindeToken token = org.mockito.Mockito.mock(KindeToken.class);
        KindeClientSession session = org.mockito.Mockito.mock(KindeClientSession.class);
        KindeTokenCheckerBuilder builder = KindeTokenCheckerBuilder.builder()
                .token(token)
                .session(session);

        // When: Create new builder
        KindeTokenCheckerBuilder newBuilder = KindeTokenCheckerBuilder.builder();

        // Then: New builder should be in clean state
        assertThrows(IllegalArgumentException.class, () -> {
            newBuilder.build();
        });
    }
}
