import { createAction, props } from "@ngrx/store";

// this is the action to create the user session
export const createUserSession = createAction(
    '[User] Create User Session',
    props<{username:string, id:string}>()
  )

export const loadUserSession = createAction(
    '[User] Get Session Details'
)

export const loadUsers = createAction('[User] Load Users');