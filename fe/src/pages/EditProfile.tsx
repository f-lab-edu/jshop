import { Box, Button, Stack, TextField, Typography } from "@mui/material";
import UsernameInput from "../components/UsernameInput";
import { useEffect, useLayoutEffect, useState } from "react";
import Addresses from "../components/Addresses";
import IAddress from "../types/IAddress";
import AddressCard from "../components/AddressCard";
import apiInstance from "../api/instance";
import IUserInfo from "../types/IUserInfo";
import IResponse from "../types/IResponse";
import { useDispatch, useSelector } from "react-redux";
import State from "../types/State";
import { isEqual } from "lodash";
import { UPDATE_USERINFO, UPDATE_USERINFO_TRIGGER } from "../redux/Action";

export default function EditProfile() {
  const userInfo = useSelector((state: State) => state.userInfo, isEqual);  
  const dispatch = useDispatch();

  const [username, setUsername] = useState("");
  const [addresses, setAddresses] = useState<IAddress[]>([]);
  const [email, setEmail] = useState("");

  useEffect(() => {    
    setUsername(userInfo.username);
    setAddresses(userInfo.addresses);
    setEmail(userInfo.email);
  }, [userInfo])

  function updateUsername(newUsername: string) {
    setUsername(newUsername);        
  }

  function updateUserInfo() {
    const result = window.confirm("변경하시겠습니까?");
    if (result) {
      apiInstance.patch("/api/users", { username })
        .then(d => {          
          dispatch({
            type: UPDATE_USERINFO_TRIGGER
          })
        })
        .catch(e => {
          console.error(e);
        })
    }
  }

  return (
    <Box>
      <Stack direction={"column"} spacing={5}>
        <Typography variant="h4" width={"100%"} marginBottom={3}>프로필</Typography>
        <Box>
          <Typography variant="h5" gutterBottom>이름</Typography>
          <TextField label="username" type="text" value={username} onChange={e => updateUsername(e.target.value)} placeholder="email" margin="normal" size="small" fullWidth />
        </Box>

        <Box>
          <Typography variant="h5" gutterBottom>이메일</Typography>
          <TextField disabled label="username" type="text" value={email} placeholder="email" margin="normal" size="small" fullWidth />
        </Box>

        <Box>
          <Addresses addresses={addresses} />
        </Box>
        <Box>
          <Button onClick={updateUserInfo} variant="contained">변경</Button>
        </Box>

      </Stack>
    </Box>
  )
}