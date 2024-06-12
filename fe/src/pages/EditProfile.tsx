import { Box, Stack, TextField, Typography } from "@mui/material";
import UsernameInput from "../components/UsernameInput";
import { useEffect, useState } from "react";
import Addresses from "../components/Addresses";
import IAddress from "../types/IAddress";
import AddressCard from "../components/AddressCard";
import apiInstance from "../api/instance";
import IUserInfo from "../types/IUserInfo";
import IResponse from "../types/IResponse";

export default function EditProfile() {
  const [username, setUsername] = useState("test");
  const [selectedAddress, setSelectedAddress] = useState<IAddress>();
  const [addresses, setAddresses] = useState<IAddress[]>([]);
  

  function updateUserInfo() {
    /**
     * TODO address가 추가되면, 유저 페이지에서 업데이트하는 기능
     */
    apiInstance.get<IResponse<IUserInfo>>("/api/users")
      .then(d => {
        debugger;
        console.log(d.data);
        setUsername(d.data.data.username);
        setAddresses(d.data.data.addresses);
      });
  }
  useEffect(() => {
    updateUserInfo();
  }, [])

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
          <TextField label="username" type="text" value={username} onChange={e => setUsername(e.target.value)} placeholder="email" margin="normal" size="small" fullWidth />
        </Box>

        {selectedAddress && <AddressCard address={selectedAddress} />}        
        <Box>
          <Addresses addresses={addresses} />
        </Box>

      </Stack>
    </Box>
  )
}