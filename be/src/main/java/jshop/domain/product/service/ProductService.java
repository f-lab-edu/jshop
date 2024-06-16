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
import jshop.global.common.ErrorCode;
import jshop.global.exception.category.CategoryIdNotFoundException;
import jshop.global.exception.common.EntityNotFoundException;
import jshop.global.exception.user.UserIdNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public void createProduct(CreateProductRequest createProductRequest, Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        User user = optionalUser.orElseThrow(() -> new EntityNotFoundException(
            "ID로 유저를 찾지 못했습니다. : " + userId, ErrorCode.USERID_NOT_FOUND));

        Optional<Category> optionalCategory = categoryRepository.findById(createProductRequest.getCategoryId());
        Category category = optionalCategory.orElseThrow(() -> new EntityNotFoundException(
            "ID로 " + "Cateogry를 찾지 못했습니다. : "
                + createProductRequest.getCategoryId(), ErrorCode.CATEGORYID_NOT_FOUND));

        Product newProduct = Product.ofCreateProductRequest(createProductRequest, category, user);

        productRepository.save(newProduct);
    }

    @Transactional
    public OwnProductsResponse getOwnProducts(Long userId, int pageNumber) {
        Optional<User> optionalUser = userRepository.findById(userId);
        User user = optionalUser.orElseThrow(() -> new EntityNotFoundException(
            "ID로 유저를 찾지 못했습니다. : " + userId, ErrorCode.USERID_NOT_FOUND));

        PageRequest pageRequest = PageRequest.of(pageNumber, 10);
        Page<Product> page = productRepository.findByOwner(user, pageRequest);

        return OwnProductsResponse
            .builder()
            .page(page.getNumber())
            .totalPage(page.getTotalPages())
            .products(page.map(ProductResponse::ofProduct).toList())
            .totalCount(page.getTotalElements())
            .build();
    }

}
