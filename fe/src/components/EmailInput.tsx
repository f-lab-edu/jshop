import { TextField } from "@mui/material"
import { useState } from "react";

export default function EmailInput(props: { email: string, setEmail: React.Dispatch<React.SetStateAction<string>>, autoComplete: string }) {
  const email = props.email;
  const setEmail = props.setEmail;
  const autoComplete = props.autoComplete;
  
  const [isInvalidEmail, setIsInvalidEmail] = useState(false);

  function validateEmail(email: string) {    
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    setIsInvalidEmail(!re.test(email));    
  };

  return (
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
      autoComplete={autoComplete}
      margin="normal"
      size="small"
      fullWidth
    />
  )
}