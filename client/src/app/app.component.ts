import { Component, OnInit, inject } from '@angular/core';
import { SessionService } from './session.service';
import { HttpService } from './http.service';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { UserSession } from './models';

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

  username!:string
  id!:string
  sessionSubscribe!: Observable<UserSession[]>
  loginButtonSubscribe!: Observable<void>
  loggedIn: boolean = false
  inLogin : boolean = false

  ngOnInit(): void {
      this.getUsername()
      this.subscribeToSession()
  }

  getUsername(){
    this.session.getSession().then(
      response =>{
        if(response.length>0){
          this.username = response[0].username
          this.id = response[0].id!
          this.loggedIn = true
        }
      }
    )
  }

  subscribeToSession(){
    this.sessionSubscribe = this.session.sessionObservable
    this.sessionSubscribe.subscribe({
      next:(response)=>{
        console.log('before processing response')
        console.log("response:",response)
        this.username = response[0].username
        this.id = response[0].id!
        this.loggedIn = true
        
      }
    })

  }

  subscribeToDisableLoginButton(){
    this.loginButtonSubscribe = this.session.forLoginObservable
    this.loginButtonSubscribe.subscribe({
      next:()=>{
        this.inLogin = false
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

  
}
