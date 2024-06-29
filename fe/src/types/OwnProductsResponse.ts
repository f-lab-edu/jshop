import ProductResponse from "./ProductResponse";

export default interface OwnProductsResponse {
  page: number;
  totalPage: number;
  totalCount: number;
  products: ProductResponse[];
}