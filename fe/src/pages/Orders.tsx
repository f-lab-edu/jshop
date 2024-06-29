import { Box, Typography } from "@mui/material";
import OrderCard from "../components/OrderCard";
import IOrder from "../types/IOrder";

export default function Orders() {
  const orders: IOrder[] = [];

  for (let i = 0; i < 10; i++) {
    orders.push({
      date: new Date().toLocaleString(),
      price: Math.round(Math.random() * 100000),
      quantity: Math.round(Math.random() * 10),
      product: "test",
      orderNumber: `${i}`
    })
  }

  
  return (
    <Box flexDirection={"column"} justifyContent={"center"} alignItems={"center"} sx={{ width: "100%", display: "flex" }}>    
      <Typography variant="h4" width={"100%"} marginBottom={3}>주문내역</Typography>    
      {orders.map((o) => (
        <OrderCard order={o} />
      ))}
    </Box>
  )
}