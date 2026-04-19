package ecomers.demo.category.controller;

import ecomers.demo.category.dto.CategoryRequest;
import ecomers.demo.category.dto.CategoryResponse;
import ecomers.demo.category.service.CategoryService;
import ecomers.demo.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> create(
            @Valid @RequestBody CategoryRequest request){
        return ResponseEntity.ok(ApiResponse.ok("Category created", categoryService.create(request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAll(){
        return ResponseEntity.ok(ApiResponse.ok("All categories",
                categoryService.getAll()));
    }

    @GetMapping("/roots")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getRoots(){
        return ResponseEntity.ok(ApiResponse.ok("Root categories", categoryService.getRootCategories()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getById(@PathVariable Long id){
        return ResponseEntity.ok(ApiResponse.ok("Category found", categoryService.getById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> update(
            @PathVariable Long id, @Valid @RequestBody CategoryRequest request){
        return ResponseEntity.ok(ApiResponse.ok("Update Category", categoryService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id){
        categoryService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Deleted category",null));
    }
}
