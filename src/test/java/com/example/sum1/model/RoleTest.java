package com.example.sum1.model;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;




class RoleTest {

    @Test
     void testRoleAdmin() {
        Role role = Role.ROLE_ADMIN;
        assertEquals("ROLE_ADMIN", role.name());
    }

    @Test
     void testRoleUser() {
        Role role = Role.ROLE_USER;
        assertEquals("ROLE_USER", role.name());
    }

    @Test
     void testRoleValues() {
        Role[] roles = Role.values();
        assertEquals(2, roles.length);
        assertEquals(Role.ROLE_ADMIN, roles[0]);
        assertEquals(Role.ROLE_USER, roles[1]);
    }

    @Test
     void testRoleValueOf() {
        Role roleAdmin = Role.valueOf("ROLE_ADMIN");
        Role roleUser = Role.valueOf("ROLE_USER");
        assertEquals(Role.ROLE_ADMIN, roleAdmin);
        assertEquals(Role.ROLE_USER, roleUser);
    }
}