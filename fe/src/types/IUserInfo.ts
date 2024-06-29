import IAddress from "./IAddress";

export default interface IUserInfo {
  username: string;
  email: string;
  userType: string;
  balance: number;
  addresses: IAddress[]; 
}