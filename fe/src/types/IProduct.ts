import ICategory from "./ICategory";

export default interface IProduct {
  name: string;
  categoryId: number;
  manufacturer: string;
  description: string;
  attributes: {[key: string]: string[]}
}