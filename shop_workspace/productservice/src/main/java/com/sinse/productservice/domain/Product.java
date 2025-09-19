package com.sinse.productservice.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "product")
public class Product {
    @Id
    /* mybatis의 selectkey 를 jpa로 구현하려면?
       pk property에  GeneratedValue를 명시해놓으면 데이터 insert 시
       동으로 채워넣음
    */
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private int productId;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "brand")
    private String brand;

    @Column(name = "price")
    private int price;

    @Column(name = "discount")
    private int discount;

    @Column(name = "detail")
    private String detail;

    //mybatis의 경우 association 대상임
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subcategory_id")
    private SubCategory subCategory;

    //mybatis 의 경우 collection으로 수집함
    @OneToMany(mappedBy = "product", fetch = FetchType.EAGER)
    private List<ProductFile> productFileList;
}
