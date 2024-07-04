package com.example.trainApplication.controller;

import com.example.trainApplication.exception.UserAlreadyExistsException;
import com.example.trainApplication.exception.UserNotFoundException;
import com.example.trainApplication.model.Ticket;
import com.example.trainApplication.model.User;
import com.example.trainApplication.service.TicketService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class TicketControllerIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TicketService ticketService;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void testPurchaseTicket() throws Exception {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");

        when(ticketService.purchaseTicket(any(User.class))).thenReturn(new Ticket());

        mockMvc.perform(post("/api/tickets/purchase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());

        // Test duplicate email
        doThrow(new UserAlreadyExistsException("User with the same email already exists."))
                .when(ticketService).purchaseTicket(any(User.class));

        mockMvc.perform(post("/api/tickets/purchase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("User with the same email already exists.")));
    }

    @Test
    public void testGetReceipt() throws Exception {
        when(ticketService.getTicketByUser(any(User.class))).thenReturn(new Ticket());

        mockMvc.perform(get("/api/tickets/receipt")
                        .param("email", "john.doe@example.com"))
                .andExpect(status().isOk());

        doThrow(new UserNotFoundException("No ticket found for the given email."))
                .when(ticketService).getTicketByUser(any(User.class));

        mockMvc.perform(get("/api/tickets/receipt")
                        .param("email", "john.doe@example.com"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("No ticket found for the given email.")));
    }

    @Test
    public void testGetUsersBySection() throws Exception {
        when(ticketService.getUserBySection(eq("A"))).thenReturn(Collections.singletonList(new User()));

        mockMvc.perform(get("/api/tickets/section/A"))
                .andExpect(status().isOk());

        when(ticketService.getUserBySection(eq("B"))).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/tickets/section/B"))
                .andExpect(status().isOk());
    }

    @Test
    public void testRemoveUser() throws Exception {
        doNothing().when(ticketService).removeUser(any(User.class));

        mockMvc.perform(delete("/api/tickets/remove")
                        .param("email", "john.doe@example.com"))
                .andExpect(status().isOk());

        doThrow(new UserNotFoundException("No user found with the given email."))
                .when(ticketService).removeUser(any(User.class));

        mockMvc.perform(delete("/api/tickets/remove")
                        .param("email", "non.existing@example.com"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("No user found with the given email.")));
    }

    @Test
    public void testModifyUserSeat() throws Exception {
        doNothing().when(ticketService).modifyUserSeat(any(User.class), eq("B"), eq(100));

        mockMvc.perform(put("/api/tickets/modify")
                        .param("email", "john.doe@example.com")
                        .param("newSection", "B")
                        .param("newSeatNumber", "100"))
                .andExpect(status().isOk());

        doThrow(new UserNotFoundException("No ticket found for the given email."))
                .when(ticketService).modifyUserSeat(any(User.class), eq("B"), eq(100));

        mockMvc.perform(put("/api/tickets/modify")
                        .param("email", "non.existing@example.com")
                        .param("newSection", "B")
                        .param("newSeatNumber", "100"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("No ticket found for the given email.")));
    }
}
