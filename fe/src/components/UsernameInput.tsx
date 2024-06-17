import { TextField } from "@mui/material"
import { useState } from "react";

export default function UsernameInput(props: {username: string, setUsername: React.Dispatch<React.SetStateAction<string>>, autoComplete: string }) {
  const username = props.username;
  const setUsername = props.setUsername;
  const autoComplete = props.autoComplete;
  const [isInvalidUsername, setIsInvalidUsername] = useState(false);

  function validateUsername(username: string) {    
    setIsInvalidUsername(username.length < 2 || username.length > 10);
  };


  return (
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
      autoComplete={autoComplete}
      margin="normal"
      size="small"
    />
  )
}