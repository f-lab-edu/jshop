import IUserInfo from "../types/IUserInfo";

export const UPDATE_USERINFO = "UPDATE_USERINFO" as const;
export const update_userinfo = (userInfo: IUserInfo) => {
    return {
        type: UPDATE_USERINFO,
        userInfo: userInfo
    }
}

export const UPDATE_USERINFO_TRIGGER = "UPDATE_USERINFO_TRIGGER" as const;
export const update_userinfo_trigger = () => {
    return {
        type: UPDATE_USERINFO_TRIGGER        
    }
}

export type Action =
    | ReturnType<typeof update_userinfo>
    | ReturnType<typeof update_userinfo_trigger>