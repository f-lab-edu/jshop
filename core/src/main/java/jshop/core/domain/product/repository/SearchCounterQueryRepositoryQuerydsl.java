package jshop.core.domain.product.repository;

import static jshop.core.domain.product.entity.QProduct.product;
import static jshop.core.domain.product.entity.QProductDetail.productDetail;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Map;
import jshop.common.exception.ErrorCode;
import jshop.common.exception.JshopException;
import jshop.core.domain.product.dto.SearchCondition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class SearchCounterQueryRepositoryQuerydsl implements SearchCounterQueryRepository{

    private final JPAQueryFactory queryFactory;
    private final ObjectMapper objectMapper;

    @Override
    @Cacheable(value = "search", key="#condition")
    public Long getTotalCount(SearchCondition condition) {

        Long totalCount = queryFactory
            .select(productDetail.count())
            .from(productDetail)
            .where(
                productDetail.product.id.in(
                    JPAExpressions
                        .select(product.id)
                        .from(product)
                        .where(
                            nameLike(condition.getQuery()),
                            categoryEq(condition.getCategoryId()),
                            manufacturerEq(condition.getManufacturer())
                        )
                ),
                attributeEq(condition.getAttributeFilters()),
                productDetail.isDeleted.isFalse()
            ).fetchOne();

        return totalCount;
    }

    private BooleanBuilder attributeEq(List<Map<String, String>> attributeFilters) {
        BooleanBuilder builder = new BooleanBuilder();

        for (Map<String, String> attribute : attributeFilters) {
            try {
                String jsonStr = objectMapper.writeValueAsString(attribute);
                builder.or(Expressions.booleanTemplate("JSON_CONTAINS({0}, {1})", productDetail.attribute, jsonStr));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return builder;
    }

    private BooleanExpression manufacturerEq(String manufacturer) {
        return manufacturer != null ? product.manufacturer.eq(manufacturer) : null;
    }

    private BooleanExpression categoryEq(Long categoryId) {
        return categoryId != null ? product.category.id.eq(categoryId) : null;
    }

    private BooleanExpression nameLike(String query) {
        if (query == null) {
            log.error(ErrorCode.NO_SEARCH_QUERY.getLogMessage());
            throw JshopException.of(ErrorCode.NO_SEARCH_QUERY);
        }

        return Expressions.booleanTemplate("fulltext_match({0}, {1})", product.name, query);
    }
}
