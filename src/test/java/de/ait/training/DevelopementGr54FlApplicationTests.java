package de.ait.training;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.Matchers.hasSize;

import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(value = "classpath:sql/clear.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "classpath:sql/seed_cars.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "classpath:sql/clear.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class DevelopementGr54FlApplicationTests {
    @Autowired
    private MockMvc mockMvc;
    @Test
    void testGetAllCars() throws Exception {
        mockMvc.perform(get("/api/cars"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThan(0))))
            ;

        mockMvc.perform(get("/api/cars"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", hasSize(0)))
        ;
    }

    @Test
    void testGetCarsByColor() throws Exception {

        mockMvc.perform(get("/api/cars/color/red"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$[*].color").value("red"));

        mockMvc.perform(get("/api/cars/color/purple"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.length()").isEmpty());
    }

    @Test
    void testGetCarsByPriceBetween() throws Exception {
        // Test case 1: Valid price range

        mockMvc.perform(get("/api/cars/price/between/10000/30000"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThan(0))));

        // Test case 2: No cars in price range
        mockMvc.perform(get("/api/cars/price/between/100/500"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").isEmpty());

        // Test case 3: Invalid price range (min > max)
        mockMvc.perform(get("/api/cars/price/between/30000/10000"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Invalid price range. Min should be less than or equal to Max."));
    }

    @Test
    void testGetCarsByPriceUnder() throws Exception {

        mockMvc.perform(get("/api/cars/price/under/20000"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", hasSize(greaterThan(0))));

        mockMvc.perform(get("/api/cars/price/under/1000"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").isEmpty());
    }

    @Test
    void testGetCarsByPriceOver() throws Exception {

        mockMvc.perform(get("/api/cars/price/over/25000"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", hasSize(greaterThan(0))));

        mockMvc.perform(get("/api/cars/price/over/1000000"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").isEmpty());
    }
}

