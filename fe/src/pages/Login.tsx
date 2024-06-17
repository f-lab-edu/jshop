import { useState } from "react"
import styled from "styled-components";
import Input from "../styled/InputComponent";
import Button1 from "../styled/Button1";
import axios from "axios";
import isAuthenticated from "../utils/isAuthenticated";
import { Navigate } from "react-router-dom";
import { TextField, Button, Stack, Typography, Box } from "@mui/material";

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
    <Box justifyContent={"center"} alignItems={"center"} sx={{width: "100%", height: "100vh", display: "flex"}}>
      <Stack direction={"column"}>
        <Typography variant={"h4"} align={"center"} >JShop</Typography>

        <Stack direction={"column"} sx={{ width: 300 }}>
          <TextField label="email" type="username" value={username} onChange={e => setUsername(e.target.value)} placeholder="email" margin="normal" size="small" fullWidth />
          <TextField label="password" type="password" value={password} onChange={e => setPassword(e.target.value)} placeholder="password" margin="normal" size="small" />
        </Stack>
        <Stack spacing="auto" direction="row" marginTop={3}>
          <Button variant="contained" onClick={signIn}>Sign in</Button>
          <Button variant="outlined" onClick={signUp}>Sign up</Button>
        </Stack>
      </Stack>
    </Box>
  )
}