package com.example.trainApplication.controller;

import com.example.trainApplication.exception.UserNotFoundException;
import com.example.trainApplication.model.Seat;
import com.example.trainApplication.model.Ticket;
import com.example.trainApplication.model.User;
import com.example.trainApplication.service.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TicketControllerTest {

    private User user1;
    private User user2;

    private TicketService ticketService;

    @BeforeEach
    public void setUp() {
        ticketService = new TicketService();
        user1 = new User();
        user1.setFirstName("John");
        user1.setLastName("Doe");
        user1.setEmail("john.doe@example.com");

        user2 = new User();
        user2.setFirstName("Jane");
        user2.setLastName("Smith");
        user2.setEmail("jane.smith@example.com");
    }

    @Test
    public void testPurchaseTicket() {
        Ticket ticket = ticketService.purchaseTicket(user1);
        assertNotNull(ticket);
        assertEquals("London", ticket.getFrom());
        assertEquals("France", ticket.getTo());
        assertEquals(20.0, ticket.getPrice());
        assertEquals(user1, ticket.getUser());
        assertNotNull(ticket.getSeat());
    }

    @Test
    public void testGetTicketByUser() {
        ticketService.purchaseTicket(user1);
        Ticket ticket = ticketService.getTicketByUser(user1);
        assertNotNull(ticket);
        assertEquals(user1, ticket.getUser());
    }

    @Test
    public void testGetUsersBySection() {
        ticketService.purchaseTicket(user1);
        ticketService.purchaseTicket(user2);

        List<User> usersInSectionA = ticketService.getUserBySection("A");
        List<User> usersInSectionB = ticketService.getUserBySection("B");

        assertTrue(usersInSectionA.contains(user1) || usersInSectionA.contains(user2));
        assertTrue(usersInSectionB.contains(user1) || usersInSectionB.contains(user2));
    }

    @Test
    public void testRemoveUser() {
        ticketService.purchaseTicket(user1);
        ticketService.removeUser(user1);

        assertThrows(UserNotFoundException.class, () -> {
            ticketService.removeUser(user1);
        });
    }

    @Test
    public void testModifyUserSeat() {
        ticketService.purchaseTicket(user1);
        ticketService.modifyUserSeat(user1, "B", 100);

        Ticket ticket = ticketService.getTicketByUser(user1);
        assertNotNull(ticket);
        Seat seat = ticket.getSeat();
        assertEquals("B", seat.getSection());
        assertEquals(100, seat.getSeatNumber());
    }

}