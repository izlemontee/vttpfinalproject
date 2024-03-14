import { Injectable, inject } from '@angular/core';
import { SessionService } from './session.service';
import { Store } from '@ngrx/store';
import { createUserSession } from './state/state.actions';

@Injectable({
  providedIn: 'root'
})
export class AppInitialiserService {

  constructor() { }

  private dexieSession = inject(SessionService)
  private store = inject(Store)

  initialise(){
    this.getUserInfo()
  }

  getUserInfo(){
    this.dexieSession.getSession().then(
      (response)=>{
        console.log("response in initialiser", response)
        var payload = {
          username:'',
          id:'',
          loggedIn:false
        }
        if(response.length>0){
          payload = {
            username:response[0].username,
            id: response[0].id!,
            loggedIn:true
          }
        }
        this.store.dispatch(createUserSession(payload))
      }
    )
  }


}
