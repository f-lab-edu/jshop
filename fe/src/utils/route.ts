function goHome() {
  window.location.href = "/";
}

function signIn() {
  /**
   * TODO
   * 리프레시 토큰 있다면, 그걸로 바로 로그인
   */
  window.location.href = "/login";
}

function goMyPage() {
  /**
   * TODO
   * 리프레시 토큰 있다면, 그걸로 바로 로그인하고 이동     
   */
  window.location.href = "/mypage"
}

function goOrders() {
  window.location.href = "/mypage/orders";
}

function goCart() {
  window.location.href = "/mypage/cart";
}


export {signIn, goMyPage, goHome, goCart, goOrders}