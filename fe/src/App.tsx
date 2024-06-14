import { BrowserRouter, Route, Routes } from "react-router-dom";
import styled from "styled-components";
import Main from "./pages/Main";
import Login from "./pages/Login";
import Join from "./pages/Join";
import DefaultLayout from "./layout/DefaultLayout";
import Test from "./pages/Test";
import { Box, CssBaseline } from "@mui/material";
import MyPage from "./layout/MyPage";
import Orders from "./pages/Orders";
import Cart from "./pages/Cart";
import EditProfile from "./pages/EditProfile";
import Profile from "./pages/Profile";
import { isEqual } from "lodash";
import { useEffect } from "react";
import { useSelector, useDispatch } from "react-redux";
import apiInstance from "./api/instance";
import { UPDATE_USERINFO } from "./redux/Action";
import IResponse from "./types/IResponse";
import IUserInfo from "./types/IUserInfo";
import State from "./types/State";
import Product from "./pages/Product";

function App() {

  const updateUserInfo = useSelector((state: State) => state.updateUserInfo, isEqual);

  const dispatch = useDispatch();
  
  useEffect(() => {
    getUserInfo();
  }, [updateUserInfo]);
  function getUserInfo() {
    /**
     * TODO address가 추가되면, 유저 페이지에서 업데이트하는 기능
     */
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

  return (
    <Box>
      <CssBaseline />
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/join" element={<Join />} />
          <Route path="/" element={<DefaultLayout />}>
            <Route index element={<Main />} />
            <Route path="test" element={<Test />} />
            <Route path="mypage" element={<MyPage />}>
              <Route index element={<EditProfile />} />
              <Route path="orders" element={<Orders />} />
              <Route path="cart" element={<Cart />} />
              <Route path="products" element={<Product />} />
            </Route>
          </Route>
        </Routes>
      </BrowserRouter>
    </Box>
  );
}

export default App;
