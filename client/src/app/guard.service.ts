import { Injectable, OnInit, inject } from '@angular/core';
import { Store, select } from '@ngrx/store';
import { selectAllUsers } from './state/state.selectors';
import { firstValueFrom, last, lastValueFrom, map } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class GuardService{

  loggedIn: boolean =false

  private store = inject(Store)

  constructor() { 
    this.changeLoginStatus()
  }

  isLoggedIn():boolean{
    // this.store.select(selectAllUsers).pipe(
    //   map(response =>{
    //     if(response != null){
    //       if(!this.usernameOrIdEmpty(response.username,response.id)){
    //         console.log("logged in")
    //         this.loggedIn = true
    //       }else{
    //         console.log("logged out")
    //         this.loggedIn = false
    //       }
    //     }
    //     else{
    //       console.log("logged out")
    //       this.loggedIn = false
    //     }
    //     return this.loggedIn
    //   })
    // ).subscribe({
      
   
    // })
    firstValueFrom(this.store.select(selectAllUsers)).then(
      response=>{
        console.log("response:",response)
        if(response != null){
          if(!this.usernameOrIdEmpty(response.username, response.id)){
            console.log("logged in")
            this.loggedIn = true
            return this.loggedIn
          }
          else{
            this.loggedIn = false

            return this.loggedIn
          }

        }
        else{
          this.loggedIn = false
          return this.loggedIn
        }
      }
    )
    return this.loggedIn
  }

  getLoginPromise(){
    return firstValueFrom(this.store.select(selectAllUsers))
  }

  getLoginObservable(){
    return this.store.select(selectAllUsers)
  }

  changeLoginStatus(){
    this.store.select(selectAllUsers).subscribe({
      next:(response)=>{
        console.log("userngrx",response)
        if(response != null){
          if(!this.usernameOrIdEmpty(response.username, response.id)){
            this.loggedIn = true
          }
          else{
            this.loggedIn = false
          }
        }
        else{
          this.loggedIn = false
        }
      }
    })

  }


  usernameOrIdEmpty(username:string, id:string):boolean{
    const usernameEmpty: boolean = (username.trim().length==0)
    const idEmpty: boolean = (id.trim().length==0)
    return usernameEmpty || idEmpty

  }


}
