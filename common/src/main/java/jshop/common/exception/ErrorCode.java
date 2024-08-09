package jshop.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // @formatter:off
    // 이미 등록된 자료
    ALREADY_REGISTERED_EMAIL(1001, "이미 등록된 이메일입니다.", "{}는 이미 등록된 이메일입니다.", HttpStatus.BAD_REQUEST),
    ALREADY_EXISTS_PRODUCT_DETAIL(1005, "이미 등록된 상세상품입니다.", "이미 등록된 상세 상품입니다. product id : {}, attribute : {}", HttpStatus.BAD_REQUEST), // 상품 ID, 상세 상품 속성
    ALREADY_EXISTS_CATEGORY(1006, "이미 등록된 카테고리입니다.", "{}는 이미 등록된 카테고리 입니다.", HttpStatus.BAD_REQUEST),

    // 인증 오류
    JWT_USER_NOT_FOUND(2001, "유저정보를 찾을 수 없습니다.", "JWT에서 유저 정보를 찾을 수 없습니다.", HttpStatus.UNAUTHORIZED),
    BAD_TOKEN(2010, "인증정보가 잘못되었습니다.", "인증 토큰 형식이 잘못되었습니다. {}", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED(2011, "로그인 세션이 만료되었습니다.", "JWT이 만료되었습니다. {}", HttpStatus.UNAUTHORIZED),

    // 인가 오류
    UNAUTHORIZED(2051, "권한이 없습니다.", "접근 권한이 없습니다. [{} : {}] [user : {}]", HttpStatus.UNAUTHORIZED), // 자원정보, 자원 ID, 유저 ID

    // ENTITY NOT FOUND
    USERID_NOT_FOUND(3010, "유저정보를 찾을 수 없습니다.", "유저 ID[{}]로 정보를 찾을 수 없습니다. ", HttpStatus.BAD_REQUEST),
    PRODUCTID_NOT_FOUND(3020, "상품 정보를 찾을 수 없습니다.", "상품 ID[{}]로 정보를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),
    CATEGORYID_NOT_FOUND(3030, "카테고리 정보를 찾을 수 없습니다.", "카테고리 ID[{}]로 정보를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),
    CATEGORY_NAME_NOT_FOUND(3030, "카테고리 정보를 찾을 수 없습니다.", "카테고리 이름[{}]로 정보를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),
    ADDRESSID_NOT_FOUND(3040, "주소 정보를 찾을 수 없습니다.", "주소 ID[{}]로 정보를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),
    PRODUCTDETAIL_ID_NOT_FOUND(3050, "상세 상품 정보를 찾을 수 없습니다.", "상세 상품 ID[{}]로 정보를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),
    INVENTORY_ID_NOT_FOUND(3055, "재고 정보를 찾을 수 없습니다.", "재고 ID[{}]로 정보를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),
    CART_NOT_FOUND(3060, "장바구니 정보를 찾을 수 없습니다", "유저 ID[{}] 로 장바구니 정보를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),
    CART_PRODUCTDETAIL_ID_NOT_FOUND(3065, "장바구니에 추가된 상품을 찾을 수 없습니다.", "장바구니에 추가된 상품 [{}] 를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),
    ORDER_ID_NOT_FOUND(3070, "주문 정보를 찾을 수 없습니다.", "주문 ID[{}] 로 정보를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),
    COUPON_ID_NOT_FOUND(3080, "쿠폰 정보를 찾을 수 없습니다.", "쿠폰 ID[{}]로 정보를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),
    USER_COUPON_ID_NOT_FOUND(3085, "유저-쿠폰 정보를 찾을 수 없습니다.", "유저-쿠폰 ID[{}]로 정보를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),

    // 유저 관련 오류
    USER_NOT_SELLER(4001, "판매 유저가 아니라면 상품을 등록할 수 없습니다.", "판매 유저가 아니라면 상품을 등록할 수 없습니다. 현재 유저 : [{}]", HttpStatus.BAD_REQUEST),

    // 잘못된 요청
    INVALID_REQUEST_BODY(10001, "요청이 잘못되었습니다.", "Request Body가 잘못되었습니다 (공백, 잘못된 포맷).", HttpStatus.BAD_REQUEST),
    BAD_REQUEST(10010, "요청이 잘못되었습니다.", "", HttpStatus.BAD_REQUEST),
    NO_SEARCH_QUERY(10050, "검색어가 없습니다.", "상품 검색 쿼리는 null일 수 없습니다.", HttpStatus.BAD_REQUEST),
    INVALID_ORDER_ITEM(10060, "주문 정보가 잘못되었습니다", "주문 수량이나 가격이 잘못되었습니다. 수량 : [{}], 가격 : [{}]", HttpStatus.BAD_REQUEST),

    // 비즈니스 로직 오류
    ILLEGAL_QUANTITY_REQUEST_EXCEPTION(50001, "재고 변화량이 잘못되었습니다.", "재고 변화량이 잘못되었습니다. [{}]", HttpStatus.BAD_REQUEST),
    NEGATIVE_QUANTITY_EXCEPTION(50002, "재고는 음수일 수 없습니다.", "재고는 음수일 수 없습니다. [{}]", HttpStatus.BAD_REQUEST),
    ILLEGAL_PRICE_EXCEPTION(50101, "가격이 잘못되었습니다.", "가격은 0보다 커야합니다. [{}].", HttpStatus.BAD_REQUEST),
    ILLEGAL_PAGE_REQUEST(60001, "요청할 수 없는 페이지 입니다.", "요청할 수 없는 페이지 입니다. pageNumber : [{}], pageSize : [{}]", HttpStatus.BAD_REQUEST),
    ILLEGAL_CART_QUANTITY_REQUEST_EXCEPTION(70001, "장바구니 수량이 잘못되었습니다.", "장바구니 수량은 1 이상이여야 합니다. [{}]", HttpStatus.BAD_REQUEST),
    ILLEGAL_BALANCE_REQUEST(70101, "잔고 변화는 0보다 커야합니다.", "잔고 변화는 0보다 커야합니다. 요청 값 : [{}]", HttpStatus.BAD_REQUEST),
    WALLET_BALANCE_EXCEPTION(80001, "잔고는 음수일 수 없습니다.", "잔고는 음수일 수 없습니다. [{}]", HttpStatus.BAD_REQUEST),
    ALREADY_SHIPPING_ORDER(90001, "이미 배송이 시작된 주문입니다.", "이미 배송이 시작된 주문입니다. [{}]", HttpStatus.BAD_REQUEST),
    ALREADY_CANCLED_DELIVERY(90100, "이미 취소된 배송입니다.", "이미 취소된 배송입니다. DELIVERY_ID : [{}]", HttpStatus.BAD_REQUEST),
    ILLEGAL_DELIVERY_STATE(90200, "배송 상태가 잘못되었습니다.", "배송 상태가 잘못되었습니다. 현재 상태 : [{}], 원하는 상태 : [{}]", HttpStatus.BAD_REQUEST),
    ORDER_PRICE_MISMATCH(90500, "주문 가격과 상품 가격이 맞지 않습니다.", "주문 가격과 상품 가격이 맞지 않습니다. 주문가격 : [{}] , 상품 가격 총합 : [{}]", HttpStatus.BAD_REQUEST),
    PRODUCT_PRICE_MISMATCH(90501, "상품 가격이 변경되었습니다.","주문요청 상품 가격과 실제 상품 가격이 맞지 않습니다. 상품 상세 ID : [{}] 주문 상품 가격 : " + "[{}], 실제 상품 가격 : [{}]", HttpStatus.BAD_REQUEST),
    ORDER_QUANTITY_MISMATCH(90550, "주문 수량과 상품 수량의 합이 맞지 않습니다.", "주문 수량과 상품 수량의 합이 맞지 않습니다. 주문수량 : [{}] , 상품 수량 총합 : " + "[{}]", HttpStatus.BAD_REQUEST),
    COUPON_MIN_PRICE_EXCEPTION(96001, "주문금액이 쿠폰 사용 최소금액보다 작습니다.", "주문금액이 쿠폰 사용 최소금액보다 작습니다. 최소금액 : [{}], 주문금액 : [{}]", HttpStatus.BAD_REQUEST),
    COUPON_USAGE_PERIOD_EXCEPTION(96051, "쿠폰 사용기간이 잘못되었습니다.", "쿠폰 사용기간이 잘못되었습니다. {} ~ {}", HttpStatus.BAD_REQUEST),
    COUPON_ISSUE_PERIOD_EXCEPTION(96052, "쿠폰 발급기간이 잘못되었습니다.", "쿠폰 발급기간이 잘못되었습니다. {} ~ {}", HttpStatus.BAD_REQUEST),
    COUPON_OUT_OF_STOCK_EXCEPTION(97051, "쿠폰이 모두 발급되었습니다.", "쿠폰이 모두 발급되었습니다. CouponID : [{}], [{}]", HttpStatus.BAD_REQUEST),
    COUPON_TYPE_NOT_DEFINED(98051, "쿠폰 타입이 지정되지 않았습니다.", "쿠폰 타입이 지정되지 않았습니다. 쿠폰을 생성할 수 없습니다. CouponID : [{}], Type: " + "[{}]", HttpStatus.BAD_REQUEST),
    COUPON_CREATE_EXCEPTION(98052, "쿠폰 생성중 문제가 발생했습니다.", "쿠폰 생성중 문제가 발생했습니다. CouponID : [{}]", HttpStatus.BAD_REQUEST),

    // 잘못된 상품
    INVALID_PRODUCT_ATTRIBUTE(100001, "상세 상품 속성이 잘못되었습니다.", "상세상품 속성이 상품 속성에 없습니다. [attributes : {}]   [attribute : {}]", HttpStatus.BAD_REQUEST),
    INVALID_PRODUCTDETAIL_INVENTORY(100100, "상세 상품이 잘못되었습니다. 관리자에게 문의하세요", "상세 상품의 Inventory가 잘못되었습니다. product " + "detail : [{}]", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_PRODUCTDETAIL_PRODUCT(100200, "상세 상품이 잘못되었습니다. 관리자에게 문의하세요", "상세 상품의 Product가 잘못되었습니다. [{}]", HttpStatus.INTERNAL_SERVER_ERROR),

    // 서버 문제
    USER_WALLET_NOT_FOUND(200100, "사용자의 지갑을 찾을 수 없습니다. 관리자에게 문의하세요", "사용자의 지갑을 찾을 수 없습니다. user : [{}]", HttpStatus.INTERNAL_SERVER_ERROR),
    INTERNAL_SERVER_ERROR(300100, "서버 문제가 발생했습니다. 잠시후 재시도 해주세요.", "", HttpStatus.INTERNAL_SERVER_ERROR);
    // @formatter:on

    private final int code;
    private final String message;
    private final String logMessage;
    private final HttpStatus httpStatus;
}
