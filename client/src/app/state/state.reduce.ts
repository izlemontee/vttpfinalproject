import { createReducer, on } from "@ngrx/store";
import { UserSession } from "../models";
import { createUserSession, deleteSession, loadUserSession } from "./state.actions";


// reducers are to modify the state when the actions are dispatched
export interface UserState{
    username:string,
    id:string
    loggedIn: boolean
}

// initial user state
export const INIT_STATE: UserState = {
    username:'',
    id:'',
    loggedIn : false
} 

export const userReducer = createReducer(
    INIT_STATE,
    // create user session
    on(createUserSession, (state:any, {username, id, loggedIn}) =>({
        ...state,
        // returns the state
        // user:{username, id, loggedIn}
        username:username,
        id:id,
        loggedIn:loggedIn,
        
    }),
        
    ),
    on(deleteSession, (state:any) =>({
        ...state,
        username:'',
        id:'',
        loggedIn:false
    }) ),
    on(loadUserSession, (state:any) => ({
        ...state
    })

    )
)