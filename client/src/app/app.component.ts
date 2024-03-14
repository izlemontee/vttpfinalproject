import { Component, OnInit, inject } from '@angular/core';
import { SessionService } from './session.service';
import { HttpService } from './http.service';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { UserSession } from './models';
import { SessioncomponentService } from './sessioncomponent.service';
import { UserstateService } from './userstate.service';
import { Store, select } from '@ngrx/store';
import { selectAllUsers } from './state/state.selectors';
import { createUserSession, loadUserSession } from './state/state.actions';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit{
  title = 'client';

  private session = inject(SessionService)
  private httpService = inject(HttpService)
  private router = inject(Router)
  private sessionComponent = inject(SessioncomponentService)
  private sessionNgrx = inject(UserstateService)
  private store = inject(Store)

  username!:string
  id!:string
  sessionSubscribe!: Observable<UserSession>
  loginButtonSubscribe!: Observable<void>

  // for the initialisation
  loginEnabledSubscribe !:Observable<void>
  setupEnabledSubscribe !: Observable<void>
  loggedIn: boolean = false
  inLogin : boolean = false
  inCreateUser : boolean = false
  user$ = this.store.select(selectAllUsers)

  

  ngOnInit(): void {
      this.getUsername()
      this.store.dispatch(loadUserSession())
      // this.subscribeToSession()
      this.iniitaliseLoginButtonState()
      this.initaliseSetupButtonState()
      this.subscribeToDisableLoginButton()
      // this.subscribeToSessionComponent()
      // this.userNgRx()
  }

  getUsername(){
    this.session.getSession().then(
      response =>{
        if(response.length>0){
          this.username = response[0].username
          this.id = response[0].id!
          const username:string = response[0].username
          const session:string = response[0].id!
          const id:string = response[0].id!
          this.sessionComponent.updateUser({session, username})
          // this.sessionNgrx.createUserSession({username, id})
          const payload={
            username:username,
            id:id,
          }
          this.store.dispatch(createUserSession(payload))
          this.userNgRx()

        }
      }
    )
  }

  subscribeToSessionNgrx(){
    // this.store.pipe(
    //   select(this.sessionNgrx.selectUsername),
    //   // select(this.sessionNgrx.selectId),
    // ).subscribe(({username, id})=>{
    //   this.username = username
    //   this.id = id
    // })
  }

  // subscribeToSession(){
  //   this.sessionSubscribe = this.session.sessionObservable
  //   this.sessionSubscribe.subscribe({
  //     next:(response)=>{
  //       console.log('before processing response')
  //       console.log("response:",response)
  //       this.username = response[0].username
  //       this.id = response[0].id!
  //       this.loggedIn = true
        
  //     }
  //   })

  // }

  subscribeToSessionComponent(){
    this.sessionSubscribe = this.sessionComponent.user$
    this.sessionSubscribe.subscribe({
      next:(response=>{
        console.log("response:", response)
        this.username = response.username,
        this.id = response.id!
        if(this.username=='' && this.id==''){
        
          this.loggedIn=false
          console.log(this.loggedIn)
        }
        else{
        this.loggedIn = true
        }
      })
    })
  }

  subscribeToDisableLoginButton(){
    this.loginButtonSubscribe = this.session.forLoginObservable
    this.loginButtonSubscribe.subscribe({
      next:()=>{
        this.inLogin = false
        this.inCreateUser = false
      }
    })
  }
  logout(){

    this.loggedIn = false
    this.httpService.deleteSession(this.id)
    this.session.deleteSession(this.id)
    this.username = ""
    this.id = ""
  }
  login(){
    this.inLogin = true
    this.router.navigate(['/login'])
  }
  createUser(){
    this.inCreateUser = true
    this.router.navigate(['/createuser'])
  }

  iniitaliseLoginButtonState(){
    this.loginEnabledSubscribe = this.session.enableLoginButton
    this.loginEnabledSubscribe.subscribe({
      next: ()=>{
        this.inLogin = true
      }
    })
  }

  initaliseSetupButtonState(){
    this.setupEnabledSubscribe = this.session.enableSetupButton
    this.setupEnabledSubscribe.subscribe({
      next: ()=>{
        this.inCreateUser = true
      }
    })
  }

  userNgRx(){
    this.store.select(selectAllUsers).subscribe({
      next:(response)=>{
        console.log("userngrx",response)
        if(response != null){
          if(!this.usernameOrIdEmpty(response.username, response.id)){
            this.username = response.username
            this.id = response.id
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
