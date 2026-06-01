package com.recrutement.offer.controller;

import com.recrutement.offer.model.Offer;
import com.recrutement.offer.service.OfferService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/offers")
@RequiredArgsConstructor
public class OfferController {

    private final OfferService offerService;

    @GetMapping
    public ResponseEntity<List<Offer>> all() {
        return ResponseEntity.ok(offerService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Offer> one(@PathVariable("id") Long id) {
        return ResponseEntity.ok(offerService.getById(id));
    }

    @PostMapping("/generate")
    public ResponseEntity<Offer> generate(@RequestBody Map<String, Object> body,
                                          @RequestHeader(value = "X-Actor", defaultValue = "system") String actor) {
        Long applicationId = Long.valueOf(body.get("applicationId").toString());
        Double salary = body.get("salary") != null ? Double.valueOf(body.get("salary").toString()) : null;
        LocalDate startDate = body.get("startDate") != null
                ? LocalDate.parse(body.get("startDate").toString()) : null;
        return ResponseEntity.ok(offerService.generate(applicationId, salary, startDate, actor));
    }

    @PostMapping("/{id}/send")
    public ResponseEntity<Offer> send(@PathVariable("id") Long id,
                                    @RequestHeader(value = "X-Actor", defaultValue = "system") String actor) {
        return ResponseEntity.ok(offerService.send(id, actor));
    }

    @PutMapping("/{id}/respond")
    public ResponseEntity<Offer> respond(@PathVariable("id") Long id,
                                         @RequestParam("accepted") boolean accepted,
                                         @RequestHeader(value = "X-Actor", defaultValue = "system") String actor) {
        return ResponseEntity.ok(offerService.respond(id, accepted, actor));
    }
}
