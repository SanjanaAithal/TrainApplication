package com.example.trainApplication.service;

import com.example.trainApplication.exception.UserAlreadyExistsException;
import com.example.trainApplication.exception.UserNotFoundException;
import com.example.trainApplication.model.Seat;
import com.example.trainApplication.model.Ticket;
import com.example.trainApplication.model.User;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class TicketService {

    private List<User> users = new ArrayList<>();
    private List<Ticket> tickets = new ArrayList<>();
    private List<Seat> seats = new ArrayList<>();
    private AtomicInteger seatIdCounter = new AtomicInteger();

    public Ticket purchaseTicket(User user) {

        Optional<Ticket> existingTicket = tickets.stream()
                .filter(ticket -> ticket.getUser().getEmail().equalsIgnoreCase(user.getEmail()))
                .findFirst();

        if (existingTicket.isPresent()) {
            throw new UserAlreadyExistsException("User with the same email already exists.");
        }

        users.add(user);

        Seat seat = allocateSeat();
        seats.add(seat);

        Ticket ticket = new Ticket();
        ticket.setFrom("London");
        ticket.setTo("France");
        ticket.setUser(user);
        ticket.setSeat(seat);
        ticket.setPrice(20.0);
        tickets.add(ticket);
        return ticket;
    }

    private Seat allocateSeat() {
        Seat seat = new Seat();
        seat.setSection(seatIdCounter.incrementAndGet() % 2 == 0 ? "A" : "B");
        seat.setSeatNumber(seatIdCounter.get());
        return seat;
    }

    public Ticket getTicketByUser(User user) {
        return tickets.stream()
                .filter(ticket -> ticket.getUser().equals(user))
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException("No ticket found for the given email."));
    }

    public List<User> getUserBySection(String section) {
        return tickets.stream().filter(ticket -> ticket.getSeat().getSection().equals(section)).map(Ticket::getUser).toList();
    }

    public void removeUser(User user) {
        Ticket ticket = getTicketByUser(user);
        if (ticket != null) {
            tickets.remove(ticket);
            users.remove(user);
            seats.remove(ticket.getSeat());
        } else {
            throw new UserNotFoundException("No user found with the given email.");
        }
    }

    public void modifyUserSeat(User user, String newSection, int newSeatNumber) {
        Ticket ticket = getTicketByUser(user);
        if (ticket != null) {
            Seat seat = ticket.getSeat();
            seat.setSection(newSection);
            seat.setSeatNumber(newSeatNumber);
        }
    }
}
