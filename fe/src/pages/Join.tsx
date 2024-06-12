import { useState } from "react";

import axios from "axios";
import isAuthenticated from "../utils/isAuthenticated";
import { Navigate } from "react-router-dom";
import { Box, Button, ButtonGroup, Stack, TextField, Typography } from "@mui/material";

export default function Join() {
  const [email, setEmail] = useState("");
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [userType, setUserType] = useState("USER")
  const [isInvalidEmail, setIsInvalidEmail] = useState(false);
  const [isInvalidUsername, setIsInvalidUsername] = useState(false);
  const [isInvalidPassword, setIsInvalidPassword] = useState(false);

  if (isAuthenticated()) {
    return <Navigate to="/" />
  }


  function validateEmail(email: string) {    
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    setIsInvalidEmail(!re.test(email));    
  };

  function validateUsername(username: string) {
    setIsInvalidUsername(username.length < 2 || username.length > 10);
  };

  function validatePassword(password: string) {
    setIsInvalidPassword(password.length < 8 || password.length > 16);
  };

  function totalValidate() {
    return isInvalidEmail || isInvalidUsername || isInvalidPassword || email == "" || username == "" || password == "";
  }

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
        console.log(e);
        alert(e?.response?.data?.message ?? "알 수 없는 오류가 발생했습니다.");
      })
  }

  return (
    <Box justifyContent={"center"} alignItems={"center"} sx={{width: "100%", height: "100vh", display: "flex"}}>
      <Stack direction="column" spacing={2}>
        <Stack>
          <Typography variant={"h4"}>회원가입</Typography>
        </Stack>
        <Stack direction="column" sx={{ width: 300 }}>
          <TextField
            label="email"
            type="username" value={email} onChange={e => {
              setEmail(e.target.value)
              validateEmail(e.target.value)
            }}
            error={isInvalidEmail}
            helperText={isInvalidEmail ? "이메일 형식에 맞춰주세요" : ""}
            name="username"
            placeholder="email"
            autoComplete="off"
            margin="normal"
            size="small"
            fullWidth
          />
          <TextField
            label="username"
            type="text"
            value={username}
            onChange={e => {
              setUsername(e.target.value);
              validateUsername(e.target.value);
            }}
            error={isInvalidUsername}
            helperText={isInvalidUsername ? "유저이름은 2 ~ 10 자 입니다." : ""}
            name="name"
            placeholder="name"
            autoComplete="off"
            margin="normal"
            size="small"
          />
          <TextField
            label="password"
            type="password"
            value={password}
            onChange={e => {
              setPassword(e.target.value);
              validatePassword(e.target.value);
            }}
            error={isInvalidPassword}
            helperText={isInvalidPassword ? "비밀번호는 8 ~ 16 자 입니다." : ""}
            name="password"
            placeholder="password"
            autoComplete="new-password"
            margin="normal"
            size="small"
          />
        </Stack>
        <Stack direction="row" justifyContent={"center"}>
          <ButtonGroup>
            <Button variant={userType == "USER" ? "contained" : "outlined"} onClick={e => setUserType("USER")}>일반회원</Button>
            <Button variant={userType == "SELLER" ? "contained" : "outlined"} onClick={e => setUserType("SELLER")}>판매회원</Button>
          </ButtonGroup>
        </Stack>
        <Stack direction="row" justifyContent={"center"}>
          <Button onClick={signUp} variant="contained" disabled={totalValidate() ? true : false}>Sign up</Button>
        </Stack>
      </Stack>
    </Box>
  )
}