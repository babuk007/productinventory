package com.example.productinventory.config;

import com.example.productinventory.model.Category;
import com.example.productinventory.model.InventoryTransaction;
import com.example.productinventory.model.Product;
import com.example.productinventory.repository.CategoryRepository;
import com.example.productinventory.repository.InventoryTransactionRepository;
import com.example.productinventory.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final InventoryTransactionRepository transactionRepository;

    public DataInitializer(CategoryRepository categoryRepository,
                           ProductRepository productRepository,
                           InventoryTransactionRepository transactionRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public void run(String... args) {
        if (categoryRepository.count() > 0) {
            return;
        }

        // 1. Seed Categories
        Category catElectronics = new Category("CAT-ELEC", "Electronics", "Computing, audio, and personal devices");
        Category catApparel = new Category("CAT-APPR", "Apparel & Fashion", "Clothing, footwear, and accessories");
        Category catFurniture = new Category("CAT-FURN", "Office & Home Furniture", "Desks, ergonomic chairs, and organizers");
        Category catKitchen = new Category("CAT-KTCH", "Home & Kitchen", "Appliances, cookware, and kitchen essentials");
        Category catIndustrial = new Category("CAT-IND", "Industrial & Tools", "Hardware, power tools, and maintenance gear");

        categoryRepository.saveAll(Arrays.asList(catElectronics, catApparel, catFurniture, catKitchen, catIndustrial));

        // 2. Seed Realistic Products
        List<Product> products = Arrays.asList(
                new Product("SKU-ELC-001", "Apple iPhone 15 Pro", "128GB Titanium, A17 Pro chip", catElectronics, 79999.00, 25, 10, "Aisle 3 - Shelf A"),
                new Product("SKU-ELC-002", "Sony WH-1000XM5 Wireless Headphones", "Industry leading noise canceling headphones", catElectronics, 29999.00, 42, 15, "Aisle 3 - Shelf B"),
                new Product("SKU-ELC-003", "Dell UltraSharp 27\" 4K USB-C Monitor", "IPS Black technology with 4K resolution", catElectronics, 34999.00, 8, 10, "Aisle 4 - Shelf C"), // LOW STOCK
                new Product("SKU-ELC-004", "Logitech MX Master 3S Wireless Mouse", "Quiet clicks and 8K DPI sensor", catElectronics, 9499.00, 55, 15, "Aisle 3 - Shelf D"),
                new Product("SKU-ELC-005", "Apple MacBook Air 15\" M3", "16GB Unified Memory, 512GB SSD", catElectronics, 124999.00, 0, 5, "Aisle 4 - Shelf A"), // OUT OF STOCK

                new Product("SKU-APP-001", "Nike Air Max 270 Running Shoes", "Breathable mesh upper with foam cushioning", catApparel, 8999.00, 18, 10, "Aisle 1 - Bin 12"),
                new Product("SKU-APP-002", "Levi's 511 Slim Fit Stretch Jeans", "Classic modern slim-cut denim pants", catApparel, 3299.00, 60, 20, "Aisle 1 - Bin 15"),
                new Product("SKU-APP-003", "Patagonia Torrentshell 3L Rain Jacket", "H2No Performance Standard waterproof shell", catApparel, 14999.00, 4, 8, "Aisle 2 - Bin 04"), // LOW STOCK

                new Product("SKU-FUR-001", "Herman Miller Aeron Ergonomic Chair", "Size B fully adjustable lumbar support", catFurniture, 85000.00, 12, 5, "Warehouse Bay 1"),
                new Product("SKU-FUR-002", "Autonomous SmartDesk Pro Motorized", "Dual motor electric standing desk frame", catFurniture, 38999.00, 7, 5, "Warehouse Bay 2"),
                new Product("SKU-FUR-003", "Minimalist Solid Oak Coffee Table", "Hand-finished Scandinavian style coffee table", catFurniture, 18500.00, 0, 3, "Warehouse Bay 3"), // OUT OF STOCK

                new Product("SKU-KTC-001", "Instant Pot Duo 7-in-1 Electric Pressure Cooker", "6 Quart multi-use pressure cooker", catKitchen, 8499.00, 34, 15, "Aisle 6 - Shelf B"),
                new Product("SKU-KTC-002", "Breville Barista Touch Espresso Machine", "Automated touch screen espresso maker", catKitchen, 68000.00, 5, 5, "Aisle 6 - Shelf A"), // LOW STOCK
                new Product("SKU-KTC-003", "Hydro Flask 32 oz Wide Mouth Bottle", "TempShield double-wall vacuum insulation", catKitchen, 2499.00, 120, 25, "Aisle 5 - Shelf C"),

                new Product("SKU-IND-001", "DeWalt 20V MAX Cordless Drill Kit", "Compact brushless drill/driver with 2 batteries", catIndustrial, 12999.00, 22, 10, "Aisle 8 - Shelf E"),
                new Product("SKU-IND-002", "Fluke 117 True RMS Multimeter", "Electricians multimeter with non-contact voltage", catIndustrial, 16500.00, 3, 5, "Aisle 8 - Shelf B") // LOW STOCK
        );

        productRepository.saveAll(products);

        // 3. Seed Initial Inventory Transactions
        for (Product product : products) {
            String type = product.getQuantity() > 0 ? "RESTOCK" : "INITIAL_STOCK";
            String note = product.getQuantity() > 0
                    ? "Initial batch shipment received from supplier"
                    : "Product registered; awaiting replenishment PO";

            InventoryTransaction tx = new InventoryTransaction(
                    product,
                    type,
                    product.getQuantity(),
                    product.getQuantity(),
                    note
            );
            transactionRepository.save(tx);
        }

        // Add a few realistic recent transactions (e.g. recent sales and restocks)
        transactionRepository.save(new InventoryTransaction(
                products.get(0), "DISPATCH", -5, 25, "B2B Order #SO-9821 fulfilled"
        ));
        transactionRepository.save(new InventoryTransaction(
                products.get(1), "RESTOCK", 15, 42, "Vendor PO #PO-4410 received"
        ));
        transactionRepository.save(new InventoryTransaction(
                products.get(4), "DISPATCH", -3, 0, "Emergency order dispatch to retail branch"
        ));
    }
}
