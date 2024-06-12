export default interface IResponse<T> {
  message: string;
  error?: number;
  data: T; 
}