import { Box, Button, Stack, TextField, Typography } from "@mui/material";
import UsernameInput from "../components/UsernameInput";
import { useEffect, useState } from "react";
import Addresses from "../components/Addresses";
import IAddress from "../types/IAddress";
import AddressCard from "../components/AddressCard";
import apiInstance from "../api/instance";
import IUserInfo from "../types/IUserInfo";
import IResponse from "../types/IResponse";

export default function EditProfile() {
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [selectedAddress, setSelectedAddress] = useState<IAddress>();
  const [addresses, setAddresses] = useState<IAddress[]>([]);

  useEffect(() => {
    getUserInfo();
  }, [])

  function getUserInfo() {
    /**
     * TODO address가 추가되면, 유저 페이지에서 업데이트하는 기능
     */
    apiInstance.get<IResponse<IUserInfo>>("/api/users")
      .then(d => {
        setUsername(d.data.data.username);
        setEmail(d.data.data.email);
        setAddresses(d.data.data.addresses);
      })
      .catch(e => {
        console.error(e);
      })
  }

  function updateUserInfo() {
    const result = window.confirm("변경하시겠습니까?");
    if (result) {
      apiInstance.patch("/api/users", { username })
        .then(d => {
          getUserInfo();
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
          <TextField label="username" type="text" value={username} onChange={e => setUsername(e.target.value)} placeholder="email" margin="normal" size="small" fullWidth />
        </Box>

        <Box>
          <Typography variant="h5" gutterBottom>이메일</Typography>
          <TextField disabled label="username" type="text" value={email} onChange={e => setEmail(e.target.value)} placeholder="email" margin="normal" size="small" fullWidth />
        </Box>

        {selectedAddress && <AddressCard address={selectedAddress} />}
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