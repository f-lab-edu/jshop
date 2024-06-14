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

export {signIn, goHome}