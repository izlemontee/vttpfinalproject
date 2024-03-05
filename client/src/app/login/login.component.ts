import { Component, Inject, OnDestroy, OnInit, Output, inject } from '@angular/core';
import { HttpService } from '../http.service';
import { DOCUMENT } from '@angular/common';
import { Router } from '@angular/router';
import { UserauthService } from '../userauth.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UserSession } from '../models';
import { SessionService } from '../session.service';
import { Subject } from 'rxjs';

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
        this.session.addSession(sessionForStorage)
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


  loginToSpotify(){
    this.httpService.getLoginUri().then(
      response =>{
        console.log(response)
        window.location.replace(response.uri)
      }
    )
    console.log("here")
  }

  togglePasswordVisibility(){
    this.passwordHidden = !this.passwordHidden
  }
  ngOnDestroy(): void {
      this.session.disableLoginButton()
  }

}
