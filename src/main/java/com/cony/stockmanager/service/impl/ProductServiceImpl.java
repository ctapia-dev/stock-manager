package com.cony.stockmanager.service.impl;

import com.cony.stockmanager.dto.request.ProductRequest;
import com.cony.stockmanager.dto.response.ProductResponse;
import com.cony.stockmanager.entity.Category;
import com.cony.stockmanager.entity.Product;
import com.cony.stockmanager.entity.Supplier;
import com.cony.stockmanager.repository.CategoryRepository;
import com.cony.stockmanager.repository.ProductRepository;
import com.cony.stockmanager.repository.SupplierRepository;
import com.cony.stockmanager.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;

    @Override
    @CacheEvict(value = "products", allEntries = true)
    public ProductResponse create(ProductRequest request) {
        if (productRepository.existsBySku(request.getSku())) {
            throw new RuntimeException("Ya existe un producto con ese SKU");
        }
        if (productRepository.existsByName(request.getName())) {
            throw new RuntimeException("Ya existe un producto con ese nombre");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .sku(request.getSku())
                .price(request.getPrice())
                .stock(request.getStock())
                .minStock(request.getMinStock())
                .category(category)
                .supplier(supplier)
                .active(true)
                .build();

        return toResponse(productRepository.save(product));
    }

    @Override
    public ProductResponse getById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        return toResponse(product);
    }

    @Override
    @Cacheable(value = "products", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<ProductResponse> getAll(Pageable pageable) {
        return productRepository.findAllByActiveTrue(pageable)
                .map(this::toResponse);
    }

    @Override
    @CacheEvict(value = "products", allEntries = true)
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setSku(request.getSku());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setMinStock(request.getMinStock());
        product.setCategory(category);
        product.setSupplier(supplier);

        return toResponse(productRepository.save(product));
    }

    @Override
    @CacheEvict(value = "products", allEntries = true)
    public void delete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        product.setActive(false);
        productRepository.save(product);
    }

    private ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .sku(product.getSku())
                .price(product.getPrice())
                .stock(product.getStock())
                .minStock(product.getMinStock())
                .active(product.getActive())
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .supplierName(product.getSupplier() != null ? product.getSupplier().getName() : null)
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}