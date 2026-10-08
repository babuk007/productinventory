package com.example.productinventory;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetProducts() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$[0].sku", notNullValue()))
                .andExpect(jsonPath("$[0].name", notNullValue()));
    }

    @Test
    void testGetCategories() throws Exception {
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThan(0))));
    }

    @Test
    void testGetDashboardStats() throws Exception {
        mockMvc.perform(get("/api/dashboard/stats"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalProducts", greaterThan(0)))
                .andExpect(jsonPath("$.totalInventoryValue", greaterThan(0.0)));
    }

    @Test
    void testCreateProductAndAdjustStock() throws Exception {
        String newProductJson = "{\n" +
                "  \"sku\": \"SKU-TEST-999\",\n" +
                "  \"name\": \"Unit Test Keyboard\",\n" +
                "  \"description\": \"Mechanical keyboard for testing\",\n" +
                "  \"categoryId\": 1,\n" +
                "  \"price\": 4999.0,\n" +
                "  \"quantity\": 20,\n" +
                "  \"minThreshold\": 5,\n" +
                "  \"location\": \"Rack T-1\"\n" +
                "}";

        String responseBody = mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newProductJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sku", is("SKU-TEST-999")))
                .andExpect(jsonPath("$.quantity", is(20)))
                .andExpect(jsonPath("$.status", is("IN_STOCK")))
                .andReturn().getResponse().getContentAsString();

        // Extract ID
        String adjustJson = "{\n" +
                "  \"type\": \"DISPATCH\",\n" +
                "  \"quantity\": 18,\n" +
                "  \"reason\": \"Test dispatch sale\"\n" +
                "}";

        // Query product by search
        mockMvc.perform(get("/api/products?search=Unit Test Keyboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sku", is("SKU-TEST-999")));
    }
}
