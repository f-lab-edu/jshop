import { Box, Stack, TextField, Typography } from "@mui/material";
import UsernameInput from "../components/UsernameInput";
import { useState } from "react";
import Addresses from "../components/Addresses";
import IAddress from "../types/IAddress";
import AddressCard from "../components/AddressCard";

export default function EditProfile() {
  const [username, setUsername] = useState("test");
  const [selectedAddress, setSelectedAddress] = useState<IAddress>();
  const addresses: IAddress[] = [] 

  addresses.push({
    id: 1,
    receiverName: "김재현",
    receiverNumber: "010-1234-5678",
    province: "경기도",
    city: "광주시",
    district: "송정동",
    street: "경안천로 159",
    detailAddress1: "101-1202",
    message: "문앞에 놔주세요"
  })

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