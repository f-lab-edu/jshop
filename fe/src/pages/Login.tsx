import { useState } from "react"
import styled from "styled-components";
import Input from "../styled/InputComponent";
import Button1 from "../styled/Button1";
import axios from "axios";
import isAuthenticated from "../utils/isAuthenticated";
import { Navigate } from "react-router-dom";

const Wrapper = styled.div`
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
`

const LoginContainer = styled.div`  
  flex: 0;
  text-align: center;
  display: flex;
  flex-direction: column;
`

const Buttons = styled.div`
  display: flex;
  justify-content: space-between;
  margin-top: 30px;
`

export default function Login() {  
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  if (isAuthenticated()) {
    return <Navigate to="/" />
  }

  function signIn() {
    axios.post("/api/login", {
      username,
      password,      
    })
      .then(d => {
        if (d.headers.authorization) {
          sessionStorage.setItem("token", d.headers.authorization);
          window.location.href = "/";
        } else {
          alert("로그인 실패. 관리자에게 문의하세요 ")
        }
          
      })
      .catch(e => {        
        alert("아이디와 비밀번호가 잘못되었습니다.");
        window.location.reload();
      })
  }

  function signUp() {
    window.location.href = "/join";
  }
  return (
    <Wrapper>
      <h1>
        JShop
      </h1>
      <LoginContainer>
        <Input type="username" value={username} onChange={e => setUsername(e.target.value)} placeholder="email" />
        <Input type="password" value={password} onChange={e => setPassword(e.target.value)} placeholder="password" />
        <Buttons>
          <Button1 onClick={signIn}>Sign in</Button1>
          <Button1 onClick={signUp}>Sign up</Button1>
        </Buttons>
      </LoginContainer>
    </Wrapper>
  )
}