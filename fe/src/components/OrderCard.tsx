import { Box, Button, Card, CardContent, Grid, Stack, Typography } from "@mui/material";
import IOrder from "../types/IOrder";
import AddressCard from "./AddressCard";

export default function OrderCard(props: { order: IOrder }) {
  const {order} = props;

  return (
    <Card variant="outlined" sx={{ width: "100%", margin: 1 }} >
      <Grid container spacing={2}>
        <Grid item xs={true}>
          <CardContent>
            <Typography variant="h6" gutterBottom>
              {order.date}
            </Typography>
            <Typography variant="body1" component="p">
              주문 번호: {order.orderNumber}
            </Typography>
            <Typography variant="body1" component="p">
              상품: {order.product}
            </Typography>
            <Typography variant="body1" component="p">
              수량: {order.quantity}
            </Typography>
            <Typography variant="body1" component="p">
              가격: ${order.price}
            </Typography>
            <Typography variant="body1" component="p">

            </Typography>
          </CardContent>
        </Grid>
        <Grid item xs={3}>
          <Box justifyContent={"center"} alignItems={"center"} display={"flex"} sx={{ width: "100%", height: "100%" }}>
            <Stack spacing={1} width={"100px"}>
              <Button fullWidth variant="outlined">상세보기</Button>
              <Button fullWidth variant="outlined">주문보기</Button>
              <Button fullWidth variant="outlined">주문취소</Button>
            </Stack>
          </Box>
        </Grid>
      </Grid>      
    </Card >
  )
}