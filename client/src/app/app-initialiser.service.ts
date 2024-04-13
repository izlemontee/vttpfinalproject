import { Injectable, inject } from '@angular/core';
import { SessionService } from './session.service';
import { Store } from '@ngrx/store';
import { createUserSession } from './state/state.actions';
import { selectAllUsers } from './state/state.selectors';

@Injectable({
  providedIn: 'root'
})
export class AppInitialiserService {

  constructor() { }

  private dexieSession = inject(SessionService)
  private store = inject(Store)

  initialise(){
    this.getUserInfo()
    this.getUserState()
    
  }

  getUserInfo():Promise<void>{
  
    return new Promise<void>((resolve, reject) =>{
      this.dexieSession.getSession().then(
        (response)=>{
          // console.log("response in initialiser", response)
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
          resolve();
          
        }
      )
    })
  }

  getUserState(){
    this.store.select(selectAllUsers).subscribe({
      next:(response)=>{
        // console.log("user state:",response)
      }
    })
  }


}
