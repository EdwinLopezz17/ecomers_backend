package ecomers.demo.product.controller;

import ecomers.demo.common.ApiResponse;
import ecomers.demo.product.dto.CreateProductRequest;
import ecomers.demo.product.dto.ProductResponse;
import ecomers.demo.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> create(
            @Valid @RequestBody CreateProductRequest request,
            @AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.ok(ApiResponse.ok("Product Created",
                productService.create(request, userDetails.getUsername())));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getAll(
            @PageableDefault(size=12) Pageable pageable){
        return ResponseEntity.ok(ApiResponse.ok("All products", productService.getAll(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getById(@PathVariable Long id){
        return ResponseEntity.ok(ApiResponse.ok("Product found",
                productService.getById(id)));
    }

    // GET /api/products/search?q=zapatilla
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> search(
            @RequestParam String q, @PageableDefault(size = 12)Pageable pageable){
        return ResponseEntity.ok(ApiResponse.ok("Results",
                productService.search(q,pageable)));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getByCategory(
            @PathVariable Long categoryId, @PageableDefault(size = 12) Pageable pageable){
        return ResponseEntity.ok(ApiResponse.ok("Products by category",
                productService.getByCategory(categoryId, pageable)));
    }

    @GetMapping("/me")
    public   ResponseEntity<ApiResponse<Page<ProductResponse>>> getMyProducts(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size=12) Pageable pageable){
        return ResponseEntity.ok(ApiResponse.ok("My products",
                productService.getMyProducts(userDetails.getUsername(), pageable)));
    }

    @PostMapping(value = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ProductResponse>> addImages(
            @PathVariable Long id,
            @RequestParam("files") List<MultipartFile> files,
            @AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.ok(ApiResponse.ok("Images",
                productService.addImages(id, files, userDetails.getUsername())));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody CreateProductRequest request,
            @AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.ok(ApiResponse.ok("Product updated",
                productService.update(id, request, userDetails.getUsername())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails){
        productService.delete(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("Product deleted",null));
    }

}
