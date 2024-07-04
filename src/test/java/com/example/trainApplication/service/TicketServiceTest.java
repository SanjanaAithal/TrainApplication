package com.example.trainApplication.service;

import com.example.trainApplication.exception.UserAlreadyExistsException;
import com.example.trainApplication.exception.UserNotFoundException;
import com.example.trainApplication.model.Ticket;
import com.example.trainApplication.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TicketServiceTest {

    private TicketService ticketService;

    @BeforeEach
    public void setUp() {
        ticketService = new TicketService();
    }

    @Test
    public void testPurchaseTicket() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");

        Ticket ticket = ticketService.purchaseTicket(user);

        assertNotNull(ticket);
        assertEquals("John", ticket.getUser().getFirstName());
        assertEquals("Doe", ticket.getUser().getLastName());
        assertEquals("john.doe@example.com", ticket.getUser().getEmail());
        assertEquals(20, ticket.getPrice());
    }

    @Test
    public void testPurchaseTicket_UserAlreadyExists() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");

        ticketService.purchaseTicket(user);

        User duplicateUser = new User();
        duplicateUser.setFirstName("John");
        duplicateUser.setLastName("Doe");
        duplicateUser.setEmail("john.doe@example.com");

        assertThrows(UserAlreadyExistsException.class, () -> ticketService.purchaseTicket(duplicateUser));
    }

    @Test
    public void testGetTicketByUserEmail() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");

        ticketService.purchaseTicket(user);

        Ticket ticket = ticketService.getTicketByUser(user);

        assertNotNull(ticket);
        assertEquals("john.doe@example.com", ticket.getUser().getEmail());
    }

    @Test
    public void testGetTicketByUserEmail_UserNotFound() {
        User user = new User();
        user.setEmail("non.existing@example.com");
        assertThrows(UserNotFoundException.class, () -> ticketService.getTicketByUser(user));
    }

    @Test
    public void testGetUsersBySection() {
        User user1 = new User();
        user1.setFirstName("John");
        user1.setLastName("Doe");
        user1.setEmail("john.doe@example.com");

        User user2 = new User();
        user2.setFirstName("Jane");
        user2.setLastName("Doe");
        user2.setEmail("jane.doe@example.com");

        ticketService.purchaseTicket(user1);
        ticketService.purchaseTicket(user2);

        List<User> sectionAUsers = ticketService.getUserBySection("A");
        List<User> sectionBUsers = ticketService.getUserBySection("B");

        assertEquals(1, sectionAUsers.size());
        assertEquals(1, sectionBUsers.size());
    }

    @Test
    public void testRemoveUser() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");

        ticketService.purchaseTicket(user);

        ticketService.removeUser(user);

        assertThrows(UserNotFoundException.class, () -> ticketService.getTicketByUser(user));
    }

    @Test
    public void testRemoveUser_UserNotFound() {
        User user = new User();
        user.setEmail("non.existing@example.com");
        assertThrows(UserNotFoundException.class, () -> ticketService.removeUser(user));
    }

    @Test
    public void testModifyUserSeat() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");

        ticketService.purchaseTicket(user);

        ticketService.modifyUserSeat(user, "B", 100);

        Ticket ticket = ticketService.getTicketByUser(user);

        assertEquals("B", ticket.getSeat().getSection());
        assertEquals(100, ticket.getSeat().getSeatNumber());
    }

}