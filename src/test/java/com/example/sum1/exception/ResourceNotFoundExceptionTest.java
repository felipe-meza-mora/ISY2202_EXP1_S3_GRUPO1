package com.example.sum1.exception;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;




class ResourceNotFoundExceptionTest {

    @Test
    void testResourceNotFoundExceptionMessage() {
        String errorMessage = "Resource not found";
        ResourceNotFoundException exception = new ResourceNotFoundException(errorMessage);
        assertEquals(errorMessage, exception.getMessage());
    }
}