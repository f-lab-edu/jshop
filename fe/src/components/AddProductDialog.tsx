import { Dialog, DialogTitle, List, ListItem, ListItemButton, ListItemAvatar, Avatar, ListItemText, Button, Box, TextField, Stack, Typography } from "@mui/material";
import { useState } from "react";
import apiInstance from "../api/instance";
import { useDispatch } from "react-redux";
import { UPDATE_USERINFO_TRIGGER } from "../redux/Action";
import IProduct from "../types/IProduct";

export interface SimpleDialogProps {
  open: boolean;
  close: () => void;
}

export default function AddProductDialog(props: SimpleDialogProps) {
  const dispatch = useDispatch();
  const { open, close } = props;
  const [productName, setProductName] = useState("");
  const [category, setCategory] = useState("");
  const [manufacturer, setManufacturer] = useState("");
  const [description, setDescription] = useState("");  

  function submitNewProduct() {
    const newProduct: IProduct = {      
      productName,
      category,
      manufacturer,
      description,      
    }
    clear();
    close();
  }

  function clear() {
    setProductName("");
    setCategory("");
    setManufacturer("");
    setDescription("");
  }

  return (
    <Dialog open={open}>
      <Box padding={3} paddingBottom={1}>
        <Box margin={2}>          
          <TextField label="상품명" type="text" value={productName} onChange={e => setProductName(e.target.value)} placeholder="상품명" margin="normal" size="small" fullWidth />
          <TextField label="카테고리" type="text" value={category} onChange={e => setCategory(e.target.value)} placeholder="카테고리" margin="normal" size="small" fullWidth />
        </Box>
        <Box margin={2}>
          <TextField label="제조사" type="text" value={manufacturer} onChange={e => setManufacturer(e.target.value)} placeholder="제조사" margin="normal" size="small" fullWidth />
          <TextField label="상세설명" type="text" value={description} onChange={e => setDescription(e.target.value)} placeholder="상세설명" margin="normal" size="small" fullWidth />          
        </Box>
        <Box display="flex" justifyContent="flex-end">
          <Stack direction={"row"} >
            <Button onClick={() => {
              clear();
              close()
            }}>취소</Button>
            <Button variant="contained" onClick={() => submitNewProduct()}>추가</Button>
          </Stack>
        </Box>
      </Box>
    </Dialog>
  )
}