export default function isAuthenticated() {
  return sessionStorage.getItem("token") !== null;
}