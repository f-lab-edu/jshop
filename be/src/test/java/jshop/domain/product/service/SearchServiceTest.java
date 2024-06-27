package jshop.domain.product.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import jshop.domain.category.repository.CategoryRepository;
import jshop.domain.inventory.service.InventoryService;
import jshop.domain.product.dto.ProductDetailResponse;
import jshop.domain.product.dto.SearchProductDetailQueryResult;
import jshop.domain.product.dto.SearchProductDetailsResponse;
import jshop.domain.product.entity.Product;
import jshop.domain.product.entity.ProductDetail;
import jshop.domain.product.repository.ProductDetailRepository;
import jshop.domain.product.repository.ProductRepository;
import jshop.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

@ExtendWith(MockitoExtension.class)
@DisplayName("[단위 테스트] SearchService")
class SearchServiceTest {

    @InjectMocks
    private SearchService searchService;

    @Mock
    private ProductDetailRepository productDetailRepository;

    @Nested
    @DisplayName("상품 조회 검증 - 상품 조회는 커서 방식의 페이징을 사용한다")
    class SearchProductDetail {

        @Test
        @DisplayName("상품 조회시 마지막 상품 ID(없다면 Long.MAX_VALUE)와, 검색어, 크기로 질의. 검색 결과는 SearchProductDetailsResponse 타입으로 제공")
        public void searchProductDetail_success() {
            // given
            long lastProductId = Long.MAX_VALUE;
            String query = "아이폰";
            int size = 30;

            PageRequest pageRequest = PageRequest.of(0, size, Sort.by(Direction.DESC, "id"));

            List<SearchProductDetailQueryResult> list = new ArrayList<>();
            SearchProductDetailQueryResult items = SearchProductDetailQueryResult
                .builder().id(1L).name("아이폰").price(1000L).build();

            list.add(items);
            Page<SearchProductDetailQueryResult> page = new PageImpl<>(list);

            List<ProductDetailResponse> contents = list.stream().map(ProductDetailResponse::of).toList();

            // when
            when(productDetailRepository.searchProductDetailsByQuery(lastProductId, query, pageRequest)).thenReturn(
                page);

            // then
            SearchProductDetailsResponse searchProductDetailsResponse = searchService.searchProductDetail(lastProductId,
                query, size);

            assertThat(searchProductDetailsResponse.getNextCursor()).isEqualTo(1L);
            assertThat(searchProductDetailsResponse.getProducts()).isEqualTo(contents);
        }

        @Test
        @DisplayName("상품 조회 결과가 없다면 다음 커서는 null, 상품 배열은 빈 리스트로 제공")
        public void searchProductDetail_noResult() {
            // given
            long lastProductId = Long.MAX_VALUE;
            String query = "아이폰";
            int size = 30;

            PageRequest pageRequest = PageRequest.of(0, size, Sort.by(Direction.DESC, "id"));

            List<SearchProductDetailQueryResult> list = new ArrayList<>();
            Page<SearchProductDetailQueryResult> page = new PageImpl<>(list);

            List<ProductDetailResponse> contents = new ArrayList<>();

            // when
            when(productDetailRepository.searchProductDetailsByQuery(lastProductId, query, pageRequest)).thenReturn(
                page);

            // then
            SearchProductDetailsResponse searchProductDetailsResponse = searchService.searchProductDetail(lastProductId,
                query, size);

            assertThat(searchProductDetailsResponse.getNextCursor()).isEqualTo(null);
            assertThat(searchProductDetailsResponse.getProducts()).isEqualTo(contents);
        }
    }
}