import { Injectable, inject } from '@angular/core';
import { Store, createAction, createFeatureSelector, createReducer, createSelector, on, props } from '@ngrx/store';
import { UserSession } from './models';
import { createUserSession, loadUserSession } from './state/state.actions';
import { Observable } from 'rxjs';


export interface UserState{
  user: UserSession|null
}

export const INIT_STATE: UserState={
  user:null
}

@Injectable({
  providedIn: 'root'
})
export class UserstateService {

  constructor() { }

  private store = inject(Store)


  getSessionDetails = createAction('[User] Get Session Details')

  clearSession = createAction('[User] Clear Session');

  // userReducer = createReducer(
  //   INIT_STATE,
  //   on(this.createUserSession, (state:any, {username, id}) => ({
  //     ...state,
  //     user: {username, id}
  //   })),
  //   on(this.getSessionDetails, state => state),
  //   on(this.clearSession, state => ({... state, user:null}))
  // )

  selectUserState = createFeatureSelector<UserState>('user')

  selectUser = createSelector(
    this.selectUserState,
    state => state.user
  )

  selectUsername = createSelector(
    this.selectUser,
    user=> user?.username
  )

  selectId = createSelector(
    this.selectUser,
    user=> user?.id
  )

  userSession(username:string, id:string){
    const payload ={
      username:username,
      id:id,
      loggedIn:true
    }
    // takes in the payload as an args
    this.store.dispatch(createUserSession(payload))
  }

  loadUser(){
    this.store.dispatch(loadUserSession())
  }



}
