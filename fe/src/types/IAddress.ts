export default interface IAddress {
  id: number;
  receiverName: string;
  receiverNumber: string;
  province: string;
  city: string;
  district: string;
  street: string;
  detailAddress1?: string;
  detailAddress2?: string;
  message: string;
}