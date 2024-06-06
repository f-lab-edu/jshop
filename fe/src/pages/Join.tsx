import { useState } from "react";
import styled from "styled-components";
import Input from "../styled/InputComponent";
import Button1 from "../styled/Button1";
import design from "../config/design.json";
import axios from "axios";

const Wrapper = styled.div`
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
`

const JoinContainer = styled.div`  
  flex: 0;
  text-align: center;
  display: flex;
  flex-direction: column;
`

const Buttons = styled.div`
  display: flex;
  justify-content: space-around;
  margin-top: 30px;
`

export default function Join() {
  const [email, setEmail] = useState("");
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [userType, setUserType] = useState("USER")

  function signUp() {
    axios.post("/api/join", {
      username,
      password,
      userType,
      email
    })
    .then(d => {
      alert("성공적으로 가입되었습니다.");
      window.location.href = "/login";
    })
    .catch(e => {   
      console.log(e)   ;
      alert(e?.response?.data?.message ?? "알 수 없는 오류가 발생했습니다.");
    })
  }

  return (
    <Wrapper>
      <h1>
        회원 가입
      </h1>
      <JoinContainer>
        <Input type="username" value={email} onChange={e => setEmail(e.target.value)} name="username" placeholder="email" autoComplete="off"/>
        <Input type="text" value={username} onChange={e => setUsername(e.target.value)} name="name" placeholder="name" autoComplete="off"/>
        <Input type="password" value={password} onChange={e => setPassword(e.target.value)} name="password" placeholder="password" autoComplete="new-password"/>
        <Buttons>
          <Button1 bgColor={userType == "USER" ? design.theme1 : "#eee"} onClick={e => setUserType("USER")}>일반회원</Button1>
          <Button1 bgColor={userType == "SELLER" ? design.theme1 : "#eee"} onClick={e => setUserType("SELLER")}>판매회원</Button1>
        </Buttons>
        <Buttons>
          <Button1 onClick={signUp}>sign up</Button1>
        </Buttons>
      </JoinContainer>
    </Wrapper>
  )
}