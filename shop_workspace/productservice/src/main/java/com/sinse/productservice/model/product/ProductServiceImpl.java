package com.sinse.productservice.model.product;

import com.sinse.productservice.domain.Product;
import com.sinse.productservice.domain.ProductFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final JpaProductRepository jpaProductRepository;//상품등록에 필요한 Jpa
    private final UploadManager uploadManager; //이미지 저장에 필요한 UploadManager
    private final JpaProductFileRepository jpaProductFileRepository;//상품이미지 등록에 필요한  Jpa

    @Override
    public void save(Product product, List<MultipartFile> files) {
        /*---------------------------------------
        상품 등록 (상품을 등록해야 pk가 반환되므로)
        pk가 잇어야 디렉토리 생성, product_file에 사용
       ---------------------------------------*/
       Product savedProduct=jpaProductRepository.save(product);
       int product_id = savedProduct.getProductId();
       log.debug("상품 insert 후 반환된 product_id={}", product_id);

        /*---------------------------------------
        이미지 수만큼 UploadManager 의 store()를 호출하자!
        단, 트랜잭션의 대상은 파일이 아니라 DB만 가능하므로, 파일 저장 시 하나라도 실패하면
        비록 트랜잭션 대상은 아니지만, 처음부터 파일이 없었던 것으로 돌려놓아야 하므로, 디렉토리 자체를 날리자
       ---------------------------------------*/
       //이미지 수만큼 반복문을 돌리되, 전통적인 반복문이 아닌 선언적 프로그래밍 방식으로 표현해본다
        List<ProductFile> productFileList=files.stream()
                .map( file ->{
                    try {
                        return uploadManager.store(product, file);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).toList();

        productFileList.forEach(productFile->{
            jpaProductFileRepository.save(productFile);
        });
    }

    @Override
    public List<Product> selectAll() {
        return jpaProductRepository.findAll();
    }

    @Override
    public Product select(int productId) {
        return jpaProductRepository.findById(productId).orElse(null);
    }

    @Override
    public void update(Product product, List<MultipartFile> files) {

    }

    @Override
    public void delete(int productId) {

    }
}





