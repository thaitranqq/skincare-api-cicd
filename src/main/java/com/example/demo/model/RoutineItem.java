package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "routine_items")
@Data
public class RoutineItem {
    @EmbeddedId
    private RoutineItemId id;

    @MapsId("routine")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routine_id")
    private Routine routine;

    @MapsId("product")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private Integer step;

    @Column(name = "time_of_day")
    private String timeOfDay;
}
