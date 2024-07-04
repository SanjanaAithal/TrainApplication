package com.example.trainApplication.controller;

import com.example.trainApplication.model.Ticket;
import com.example.trainApplication.model.User;
import com.example.trainApplication.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @PostMapping("/purchase")
    public Ticket purchaseTicket(@RequestBody User user) {
        return ticketService.purchaseTicket(user);
    }

    @GetMapping("/receipt")
    public Ticket getReceipt(@RequestParam String email) {
        User user = new User();
        user.setEmail(email);
        return ticketService.getTicketByUser(user);
    }

    @GetMapping("/section/{section}")
    public List<User> getUsersBySection(@PathVariable String section) {
        return ticketService.getUserBySection(section);
    }

    @DeleteMapping("/remove")
    public void removeUser(@RequestParam String email) {
        User user = new User();
        user.setEmail(email);
        ticketService.removeUser(user);
    }

    @PutMapping("/modify")
    public void modifyUserSeat(@RequestParam String email, @RequestParam String newSection, @RequestParam int newSeatNumber) {
        User user = new User();
        user.setEmail(email);
        ticketService.modifyUserSeat(user, newSection, newSeatNumber);
    }

}
