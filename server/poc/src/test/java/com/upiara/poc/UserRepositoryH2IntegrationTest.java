package com.upiara.poc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.upiara.poc.model.User;
import com.upiara.poc.repository.UserRepository;

/**
 * Integration tests for the "test" profile that uses H2 + SQL init scripts.
 *
 * Validates that:
 * - Spring context loads with the H2 datasource
 * - VW_USERS view is readable via JPA entity mapping
 * - Writes against VW_USERS succeed due to H2 INSTEAD OF trigger emulation
 */
@SpringBootTest
@ActiveProfiles("test")
class UserRepositoryH2IntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void repositoryCanReadSeededUsersFromView() {
        List<User> users = userRepository.findAll();
        assertNotNull(users);
        assertTrue(users.size() >= 2, "Expected seeded users to be present in VW_USERS");
        assertNotNull(users.get(0).getId());
        assertNotNull(users.get(0).getName());
    }

    @Test
    void repositoryCanInsertViaViewAndReadComputedJoinFields() {
        User u = new User();
        u.setName("Maria");
        // In Oracle, the DB trigger sets the date; our H2 trigger sets CURRENT_DATE.
        // Provide a value anyway because the entity has the field; it will be overwritten by trigger.
        u.setBirthday(Date.valueOf(LocalDate.of(2000, 1, 1)));
        u.setLanguage_code("BR");

        userRepository.save(u);

        User latest = userRepository.findTopByOrderByIdDesc();
        assertNotNull(latest, "Expected repository to return last inserted row");
        assertNotNull(latest.getId(), "Expected inserted row to have an id");
        assertEquals("Maria", latest.getName());
        assertEquals("BR", latest.getLanguage_code());
        assertEquals("Português", latest.getLanguage_description(), "Expected view join to resolve language description");
        assertNotNull(latest.getBirthday());
    }
}
