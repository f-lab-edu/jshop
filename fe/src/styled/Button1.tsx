import styled from "styled-components";
import design from "../config/design.json";

const Button1 = styled.div<{bgColor?: string; hoverBgColor?: string;}>`
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100px;
  height: 2.5em;
  font-size: 15px;
  border-radius: 10px;
  cursor: pointer;
  background: ${e => e.bgColor ? e.bgColor : "#efefef"};
  &:hover {
    background: ${e => e.hoverBgColor ? e.hoverBgColor : design.button1.hover.background};
  }
`

export default Button1;