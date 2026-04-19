package ecomers.demo.product.service.impl;

import ecomers.demo.category.entity.Category;
import ecomers.demo.category.repository.CategoryRepository;
import ecomers.demo.exception.ResourceNotFoundException;
import ecomers.demo.product.dto.CreateProductRequest;
import ecomers.demo.product.dto.ProductResponse;
import ecomers.demo.product.entity.Product;
import ecomers.demo.product.repository.ProductRepository;
import ecomers.demo.product.service.ProductService;
import ecomers.demo.storage.StorageService;
import ecomers.demo.user.entity.User;
import ecomers.demo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final StorageService storageService;

    @Override
    @Transactional
    public ProductResponse create(CreateProductRequest request, String sellerEmail) {
        User seller = findUser(sellerEmail);
        Category category = findCategory(request.getCategoryId());

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .category(category)
                .seller(seller)
                .images(new ArrayList<>())
                .build();

        return toResponse(productRepository.save(product));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getById(Long id) {
        return toResponse(findActive(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getAll(Pageable pageable) {
        return productRepository.findByActiveTrue(pageable).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getByCategory(Long categoryId, Pageable pageable) {
        return productRepository.findByActiveTrueAndCategoryId(categoryId, pageable).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> search(String query, Pageable pageable) {
        return productRepository.search(query, pageable).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getMyProducts(String sellerEmail, Pageable pageable) {
        User seller = findUser(sellerEmail);
        return productRepository.findBySellerIdAndActiveTrue(seller.getId(), pageable).map(this::toResponse);
    }

    @Override
    @Transactional
    public ProductResponse addImages(Long productId, List<MultipartFile> files, String sellerEmail) {
        Product product = findActive(productId);
        validateOwner(product, sellerEmail);

        List<String> saved = files.stream()
                .map(storageService::save)
                .collect(Collectors.toList());

        product.getImages().addAll(saved);
        return toResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public ProductResponse update(Long id, CreateProductRequest request, String sellerEmail) {
        Product product = findActive(id);
        validateOwner(product, sellerEmail);

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setCategory(findCategory(request.getCategoryId()));

        return toResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public void delete(Long id, String sellerEmail) {
        Product product = findActive(id);
        validateOwner(product, sellerEmail);
        product.setActive(false);
        productRepository.save(product);
    }

    private Product findActive(Long id){
        return productRepository.findById(id)
                .filter(Product::getActive)
                .orElseThrow(()-> new ResourceNotFoundException("Product not found"));
    }

    private User findUser(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(()->new ResourceNotFoundException("User not found"));
    }

    private Category findCategory(Long id){
        return categoryRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Category not found"));
    }

    private void validateOwner(Product product, String email){
        if(!product.getSeller().getEmail().equals(email))
            throw new RuntimeException("You do not have permission to modify this product");
    }

    private ProductResponse toResponse(Product product){
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .images(product.getImages())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .sellerId(product.getSeller().getId())
                .sellerName(product.getSeller().getName()+" "+product.getSeller().getLastName())
                .build();
    }
}
