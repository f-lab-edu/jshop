import { Dialog, DialogTitle, List, ListItem, ListItemButton, ListItemAvatar, Avatar, ListItemText, Button, Box, TextField, Stack, Typography, FormControl, InputLabel, MenuItem, Select } from "@mui/material";
import { useEffect, useState } from "react";
import apiInstance from "../api/instance";
import { useDispatch } from "react-redux";
import { UPDATE_USERINFO_TRIGGER } from "../redux/Action";
import IProduct from "../types/IProduct";
import ICategory from "../types/ICategory";

export interface SimpleDialogProps {
  open: boolean;
  close: () => void;
}

export default function AddProductDialog(props: SimpleDialogProps) {
  const dispatch = useDispatch();
  const { open, close } = props;
  const [name, setProductName] = useState("");
  const [categories, setCategories] = useState<ICategory[]>([]);
  const [categoryId, setCategoryId] = useState(0);
  const [manufacturer, setManufacturer] = useState("");
  const [description, setDescription] = useState("");
  const [attributes, setAttributes] = useState<{ [key: string]: string[] }>({ "a": ["1", "2", "3"] });

  useEffect(() => {
    apiInstance.get("/api/categories")
      .then(d => {
        setCategories(d.data.data);
      })
  }, [])

  function submitNewProduct() {
    const newProduct: IProduct = {
      name: name,
      categoryId,
      manufacturer,
      description,
      attributes
    }

    apiInstance.post("/api/products", newProduct)
    .then(d => {
      clear();
      close();
    })
    .catch(e => {
      alert(e?.response?.data?.message ?? "!");
    })
    
  }

  function clear() {
    setProductName("");
    setCategoryId(0);
    setManufacturer("");
    setDescription("");
  }

  return (
    <Dialog open={open}>
      <Box padding={3} paddingBottom={1}>
        <FormControl fullWidth size="small">
          <InputLabel id="category-label">카테고리</InputLabel>
          <Select
            labelId="category-label"
            id="category"
            value={categoryId}
            label="category"
            onChange={e => setCategoryId(e.target.value as number)}
          >
            {categories.map((category) => (
              <MenuItem key={Math.random()} value={category.id}>{category.name}</MenuItem>
            ))}
          </Select>
        </FormControl>

        <TextField label="상품명" type="text" value={name} onChange={e => setProductName(e.target.value)} placeholder="상품명" margin="normal" size="small" fullWidth />
        <TextField label="제조사" type="text" value={manufacturer} onChange={e => setManufacturer(e.target.value)} placeholder="제조사" margin="normal" size="small" fullWidth />
        <TextField label="상세설명" type="text" value={description} onChange={e => setDescription(e.target.value)} placeholder="상세설명" margin="normal" size="small" fullWidth />
        <Button variant="contained">속성 추가</Button>

      </Box>
      <Box display="flex" justifyContent="flex-end" margin={1}>
        <Stack direction={"row"} >
          <Button onClick={() => {
            clear();
            close()
          }}>취소</Button>
          <Button variant="contained" onClick={() => submitNewProduct()}>추가</Button>
        </Stack>
      </Box>
    </Dialog>
  )
}