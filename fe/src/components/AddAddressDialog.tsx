import { Dialog, DialogTitle, List, ListItem, ListItemButton, ListItemAvatar, Avatar, ListItemText, Button } from "@mui/material";
import { blue } from "@mui/material/colors";
import { Person, Add } from "@mui/icons-material";
import IAddress from "../types/IAddress";
import { useState } from "react";

export interface SimpleDialogProps {
  open: boolean;  
  openAddAddress: () => void;
  closeAddAddress: () => void;
}

export default function AddAddressDialog(props: SimpleDialogProps) {
  const { open, openAddAddress, closeAddAddress } = props;
  const [address, setAddress] = useState<IAddress>();

  function submitNewAddress() {
    console.log()
  }

  return (
    <Dialog open={open}>
      <DialogTitle>Set backup account</DialogTitle>
      <Button onClick={() => closeAddAddress()}>취소</Button>
      <Button onClick={() => closeAddAddress()}>추가</Button>
    </Dialog>
  )
}