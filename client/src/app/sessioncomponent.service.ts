import { Injectable } from '@angular/core';
import { UserSession } from './models';
import { ComponentStore } from '@ngrx/component-store';
import { Observable } from 'rxjs';


const INIT_STATE: UserSession={
  username:'',
  id:''
}

@Injectable({
  providedIn: 'root'
})
export class SessioncomponentService extends ComponentStore<UserSession>{

  constructor() {   
    super(INIT_STATE)
  }


  readonly updateUser = this.updater((state, { session, username }:{session:string, username:string}) => ({
    ...state,
    id: session, // Corrected assignment
    username: username
    // return {username:username, id:session}
  }));

  // readonly updateUser = this.updater<UserSession>(
  //   (state, { session, username }:{session:string, username:string}) => (

  //   return {username:username, id:session}
  // ));


  user$ = this.select<UserSession>(state => state);

  readonly deleteSession = this.updater((state)=>({
    ...state,
    id:undefined,
    username:undefined!
  }))
  
  


}
