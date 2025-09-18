package com.sinse.productservice.model.product;

import com.sinse.productservice.domain.Product;
import com.sinse.productservice.domain.ProductFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class UploadManager {

    @Value("${file.upload-dir}")
    private String uploadDir;

    //서비스가 이 메서드를 파일수만큼 호출할예정
    public ProductFile store(Product product, MultipartFile file) throws IOException {
        //루트 디렉토리가 존재하지 않을 경우 생성
        createDirectory(uploadDir);//루트 디렉토리 생성

        //각 상품에 1:1 대응하는 디렉토리
        Path savePath=createDirectory(uploadDir+"/p"+product.getProductId());

        /*------------------------------------------------------
        파일명 생성
        ------------------------------------------------------*/
        String originalName=file.getOriginalFilename(); //사용자가 원래 부여한 파일명
        String extension=null;//확장자

        //파일이 존재하면서, 파일명에 .이 포한된 유효한 경우
        if( originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf("."));
        }
        String newFilename= UUID.randomUUID().toString()+extension;
        log.debug("개발자가 정의한 새로운 파일명은 {}", newFilename);

        /*------------------------------------------------------
        파일저장
        ------------------------------------------------------*/
        Path targetlocation=savePath.resolve(newFilename); //OS에 적절한 경로로 변경
        Files.copy(file.getInputStream(), targetlocation, StandardCopyOption.REPLACE_EXISTING);

        //저장정보를 담을 ProductFile 을 반환
        ProductFile productFile=new ProductFile();
        productFile.setProduct(product);
        productFile.setFileName(newFilename);//새롭게 생성된 파일명 UUID...
        productFile.setOriginalName(originalName); //유저의 원래 파일명..
        productFile.setContentType(file.getContentType()); // image/jpg
        productFile.setFilePath(targetlocation.toString()); //풀경로
        productFile.setFileSize(file.getSize()); //파일용량

        return productFile;
    }

    //디렉토리 생성 메서드 정의(루트, 상품마다 필요한 디렉토리..)
    //createDirectory("c:/upload"), createDirectory("p23")
    public Path createDirectory(String path) throws IOException {
        //기존 java.io.File과 동일한 업무를 수행하지만, OS특화된 처리에서 유용함
        Path dir= Paths.get(path);
        Path savePath = Paths.get(path).toAbsolutePath().normalize();

        if(!(Files.exists(dir) && Files.isDirectory(dir)) ) { //디렉토리가 존재하지 않을 경우,  이미 C:/upload가 잇다면 생성안함
            Files.createDirectories(savePath);//디렉토리 생성
        }else{
            log.debug("{} 디렉토리가 이미 존재함", path);
        }
        return savePath;
    }
}










