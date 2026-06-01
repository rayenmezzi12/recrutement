package com.recrutement.inapp.controller;



import com.recrutement.inapp.model.AppNotification;

import com.recrutement.inapp.service.AppNotificationService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;



import java.util.List;

import java.util.Map;



@RestController

@RequestMapping("/api/notifications")

@RequiredArgsConstructor

public class AppNotificationController {



    private final AppNotificationService appNotificationService;



    @GetMapping("/user/{username}")

    public ResponseEntity<List<AppNotification>> forUser(@PathVariable("username") String username) {

        return ResponseEntity.ok(appNotificationService.getForUser(username));

    }



    @GetMapping("/user/{username}/unread-count")

    public ResponseEntity<Map<String, Long>> unreadCount(@PathVariable("username") String username) {

        return ResponseEntity.ok(Map.of("count", appNotificationService.countUnread(username)));

    }



    @PostMapping("/user/{username}/read-all")

    public ResponseEntity<Void> markAllRead(@PathVariable("username") String username) {

        appNotificationService.markAllRead(username);

        return ResponseEntity.noContent().build();

    }

}


