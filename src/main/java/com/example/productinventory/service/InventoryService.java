package com.example.productinventory.service;

import com.example.productinventory.dto.DashboardStats;
import com.example.productinventory.dto.ProductRequest;
import com.example.productinventory.dto.StockAdjustmentRequest;
import com.example.productinventory.exception.ResourceNotFoundException;
import com.example.productinventory.model.Category;
import com.example.productinventory.model.InventoryTransaction;
import com.example.productinventory.model.Product;
import com.example.productinventory.repository.CategoryRepository;
import com.example.productinventory.repository.InventoryTransactionRepository;
import com.example.productinventory.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class InventoryService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final InventoryTransactionRepository transactionRepository;

    public InventoryService(ProductRepository productRepository,
                            CategoryRepository categoryRepository,
                            InventoryTransactionRepository transactionRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional(readOnly = true)
    public List<Product> getAllProducts(String search, Long categoryId, String status) {
        if (search != null && !search.trim().isEmpty()) {
            return productRepository.searchProducts(search.trim());
        }
        if (categoryId != null) {
            return productRepository.findByCategoryId(categoryId);
        }
        if (status != null && !status.trim().isEmpty()) {
            return productRepository.findByStatus(status.trim().toUpperCase());
        }
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    public Product createProduct(ProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        Product product = new Product(
                request.getSku(),
                request.getName(),
                request.getDescription(),
                category,
                request.getPrice(),
                request.getQuantity(),
                request.getMinThreshold(),
                request.getLocation()
        );

        Product savedProduct = productRepository.save(product);

        // Record initial stock transaction
        if (savedProduct.getQuantity() > 0) {
            InventoryTransaction tx = new InventoryTransaction(
                    savedProduct,
                    "INITIAL_STOCK",
                    savedProduct.getQuantity(),
                    savedProduct.getQuantity(),
                    "Initial stock upon creation"
            );
            transactionRepository.save(tx);
        }

        return savedProduct;
    }

    public Product updateProduct(Long id, ProductRequest request) {
        Product product = getProductById(id);
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        product.setSku(request.getSku());
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setCategory(category);
        product.setPrice(request.getPrice());
        product.setMinThreshold(request.getMinThreshold());
        product.setLocation(request.getLocation());

        // Note: quantity adjustments are preferred through adjustStock, but if updated directly:
        int diff = request.getQuantity() - product.getQuantity();
        if (diff != 0) {
            product.setQuantity(request.getQuantity());
            InventoryTransaction tx = new InventoryTransaction(
                    product,
                    "MANUAL_EDIT",
                    diff,
                    product.getQuantity(),
                    "Quantity updated via product edit form"
            );
            transactionRepository.save(tx);
        }

        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        productRepository.delete(product);
    }

    public Product adjustStock(Long id, StockAdjustmentRequest request) {
        Product product = getProductById(id);
        int currentQty = product.getQuantity();
        int change = request.getQuantity();
        String type = request.getType().toUpperCase();

        int newQty = currentQty;
        if ("RESTOCK".equals(type) || "INBOUND".equals(type)) {
            newQty = currentQty + Math.abs(change);
        } else if ("DISPATCH".equals(type) || "OUTBOUND".equals(type)) {
            newQty = Math.max(0, currentQty - Math.abs(change));
            change = -Math.abs(change);
        } else { // ADJUSTMENT or manual overwrite
            newQty = Math.max(0, change);
            change = newQty - currentQty;
        }

        product.setQuantity(newQty);
        Product updatedProduct = productRepository.save(product);

        InventoryTransaction tx = new InventoryTransaction(
                updatedProduct,
                type,
                change,
                newQty,
                request.getReason() != null ? request.getReason() : "Stock adjusted to " + newQty
        );
        transactionRepository.save(tx);

        return updatedProduct;
    }

    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Transactional(readOnly = true)
    public List<InventoryTransaction> getRecentTransactions() {
        return transactionRepository.findTop20ByOrderByTimestampDesc();
    }

    @Transactional(readOnly = true)
    public List<InventoryTransaction> getTransactionsForProduct(Long productId) {
        return transactionRepository.findByProductIdOrderByTimestampDesc(productId);
    }

    @Transactional(readOnly = true)
    public DashboardStats getDashboardStats() {
        List<Product> allProducts = productRepository.findAll();
        long totalProducts = allProducts.size();
        long totalQuantity = allProducts.stream().mapToLong(Product::getQuantity).sum();
        double totalValue = allProducts.stream().mapToDouble(p -> p.getPrice() * p.getQuantity()).sum();
        long lowStock = allProducts.stream().filter(p -> "LOW_STOCK".equals(p.getStatus())).count();
        long outOfStock = allProducts.stream().filter(p -> "OUT_OF_STOCK".equals(p.getStatus())).count();
        long categoryCount = categoryRepository.count();

        return new DashboardStats(totalProducts, totalQuantity, totalValue, lowStock, outOfStock, categoryCount);
    }
}
