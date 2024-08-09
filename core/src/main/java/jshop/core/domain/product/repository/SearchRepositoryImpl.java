package jshop.core.domain.product.repository;

import static jshop.core.domain.category.entity.QCategory.category;
import static jshop.core.domain.product.entity.QProduct.product;
import static jshop.core.domain.product.entity.QProductDetail.productDetail;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import jshop.core.domain.product.dto.QSearchProductDetailQueryResult;
import jshop.core.domain.product.dto.SearchCondition;
import jshop.core.domain.product.dto.SearchProductDetailQueryResult;
import jshop.common.exception.ErrorCode;
import jshop.common.exception.JshopException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class SearchRepositoryImpl implements SearchRepository {

    private final JPAQueryFactory queryFactory;
    private final ObjectMapper objectMapper;

    @Override
    public Page<SearchProductDetailQueryResult> search(SearchCondition condition, Pageable pageable) {
        /**
         * 필터조건 : manufacturer, categoryId, attribute
         * 정렬조건 : 가격, 생성일, 이름,
         */
        // @formatter:off
        List<SearchProductDetailQueryResult> content = queryFactory
            .select(new QSearchProductDetailQueryResult(
                productDetail.id,
                product.name,
                category.name,
                product.manufacturer,
                product.description,
                productDetail.price,
                productDetail.attribute))
            .from(product)
            .leftJoin(product.productDetails, productDetail)
            .leftJoin(product.category, category)
            .where(
                nameLike(condition.getQuery()),
                categoryEq(condition.getCategoryId()),
                manufacturerEq(condition.getManufacturer()),
                attributeEq(condition.getAttributeFilters()),
                productDetail.isDeleted.isFalse()
            )
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(buildOrderSpecifier(pageable.getSort()))
            .fetch();




        JPAQuery<Long> countQuery = queryFactory
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
            );
        // @formatter:on

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private OrderSpecifier[] buildOrderSpecifier(Sort sort) {
        List<OrderSpecifier> orderSpecifiers = new ArrayList<>();

        sort.stream().forEach(order -> {
            orderSpecifiers.add(new OrderSpecifier(getOrderDirection(order), getOrderProperty(order)));
        });

        return orderSpecifiers.toArray(new OrderSpecifier[orderSpecifiers.size()]);
    }

    private Expression getOrderProperty(Sort.Order order) {
        if (order.getProperty().equals("price")) {
            return productDetail.price;
        } else if (order.getProperty().equals("createdAt")) {
            return productDetail.createdAt;
        } else if (order.getProperty().equals("name")) {
            return product.name;
        } else {
            return null;
        }
    }

    private Order getOrderDirection(Sort.Order order) {
        return order.getDirection().equals(Direction.DESC) ? Order.DESC : Order.ASC;
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
