package com.example.demo.offer.service.impl;

import com.example.demo.model.Offer;
import com.example.demo.model.Product;
import com.example.demo.model.Retailer;
import com.example.demo.offer.dto.OfferCreateRequest;
import com.example.demo.offer.dto.OfferDTO;
import com.example.demo.offer.dto.OfferUpdateRequest;
import com.example.demo.offer.service.OfferService;
import com.example.demo.product.dto.ProductDTO;
import com.example.demo.repository.OfferRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.RetailerRepository;
import com.example.demo.retailer.dto.RetailerDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OfferServiceImpl implements OfferService {

    private final OfferRepository offerRepository;
    private final ProductRepository productRepository;
    private final RetailerRepository retailerRepository;

    @Override
    public List<OfferDTO> getAllOffers() {
        return offerRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public OfferDTO getOfferById(Long id) {
        Offer offer = offerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offer not found with id: " + id));
        return toDto(offer);
    }

    @Override
    public List<OfferDTO> getOffersByProductId(Long productId) {
        return offerRepository.findByProductId(productId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<OfferDTO> getOffersByRetailerId(Long retailerId) {
        return offerRepository.findByRetailerId(retailerId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public OfferDTO createOffer(OfferCreateRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + request.getProductId()));
        Retailer retailer = retailerRepository.findById(request.getRetailerId())
                .orElseThrow(() -> new RuntimeException("Retailer not found with id: " + request.getRetailerId()));

        Offer offer = new Offer();
        offer.setProduct(product);
        offer.setRetailer(retailer);
        offer.setPrice(request.getPrice());
        offer.setUrl(request.getUrl());
        offer.setUpdatedAt(OffsetDateTime.now());

        Offer savedOffer = offerRepository.save(offer);
        return toDto(savedOffer);
    }

    @Override
    public OfferDTO updateOffer(Long id, OfferUpdateRequest request) {
        Offer offer = offerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offer not found with id: " + id));

        if (request.getPrice() != null) {
            offer.setPrice(request.getPrice());
        }
        if (request.getUrl() != null) {
            offer.setUrl(request.getUrl());
        }
        if (request.getRetailerId() != null) {
            Retailer retailer = retailerRepository.findById(request.getRetailerId())
                    .orElseThrow(() -> new RuntimeException("Retailer not found with id: " + request.getRetailerId()));
            offer.setRetailer(retailer);
        }
        offer.setUpdatedAt(OffsetDateTime.now());

        Offer updatedOffer = offerRepository.save(offer);
        return toDto(updatedOffer);
    }

    @Override
    public void deleteOffer(Long id) {
        if (!offerRepository.existsById(id)) {
            throw new RuntimeException("Offer not found with id: " + id);
        }
        offerRepository.deleteById(id);
    }

    private OfferDTO toDto(Offer offer) {
        OfferDTO dto = new OfferDTO();
        dto.setId(offer.getId());
        dto.setPrice(offer.getPrice());
        dto.setUrl(offer.getUrl());
        dto.setUpdatedAt(offer.getUpdatedAt());
        
        // Map Product and Retailer to their DTOs
        if (offer.getProduct() != null) {
            ProductDTO productDTO = new ProductDTO();
            productDTO.setId(offer.getProduct().getId());
            productDTO.setName(offer.getProduct().getName());
            // ... copy other product fields as needed
            dto.setProduct(productDTO);
        }
        if (offer.getRetailer() != null) {
            RetailerDTO retailerDTO = new RetailerDTO();
            retailerDTO.setId(offer.getRetailer().getId());
            retailerDTO.setName(offer.getRetailer().getName());
            // ... copy other retailer fields as needed
            dto.setRetailer(retailerDTO);
        }
        return dto;
    }
}
