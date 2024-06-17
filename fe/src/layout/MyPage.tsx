import { alpha, Box, Drawer, Grid, List, ListItem, ListItemButton, ListItemIcon, ListItemText, Stack } from "@mui/material";
import { LocalShipping, Edit, ShoppingCart } from '@mui/icons-material';
import { Navigate, Outlet } from "react-router-dom";
import isAuthenticated from "../utils/isAuthenticated";
import { goOrders, goMyPage, goCart } from "../utils/route";
export default function MyPage() {

  if (!isAuthenticated()) {
    return <Navigate to="/login" />
  }

  return (  
    <Box>
      <Grid container direction={"row"} >
        <Grid item width={200}>
          <List>
            <ListItem disablePadding onClick={goOrders}>
              <ListItemButton>
                <ListItemIcon>
                  <LocalShipping />
                </ListItemIcon>
                <ListItemText primary="주문내역" />
              </ListItemButton>
            </ListItem>
            <ListItem disablePadding onClick={goMyPage}>
              <ListItemButton>
                <ListItemIcon>
                  <Edit />
                </ListItemIcon>
                <ListItemText primary="개인정보수정" />
              </ListItemButton>
            </ListItem>
            <ListItem disablePadding onClick={goCart}>
              <ListItemButton>
                <ListItemIcon>
                  <ShoppingCart />
                </ListItemIcon>
                <ListItemText primary="장바구니" />
              </ListItemButton>
            </ListItem>
          </List>

        </Grid>
        <Grid item paddingTop={2} marginLeft={3} sx={{flexGrow: 1}}>
          <Outlet />
        </Grid>
      </Grid>
    </Box>
  )
}