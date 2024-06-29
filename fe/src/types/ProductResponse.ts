export default interface ProductResponse {
  id: number;
  name: string;
  manufacturer: string;
  description: string;
  attributes: {[key: string]: string[]};
}