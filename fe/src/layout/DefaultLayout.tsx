import { Box, Container } from "@mui/material";
import { Outlet } from "react-router-dom";
import Header from "../components/Header";

export default function DefaultLayout() {

  return (
    <Box justifyContent={"center"}>
      <Header />
      <Container fixed sx={{width: "1020px", minWidth: "1020px", height: "100%", marginTop: 3}}>
        <Outlet />
      </Container>      
    </Box>
  )
}