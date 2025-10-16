package com.example.demo.offer.service;

import com.example.demo.offer.dto.OfferCreateRequest;
import com.example.demo.offer.dto.OfferDTO;
import com.example.demo.offer.dto.OfferUpdateRequest;

import java.util.List;

public interface OfferService {
    List<OfferDTO> getAllOffers();
    OfferDTO getOfferById(Long id);
    List<OfferDTO> getOffersByProductId(Long productId);
    List<OfferDTO> getOffersByRetailerId(Long retailerId);
    OfferDTO createOffer(OfferCreateRequest request);
    OfferDTO updateOffer(Long id, OfferUpdateRequest request);
    void deleteOffer(Long id);
}
