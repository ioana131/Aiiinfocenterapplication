package org.example.aiinfocenter;

import org.example.aiinfocenter.model.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ModelAllConstructorTests {

    // =========================
    // user
    // =========================

    @Test
    void user_noargs_constructor() {
        User u = new User();
        assertNotNull(u); // doar verific ca nu crapa
        // la no-args, in mod normal campurile sunt null
        assertNull(u.getId());
        assertNull(u.getName());
        assertNull(u.getEmail());
        assertNull(u.getPassword());
        assertNull(u.getRole());
    }

    @Test
    void user_args_constructor() {
        User u = new User("Ana", "ana@mail.com", "1234", UserRole.STUDENT);

        assertEquals("Ana", u.getName());
        assertEquals("ana@mail.com", u.getEmail());
        assertEquals("1234", u.getPassword());
        assertEquals(UserRole.STUDENT, u.getRole());
    }

    // =========================
    // studentprofile
    // =========================

    @Test
    void studentProfile_noargs_constructor() {
        StudentProfile sp = new StudentProfile();
        assertNotNull(sp);
        // no-args -> default, nimic setat
        assertNull(sp.getUser());
        assertNull(sp.getFaculty());
        // yearOfStudy e int -> default 0
        assertEquals(0, sp.getYearOfStudy());
        // userId poate fi null
        assertNull(sp.getUserId());
    }

    @Test
    void studentProfile_args_constructor() {
        User u = new User("Ana", "ana@mail.com", "1234", UserRole.STUDENT);
        StudentProfile sp = new StudentProfile(u, "CS", 2);

        assertEquals(u, sp.getUser());
        assertEquals("CS", sp.getFaculty());
        assertEquals(2, sp.getYearOfStudy());
        assertNull(sp.getUserId());
    }

    // =========================
    // request
    // =========================

    @Test
    void request_noargs_constructor() {
        Request r = new Request();
        assertNotNull(r);

        // default-uri la no-args
        assertNull(r.getId());
        assertNull(r.getStudent());
        assertNull(r.getType());
        assertNull(r.getMessage());

        // status are default OPEN in entity
        assertEquals(Request.Status.OPEN, r.getStatus());

        // createdAt e setat instant (initializare camp)
        assertNotNull(r.getCreatedAt());
    }

    @Test
    void request_args_constructor() {
        User u = new User("Ana", "ana@mail.com", "1234", UserRole.STUDENT);
        Request r = new Request(u, "FOAIE_MATRICOLA", "vreau foaia matricola");

        assertEquals(u, r.getStudent());
        assertEquals("FOAIE_MATRICOLA", r.getType());
        assertEquals("vreau foaia matricola", r.getMessage());

        // default status OPEN
        assertEquals(Request.Status.OPEN, r.getStatus());
        assertNotNull(r.getCreatedAt());
    }

    // =========================
    // conversationthread
    // =========================

    @Test
    void conversationThread_noargs_constructor() {
        ConversationThread t = new ConversationThread();
        assertNotNull(t);

        assertNull(t.getId());
        assertNull(t.getStudent());
        assertNull(t.getTitle());

        // createdAt e setat instant
        assertNotNull(t.getCreatedAt());
    }

    @Test
    void conversationThread_args_constructor() {
        User u = new User("Ana", "ana@mail.com", "1234", UserRole.STUDENT);
        ConversationThread t = new ConversationThread(u, "title");

        assertEquals(u, t.getStudent());
        assertEquals("title", t.getTitle());
        assertNotNull(t.getCreatedAt());
    }

    // =========================
    // chatmessage
    // =========================

    @Test
    void chatMessage_noargs_constructor() {
        ChatMessage m = new ChatMessage();
        assertNotNull(m);

        assertNull(m.getId());
        assertNull(m.getThread());
        assertNull(m.getSender());
        assertNull(m.getText());

        // createdAt e setat instant
        assertNotNull(m.getCreatedAt());
    }

    @Test
    void chatMessage_args_constructor() {
        User u = new User("Ana", "ana@mail.com", "1234", UserRole.STUDENT);
        ConversationThread t = new ConversationThread(u, "t");
        ChatMessage m = new ChatMessage(t, ChatMessage.Sender.AI, "hello");

        assertEquals(t, m.getThread());
        assertEquals(ChatMessage.Sender.AI, m.getSender());
        assertEquals("hello", m.getText());
        assertNotNull(m.getCreatedAt());
    }
}
