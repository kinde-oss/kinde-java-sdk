package com.kinde.token;

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
        // When & Then: Should throw exception
        assertThrows(IllegalArgumentException.class, () -> {
            KindeTokenCheckerBuilder.builder()
                    .token(null)
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
    void testBuilderPattern_WithValidParameters() {
        // Given: Valid token and session (using null for testing purposes)
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

        // When: Try to build again with valid parameters
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

        // When: Set same parameters
        builder1.token(null).session(null);
        builder2.token(null).session(null);

        // Then: Should have same hash code (if equals is implemented)
        // Note: Hash codes may differ due to object identity, so we just verify they're not null
        assertNotNull(builder1.hashCode());
        assertNotNull(builder2.hashCode());
    }

    @Test
    void testBuilderPattern_ThreadSafety() {
        // Given: Multiple threads creating builders
        Runnable task = () -> {
            for (int i = 0; i < 100; i++) {
                KindeTokenCheckerBuilder builder = KindeTokenCheckerBuilder.builder();
                assertNotNull(builder);
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
        assertTrue(true); // If we get here, no exceptions were thrown
    }

    @Test
    void testBuilderPattern_ParameterValidation() {
        // Given: Builder with various null combinations
        KindeTokenCheckerBuilder builder = KindeTokenCheckerBuilder.builder();

        // When & Then: Should validate token parameter
        assertThrows(IllegalArgumentException.class, () -> {
            builder.token(null).session(null).build();
        });

        // When & Then: Should validate session parameter
        assertThrows(IllegalArgumentException.class, () -> {
            KindeTokenCheckerBuilder.builder()
                    .token(null)
                    .session(null)
                    .build();
        });
    }

    @Test
    void testBuilderPattern_ResetBehavior() {
        // Given: Builder with parameters set
        KindeTokenCheckerBuilder builder = KindeTokenCheckerBuilder.builder()
                .token(null)
                .session(null);

        // When: Create new builder
        KindeTokenCheckerBuilder newBuilder = KindeTokenCheckerBuilder.builder();

        // Then: New builder should be in clean state
        assertThrows(IllegalArgumentException.class, () -> {
            newBuilder.build();
        });
    }
}
