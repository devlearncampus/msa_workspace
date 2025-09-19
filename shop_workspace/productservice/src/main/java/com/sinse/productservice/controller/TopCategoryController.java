package com.sinse.productservice.controller;

import com.sinse.productservice.domain.TopCategory;
import com.sinse.productservice.model.category.TopCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/productapp")
@RequiredArgsConstructor
public class TopCategoryController {
    private final TopCategoryService topCategoryService;

    @GetMapping("/topcategories")
    public ResponseEntity<?> topcategories() {
        List<TopCategory> topCategoryList=topCategoryService.selectAll();

        return ResponseEntity.ok(Map.of("result", topCategoryList));
    }
}
