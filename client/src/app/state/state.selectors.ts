import { createSelector } from "@ngrx/store";
import { AppState } from "./app.state";
import { UserState } from "./state.reduce";

export const selectUser = (state:AppState)  => state.user

export const selectAllUsers = createSelector(
    selectUser,
    (state: UserState) => state
)