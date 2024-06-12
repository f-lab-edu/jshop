import axios from "axios";

const apiInstance = axios.create({
  baseURL: "/",
  headers: {
    "Authorization" : sessionStorage.getItem("token")
  }
})

export default apiInstance;