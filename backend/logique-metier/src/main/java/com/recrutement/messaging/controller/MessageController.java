package com.recrutement.messaging.controller;



import com.recrutement.messaging.model.Message;

import com.recrutement.messaging.service.MessageService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;



import java.util.List;

import java.util.Map;



@RestController

@RequestMapping("/api/messages")

@RequiredArgsConstructor

public class MessageController {



    private final MessageService messageService;



    @GetMapping("/conversation")

    public ResponseEntity<List<Message>> conversation(

            @RequestParam("user1") String user1,

            @RequestParam("user2") String user2) {

        return ResponseEntity.ok(messageService.getConversation(user1, user2));

    }



    @PostMapping

    public ResponseEntity<Message> send(@RequestBody Map<String, String> body) {

        return ResponseEntity.ok(messageService.send(

                body.get("senderUsername"),

                body.get("recipientUsername"),

                body.get("content")));

    }



    @GetMapping("/unread-count/{username}")

    public ResponseEntity<Map<String, Long>> unread(@PathVariable("username") String username) {

        return ResponseEntity.ok(Map.of("count", messageService.countUnread(username)));

    }

}


