import { Box, Button, Card, CardContent, Grid, Stack, Typography } from "@mui/material";
import IAddress from "../types/IAddress";
import { createTheme } from '@mui/material/styles';
import { purple, red } from '@mui/material/colors';

interface Props {
  address: IAddress;
  selectAddress?: React.Dispatch<React.SetStateAction<IAddress | undefined>>;
}

export default function AddressCard(props: Props) {
  const address = props.address;
  const selectAddres = props.selectAddress;
  return (
    <Card variant="outlined" sx={{ width: "100%", marginTop: 1, marginBottom: 1 }}>
      <Grid container spacing={2}>
        <Grid item xs={true}>
          <CardContent>
            <Typography variant="h6" gutterBottom>
              {address.receiverName}
            </Typography>
            <Typography variant="body1" component="p">
              {address.province} {address.city} {address.district} {address.street} {address.detailAddress1} {address.detailAddress2}
            </Typography>
            <Typography variant="body1" component="p">
              {address.receiverNumber}
            </Typography>
            <Typography variant="body1" component="p">
              {address.message}
            </Typography>
          </CardContent>
        </Grid>
        <Grid item xs={3}>
          <Box justifyContent={"center"} alignItems={"center"} display={"flex"} sx={{ width: "100%", height: "100%" }}>
            <Stack spacing={1} width={"100px"}>
              <Button fullWidth variant="outlined" style={{ borderColor: red[300], color: red[300] }} >삭제</Button>            
              {selectAddres && <Button fullWidth variant="outlined" onClick={() => selectAddres}>선택</Button>}
            </Stack>
          </Box>
        </Grid>
      </Grid>
    </Card >
  )
}