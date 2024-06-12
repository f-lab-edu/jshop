import { Box } from "@mui/material";
import OrderCard from "../components/OrderCard";
import { Order } from "../components/OrderCard"

export default function Orders() {
  const orders: Order[] = [];
  const order: Order = {
    orderNumber: "1",
    product: "2",
    quantity: 3,
    price: 4,
    date: "2023.05.05"
  }

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
      {orders.map((o) => (
        <OrderCard order={o} />
      ))}
    </Box>
  )
}