import { Dialog, DialogTitle, List, ListItem, ListItemButton, ListItemAvatar, Avatar, ListItemText, Button, Box, TextField, Stack, Typography } from "@mui/material";
import { blue } from "@mui/material/colors";
import { Person, Add } from "@mui/icons-material";
import IAddress from "../types/IAddress";
import { useState } from "react";
import apiInstance from "../api/instance";
import ISaveAddress from "../types/ISaveAddressRequest";
import { useDispatch } from "react-redux";
import { UPDATE_USERINFO_TRIGGER } from "../redux/Action";

export interface SimpleDialogProps {
  open: boolean;
  close: () => void;
}

export default function AddAddressDialog(props: SimpleDialogProps) {
  const dispatch = useDispatch();
  const { open, close } = props;
  const [receiverName, setReceiverName] = useState("");
  const [receiverNumber, setReceiverNumber] = useState("");
  const [province, setProvince] = useState("");
  const [city, setCity] = useState("");
  const [district, setDistrict] = useState("");
  const [street, setStreet] = useState("");
  const [detailAddress1, setDetailAddress1] = useState("");
  const [detailAddress2, setDetailAddress2] = useState("");
  const [message, setMessage] = useState("");

  function submitNewAddress() {
    const newAddress: ISaveAddress = {      
      receiverName,
      receiverNumber,
      province,
      city,
      district,
      street,
      detailAddress1,
      detailAddress2,
      message
    }

    apiInstance.post("/api/address", newAddress)
      .then(d => {        
        clear();
        close();
        dispatch({
          type: UPDATE_USERINFO_TRIGGER
        })
      })
      .catch(e => console.error(e))

  }

  function clear() {
    setReceiverName("");
    setReceiverNumber("");
    setProvince("");
    setCity("");
    setDistrict("");
    setStreet("");
    setDetailAddress1("");
    setDetailAddress2("");
    setMessage("");
  }

  return (
    <Dialog open={open}>
      <Box padding={3} paddingBottom={1}>
        <Box margin={2}>
          <Typography variant="h5" gutterBottom>받는사람</Typography>
          <TextField label="이름" type="text" value={receiverName} onChange={e => setReceiverName(e.target.value)} placeholder="이름" margin="normal" size="small" fullWidth />
          <TextField label="전화번호" type="text" value={receiverNumber} onChange={e => setReceiverNumber(e.target.value)} placeholder="전화번호" margin="normal" size="small" fullWidth />
        </Box>
        <Box margin={2}>
          <Typography variant="h5" gutterBottom>주소</Typography>
          <TextField label="시 / 도" type="text" value={province} onChange={e => setProvince(e.target.value)} placeholder="ex) 경기도, 서울특별시" margin="normal" size="small" fullWidth />
          <TextField label="시" type="text" value={city} onChange={e => setCity(e.target.value)} placeholder="ex) 광주시, 수원시" margin="normal" size="small" fullWidth />
          <TextField label="구 / 군" type="text" value={district} onChange={e => setDistrict(e.target.value)} placeholder="ex) 강동구, 송정동" margin="normal" size="small" fullWidth />
          <TextField label="도로명" type="text" value={street} onChange={e => setStreet(e.target.value)} placeholder="ex) 경안천로 159" margin="normal" size="small" fullWidth />
          <TextField label="세부주소 1" type="text" value={detailAddress1} onChange={e => setDetailAddress1(e.target.value)} placeholder="ex) 101-1202" margin="normal" size="small" fullWidth />
          <TextField label="세부주소 2" type="text" value={detailAddress2} onChange={e => setDetailAddress2(e.target.value)} placeholder="ex) 12층" margin="normal" size="small" fullWidth />
          <TextField label="배송 메시지" type="text" value={message} onChange={e => setMessage(e.target.value)} placeholder="ex) 문앞에 놔주세요" margin="normal" size="small" fullWidth />
        </Box>
        <Box display="flex" justifyContent="flex-end">
          <Stack direction={"row"} >
            <Button onClick={() => {
              clear();
              close()
            }}>취소</Button>
            <Button variant="contained" onClick={() => submitNewAddress()}>추가</Button>
          </Stack>
        </Box>
      </Box>
    </Dialog>
  )
}