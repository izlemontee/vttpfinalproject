import { Component, OnInit, inject } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { UserSession } from '../models';
import { selectAllUsers } from '../state/state.selectors';
import { Store } from '@ngrx/store';
import { HttpService } from '../http.service';
import { SessionService } from '../session.service';
import { deleteSession } from '../state/state.actions';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent implements OnInit{

  private session = inject(SessionService)
  private router = inject(Router)
  private store = inject(Store)
  private httpService = inject(HttpService)

  username!:string
  id!:string
  sessionSubscribe!: Observable<UserSession>
  loginButtonSubscribe!: Observable<void>
  image!:string

  // for the initialisation
  loginEnabledSubscribe !:Observable<void>
  setupEnabledSubscribe !: Observable<void>
  loggedIn: boolean = false
  inLogin : boolean = false
  inCreateUser : boolean = false
  user$ = this.store.select(selectAllUsers)


  ngOnInit(): void {
    this.iniitaliseLoginButtonState()
    this.initaliseSetupButtonState()
    this.subscribeToDisableLoginButton()
    this.userNgRx()
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

    // this.loggedIn = false
    this.httpService.deleteSession(this.id)
    this.session.deleteSession(this.id)
    this.store.dispatch(deleteSession())
    // this.username = ""
    // this.id = ""
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
        // console.log("userngrx",response)
        if(response != null){
          if(!this.usernameOrIdEmpty(response.username, response.id)){
            this.username = response.username
            this.id = response.id
            this.loggedIn = true
            this.httpService.getUserImage(this.username).then(
              (response)=>{
                this.image = response.image
              }
            )
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
