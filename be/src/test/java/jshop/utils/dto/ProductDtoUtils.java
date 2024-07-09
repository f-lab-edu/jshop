package jshop.utils.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jshop.domain.product.dto.CreateProductDetailRequest;
import jshop.domain.product.dto.CreateProductRequest;

public class ProductDtoUtils {

    public static CreateProductRequest getCreateProductRequest(Long categoryId) {
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("attr1", List.of("a", "b", "c"));
        return CreateProductRequest
            .builder()
            .name("product")
            .categoryId(categoryId)
            .manufacturer("manufacturer")
            .description("description")
            .attributes(attributes)
            .build();

    }

    public static List<CreateProductDetailRequest> getCreateProductDetailRequest() {
        List<CreateProductDetailRequest> list = new ArrayList<>();
        String[] props = {"a", "b", "c"};

        for (String prop : props) {
            Map<String, String> attribute = new HashMap<>();
            attribute.put("attr1", prop);
            list.add(CreateProductDetailRequest
                .builder().price(1000L).attribute(attribute).build());
        }
        return list;
    }
}
