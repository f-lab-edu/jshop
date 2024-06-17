import { AppBar, Box, Button, Drawer, IconButton, Stack, Toolbar, Typography } from "@mui/material";
import { styled, alpha } from '@mui/material/styles';
import { NavLink, Outlet } from "react-router-dom";
import { ShoppingCart, Menu, Home, Logout } from "@mui/icons-material";
import AccountCircle from "@mui/icons-material/AccountCircle";
import SearchIcon from '@mui/icons-material/Search';
import InputBase from '@mui/material/InputBase';
import isAuthenticated from "../utils/isAuthenticated";
import { useState } from "react";
import Sidebar from "./Sidebar";
import { goCart, goHome, goMyPage, goOrders, signIn } from "../utils/route";
import logout from "../utils/logout";


const Search = styled('div')(({ theme }) => ({
  position: 'relative',
  borderRadius: theme.shape.borderRadius,
  backgroundColor: alpha(theme.palette.common.white, 0.15),
  '&:hover': {
    backgroundColor: alpha(theme.palette.common.white, 0.25),
  },
  marginLeft: 0,
  width: '100%',
  [theme.breakpoints.up('sm')]: {
    marginLeft: theme.spacing(1),
    width: 'auto',
  },
}));

const SearchIconWrapper = styled('div')(({ theme }) => ({
  padding: theme.spacing(0, 2),
  height: '100%',
  position: 'absolute',
  pointerEvents: 'none',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
}));

const StyledInputBase = styled(InputBase)(({ theme }) => ({
  color: 'inherit',
  width: '100%',
  '& .MuiInputBase-input': {
    padding: theme.spacing(1, 1, 1, 0),
    // vertical padding + font size from searchIcon
    paddingLeft: `calc(1em + ${theme.spacing(4)})`,
    transition: theme.transitions.create('width'),
  },
}));

export default function Header() {

  const [auth, setAuth] = useState(isAuthenticated());
  const [openSidebar, setOpenSidebar] = useState(false);


  return (
    <AppBar position="static">
      <Drawer open={openSidebar} onClose={e => setOpenSidebar(false)}>
        <Sidebar></Sidebar>
      </Drawer>
      <Toolbar>
        <IconButton
          size="large"
          edge="start"
          color="inherit"
          aria-label="menu"
          sx={{ mr: 2 }}
          onClick={e => setOpenSidebar(true)}
        >
          <Menu />
        </IconButton>
        <IconButton
          size="large"
          edge="start"
          color="inherit"
          aria-label="menu"
          sx={{ mr: 2 }}
          onClick={goHome}
        >
          <Home />
        </IconButton>
        <Stack sx={{ flexGrow: 1, marginRight: 1 }}>
          <Search>
            <SearchIconWrapper>
              <SearchIcon />
            </SearchIconWrapper>
            <StyledInputBase
              placeholder="Search…"
              inputProps={{ 'aria-label': 'search' }}
            />
          </Search>
        </Stack>
        {auth ? <IconButton
          size="large"
          edge="end"
          color="inherit"
          aria-label="user"
          sx={{ mr: 2 }}
          onClick={goOrders}
        >
          <AccountCircle />
        </IconButton> : <Button color="inherit" onClick={signIn}>Sign in</Button>}
        <IconButton
        size="large"
        edge="end"
        color="inherit"
        aria-label="user"
        onClick={goCart}
        sx={{ mr: 2 }}>
          <ShoppingCart/>
        </IconButton>
        {auth && <IconButton
        size="large"
        edge="end"
        color="inherit"
        aria-label="user"
        onClick={logout}
        sx={{ mr: 2 }}>
          <Logout/>
        </IconButton>}        
      </Toolbar>
    </AppBar>
  )
}