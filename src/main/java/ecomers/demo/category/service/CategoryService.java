package ecomers.demo.category.service;

import ecomers.demo.category.dto.CategoryRequest;
import ecomers.demo.category.dto.CategoryResponse;

import java.util.List;

public interface CategoryService {
    CategoryResponse create(CategoryRequest request);
    CategoryResponse getById(Long id);
    List<CategoryResponse> getAll();
    List<CategoryResponse> getRootCategories();
    CategoryResponse update(Long id, CategoryRequest request);
    void delete(Long id);
}
