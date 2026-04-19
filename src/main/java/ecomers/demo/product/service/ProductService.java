package ecomers.demo.product.service;

import ecomers.demo.product.dto.CreateProductRequest;
import ecomers.demo.product.dto.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    ProductResponse create(CreateProductRequest request, String sellerEmail);
    ProductResponse getById(Long id);
    Page<ProductResponse> getAll(Pageable pageable);
    Page<ProductResponse> getByCategory(Long categoryId, Pageable pageable);
    Page<ProductResponse> search(String query, Pageable pageable);
    Page<ProductResponse> getMyProducts(String sellerEmail, Pageable pageable);
    ProductResponse addImages(Long productId, List<MultipartFile> files, String sellerEmail);
    ProductResponse update(Long id, CreateProductRequest request, String sellerEmail);
    void delete(Long id, String sellerEmail);
}
