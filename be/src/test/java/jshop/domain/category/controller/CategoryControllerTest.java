package jshop.domain.category.controller;

import static org.junit.jupiter.api.Assertions.*;

import jshop.domain.address.controller.AddressController;
import jshop.domain.address.service.AddressService;
import jshop.domain.category.service.CategoryService;
import jshop.global.controller.GlobalExceptionHandler;
import jshop.utils.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.web.client.MockRestServiceServer.MockRestServiceServerBuilder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


@WebMvcTest(CategoryController.class)
@Import({TestSecurityConfig.class, GlobalExceptionHandler.class})
class CategoryControllerTest {

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Autowired
    private MockMvc mockMvc;

}