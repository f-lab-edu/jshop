import { composeWithDevTools } from "@redux-devtools/extension";
import State from "../types/State";
import { Action, UPDATE_USERINFO, UPDATE_USERINFO_TRIGGER } from "./Action";

import { createStore } from 'redux';


const initState: State = {
  userInfo: {
    addresses: [],
    balance: 0,
    email: "",
    username: "",
    userType: ""
  },
  updateUserInfo: 0
}

function reducer(state: State = initState, action: Action) {
  const newState = {...state};

  switch(action.type) {
    case UPDATE_USERINFO:
      newState.userInfo = action.userInfo;
      break;

    case UPDATE_USERINFO_TRIGGER:
      newState.updateUserInfo++;
      break;
  }

  

  return newState;
}

const store = createStore(reducer, composeWithDevTools());
export {store};