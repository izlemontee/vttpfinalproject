import { Component, Inject, OnDestroy, OnInit, Output, inject } from '@angular/core';
import { HttpService } from '../http.service';
import { DOCUMENT } from '@angular/common';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UserSession } from '../models';
import { SessionService } from '../session.service';
import { Subject } from 'rxjs';
import { SessioncomponentService } from '../sessioncomponent.service';
import { Store } from '@ngrx/store';
import { createUserSession } from '../state/state.actions';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent implements OnInit, OnDestroy{

  private httpService  = inject(HttpService)
  private router = inject(Router)
  private fb = inject(FormBuilder)
  private session = inject(SessionService)
  private sessionComponent = inject(SessioncomponentService)
  private store = inject(Store)
  loginUri!: string

  clientid!:string
  loginForm !: FormGroup
  passwordHidden: boolean = true
  usernameExists: boolean = true
  usernamePasswordMatch : boolean=true 

  @Output()
  loginSubject = new Subject<any>
  
  ngOnInit(): void {
      this.loginForm = this.createForm()
      this.session.initialiseLoginButton()
  }

  login(){
    const userSession = this.loginForm.value as UserSession
    this.httpService.checkUserExists(userSession.username).then(
      ()=>{
        this.usernameExists = true
      }
    ).catch(
      ()=>{
        this.usernameExists = false
      }
    )
    // if all works
    this.httpService.usernamePasswordMatch(userSession).then(
      (response)=>{
        this.usernamePasswordMatch=true
        const sessionForStorage: UserSession={
          username:userSession.username,
          id:response.id
        }
        const session = response.id
        const username = userSession.username
        this.session.addSession(sessionForStorage)
        const payload={
          username:username,
          id:session,
        }
        this.store.dispatch(createUserSession(payload))
        // this.sessionComponent.updateUser({session, username})
        this.router.navigate([''])
      }
    ).catch(
      ()=>{
        this.usernamePasswordMatch=false
      }
    )
  }

  createForm(){
    return this.fb.group({
      username: this.fb.control<string>('', Validators.required),
      password: this.fb.control<string>('',Validators.required)
    })
  }

  togglePasswordVisibility(){
    this.passwordHidden = !this.passwordHidden
  }
  ngOnDestroy(): void {
      this.session.disableLoginButton()
  }

}
