import { TextField } from "@mui/material"
import { useState } from "react";

export default function PasswordInput(props: { password: string, setPassword: React.Dispatch<React.SetStateAction<string>>, autoComplete: string }) {
  const password = props.password;
  const setPassword = props.setPassword;
  const autoComplete = props.autoComplete;
  const [isInvalidPassword, setIsInvalidPassword] = useState(false);

  function validatePassword(password: string) {
    setIsInvalidPassword(password.length < 8 || password.length > 16);
  };

  return (
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
      autoComplete={autoComplete}
      margin="normal"
      size="small"
    />
  )
}