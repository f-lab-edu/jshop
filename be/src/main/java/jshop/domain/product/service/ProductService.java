package jshop.domain.product.service;

import java.util.Optional;
import jshop.domain.category.entity.Category;
import jshop.domain.category.repository.CategoryRepository;
import jshop.domain.product.dto.CreateProductRequest;
import jshop.domain.product.dto.OwnProductsResponse;
import jshop.domain.product.dto.ProductResponse;
import jshop.domain.product.entity.Product;
import jshop.domain.product.repository.ProductRepository;
import jshop.domain.user.entity.User;
import jshop.domain.user.repository.UserRepository;
import jshop.global.exception.category.CategoryIdNotFoundException;
import jshop.global.exception.user.UserIdNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public void createProduct(CreateProductRequest createProductRequest, Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        User user = optionalUser.orElseThrow(UserIdNotFoundException::new);

        Optional<Category> optionalCategory = categoryRepository.findById(createProductRequest.getCategoryId());
        Category category = optionalCategory.orElseThrow(CategoryIdNotFoundException::new);

        Product newProduct = Product.ofCreateProductRequest(createProductRequest, category, user);

        productRepository.save(newProduct);
    }

    public OwnProductsResponse getOwnProducts(Long userId, int pageNumber) {
        Optional<User> optionalUser = userRepository.findById(userId);
        User user = optionalUser.orElseThrow(UserIdNotFoundException::new);

        PageRequest pageRequest = PageRequest.of(pageNumber, 10);
        Page<Product> page = productRepository.findByOwner(user, pageRequest);

        return OwnProductsResponse
            .builder()
            .page(page.getNumber())
            .totalPage(page.getTotalPages())
            .products(page.getContent().stream().map(ProductResponse::ofProduct).toList())
            .totalCount(page.getTotalElements())
            .build();
    }

}
