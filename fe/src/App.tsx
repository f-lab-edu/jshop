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

function App() {


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
            </Route>
          </Route>
        </Routes>
      </BrowserRouter>
    </Box>
  );
}

export default App;
