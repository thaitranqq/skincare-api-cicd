package com.example.demo.offer.controller;

import com.example.demo.offer.dto.OfferCreateRequest;
import com.example.demo.offer.dto.OfferDTO;
import com.example.demo.offer.dto.OfferUpdateRequest;
import com.example.demo.offer.service.OfferService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/offers")
@RequiredArgsConstructor
public class OfferController {

    private final OfferService offerService;

    @GetMapping
    public ResponseEntity<List<OfferDTO>> getAllOffers() {
        List<OfferDTO> offers = offerService.getAllOffers();
        return ResponseEntity.ok(offers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OfferDTO> getOfferById(@PathVariable Long id) {
        OfferDTO offer = offerService.getOfferById(id);
        return ResponseEntity.ok(offer);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<OfferDTO>> getOffersByProductId(@PathVariable Long productId) {
        List<OfferDTO> offers = offerService.getOffersByProductId(productId);
        return ResponseEntity.ok(offers);
    }

    @GetMapping("/retailer/{retailerId}")
    public ResponseEntity<List<OfferDTO>> getOffersByRetailerId(@PathVariable Long retailerId) {
        List<OfferDTO> offers = offerService.getOffersByRetailerId(retailerId);
        return ResponseEntity.ok(offers);
    }

    @PostMapping
    public ResponseEntity<OfferDTO> createOffer(@RequestBody OfferCreateRequest request) {
        OfferDTO createdOffer = offerService.createOffer(request);
        return new ResponseEntity<>(createdOffer, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OfferDTO> updateOffer(@PathVariable Long id, @RequestBody OfferUpdateRequest request) {
        OfferDTO updatedOffer = offerService.updateOffer(id, request);
        return ResponseEntity.ok(updatedOffer);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOffer(@PathVariable Long id) {
        offerService.deleteOffer(id);
        return ResponseEntity.noContent().build();
    }
}
