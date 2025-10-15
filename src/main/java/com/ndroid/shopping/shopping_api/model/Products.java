package com.ndroid.shopping.shopping_api.model;

import javax.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "products")
public class Products {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Integer productId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    private Integer quantity = 0;

    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "seller_id", referencedColumnName = "seller_id",
    //             foreignKey = @ForeignKey(name = "fk_seller_product"))
    // private Seller seller;

    @Column(name = "seller_id")
    private Integer sellerId;

    private String category;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(precision = 2, scale = 1)
    private BigDecimal ratings = BigDecimal.ZERO;

    @Lob
    private byte[] image;

    @Column(name = "created_at", updatable = false, insertable = false)
    private LocalDateTime createdAt;

    // Getters and Setters (or use Lombok for brevity)

    // Constructors, toString(), equals(), hashCode() as needed
}
