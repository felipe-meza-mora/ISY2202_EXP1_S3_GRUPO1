package com.example.sum1.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;



class RecetaNotFoundExceptionTest {

    @Test
    void testRecetaNotFoundExceptionMessage() {
        String errorMessage = "Receta not found";
        RecetaNotFoundException exception = new RecetaNotFoundException(errorMessage);
        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    void testRecetaNotFoundExceptionIsRuntimeException() {
        RecetaNotFoundException exception = new RecetaNotFoundException("Receta not found");
        assertTrue(exception instanceof RuntimeException);
    }
}