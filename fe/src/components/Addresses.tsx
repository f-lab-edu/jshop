import { Box, Button, Typography } from "@mui/material";
import IAddress from "../types/IAddress";
import AddressCard from "./AddressCard";
import AddAddressDialog from "./AddAddressDialog";
import React, { useState } from "react";

interface Props {
  addresses: IAddress[];
  setSelectedAddress?: React.Dispatch<React.SetStateAction<IAddress | undefined>>;
}
export default function Addresses(props: Props) {
  const { addresses, setSelectedAddress } = props;
  const [open, setOpen] = useState(false);

  function openAddAddress (){
    setOpen(true);
  };

  function closeAddAddress () {
    console.log("close");
    setOpen(false);
  }

  return (
    <Box>
      <Typography variant="h5" gutterBottom>
        배송지
      </Typography>

      {addresses.map(address => (
        <AddressCard key={Math.random()} address={address} selectAddress={props.setSelectedAddress} />
      ))}

      <Button fullWidth variant="outlined" onClick={openAddAddress}>추가하기</Button>
      <AddAddressDialog
        open={open}      
        close={closeAddAddress} />
    </Box>
  )

}