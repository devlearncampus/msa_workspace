package com.sinse.productservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;

@Slf4j
//Roy폴딩
@RestController
public class ProductController {

    @GetMapping("/products")
    public ResponseEntity<?> products() {
        return ResponseEntity.ok(
                Map.of("data", List.of("노트북","스마트폰","태블릿","마우스","키보드","스피커"))
        );
    }

    //파일업로드 요청 처리
    public ResponseEntity<?> regist(String id) {
        log.debug("글쓰기 요청 받음");

        return ResponseEntity.ok(Map.of("result", "업로드 성공"));
    }
}















