package ecomers.demo.category.service.impl;

import ecomers.demo.category.dto.CategoryRequest;
import ecomers.demo.category.dto.CategoryResponse;
import ecomers.demo.category.entity.Category;
import ecomers.demo.category.repository.CategoryRepository;
import ecomers.demo.category.service.CategoryService;
import ecomers.demo.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public CategoryResponse create(CategoryRequest request) {
        if(categoryRepository.existsByNameIgnoreCase(request.getName()))
            throw new RuntimeException("A category with that name already exists");

        Category parent = null;
        if(request.getParentId() != null){
            parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(()->new ResourceNotFoundException("Parent category not found"));
        }

        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .parent(parent)
                .build();

        return toResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getById(Long id) {
        return toResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAll() {
        return categoryRepository.findAllWithChildren()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getRootCategories() {
        return categoryRepository.findByParentIsNullAndActiveTrue()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = findById(id);
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        if(request.getImageUrl() != null) category.setImageUrl(request.getImageUrl());
        if(request.getParentId() != null){
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(()->new ResourceNotFoundException("Parent category not found"));
            category.setParent(parent);
        }
        return toResponse(categoryRepository.save(category));
    }

    @Override
    public void delete(Long id) {
        Category category = findById(id);
        category.setActive(false);
        categoryRepository.save(category);
    }

    private Category findById(Long id){
        return categoryRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Category not found"));
    }

    private CategoryResponse toResponse(Category category){
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .imageUrl(category.getImageUrl())
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .parentName(category.getParent() != null ? category.getParent().getName() : null)
                .children(category.getChildren() != null
                        ? category.getChildren().stream()
                          .filter(c->c.getActive())
                          .map(this::toResponse)
                          .collect(Collectors.toList())
                        : null)
                .build();
    }
}
