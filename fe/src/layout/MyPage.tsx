import { alpha, Box, Drawer, Grid, List, ListItem, ListItemButton, ListItemIcon, ListItemText, Stack } from "@mui/material";
import { LocalShipping, Edit, ShoppingCart } from '@mui/icons-material';
import { Link, Navigate, Outlet } from "react-router-dom";
import isAuthenticated from "../utils/isAuthenticated";
import { useDispatch, useSelector } from "react-redux";
import { UPDATE_USERINFO } from "../redux/Action";
import State from "../types/State";
import { isEqual } from "lodash";
import { useEffect } from "react";
import apiInstance from "../api/instance";
import IResponse from "../types/IResponse";
import IUserInfo from "../types/IUserInfo";

export default function MyPage() {
  const userType = useSelector((state: State) => state.userInfo.userType, isEqual);
  const updateUserInfo = useSelector((state: State) => state.updateUserInfo, isEqual);
  const dispatch = useDispatch();

  useEffect(() => {
    getUserInfo();
  }, [updateUserInfo]);
  function getUserInfo() {
    apiInstance.get<IResponse<IUserInfo>>("/api/users")
      .then(d => {
        dispatch({
          type: UPDATE_USERINFO,
          userInfo: d.data.data
        })
      })
      .catch(e => {
        console.error(e);
      })
  }

  if (!isAuthenticated()) {
    return <Navigate to="/login" />
  }

  const sidebar = [
    {
      path: "/mypage/orders",
      icon: LocalShipping,
      label: "주문내역"
    },
    {
      path: "/mypage/",
      icon: Edit,
      label: "개인정보수정"
    },
    {
      path: "/mypage/cart",
      icon: ShoppingCart,
      label: "장바구니"
    }
  ];

  if (userType == "SELLER") {
    sidebar.push({
      path: "/mypage/products",
      icon: ShoppingCart,
      label: "상품관리"
    })
  }

  return (
    <Box>
      <Grid container direction={"row"} >
        <Grid item width={200}>
          <List>

            {sidebar.map((item) => {
              return (
                <Link to={item.path} key={Math.random()}>
                  <ListItem disablePadding >
                    <ListItemButton>
                      <ListItemIcon>
                        <item.icon />
                      </ListItemIcon>
                      <ListItemText primary={item.label} />
                    </ListItemButton>
                  </ListItem>
                </Link>
              )
            })}
          </List>

        </Grid>
        <Grid item width={750} paddingTop={2} marginLeft={2} >
          <Outlet />
        </Grid>
      </Grid>
    </Box>
  )
}