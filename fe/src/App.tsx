import { BrowserRouter, Route, Routes } from "react-router-dom";
import styled from "styled-components";
import Main from "./pages/Main";
import Login from "./pages/Login";
import Join from "./pages/Join";

const Wrapper = styled.div`
  width: 100%;
  height: 100%;
`

function App() {
  return (
    <Wrapper>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Main/>} />
          <Route path="/login" element={<Login/>} />
          <Route path="/join" element={<Join/>} />
        </Routes>
      </BrowserRouter>
    </Wrapper>
  );
}

export default App;
