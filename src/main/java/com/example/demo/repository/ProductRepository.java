package com.example.demo.repository;

import com.example.demo.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Finds products considered "risky", defined as those having at least one feedback
     * and an average rating below 2.0.
     * @return A list of risky products.
     */
    @Query("SELECT p FROM Product p JOIN p.feedbacks f GROUP BY p.id HAVING AVG(f.rating) < 2.0")
    List<Product> findRiskyProducts();
}
