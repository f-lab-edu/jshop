import apiInstance from "../api/instance";

export default function logout() {
  apiInstance.post("/api/logout", {}, {headers : {"Authorization" : sessionStorage.getItem("token")}})
    .then(d => {
      sessionStorage.removeItem("token"); 
      window.location.href = "/";
    })
    .catch(e => {
      console.error(e);
    })
}