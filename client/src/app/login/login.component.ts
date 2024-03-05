import { Component, Inject, inject } from '@angular/core';
import { HttpService } from '../http.service';
import { DOCUMENT } from '@angular/common';
import { Router } from '@angular/router';
import { UserauthService } from '../userauth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {

  private httpService  = inject(HttpService)
  private document = inject(DOCUMENT)
  private router = inject(Router)
  private userAuth = inject(UserauthService)
  loginUri!: string

  clientid!:string


  login(){
    // this.httpService.getLoginUri().then(
    //   value=> this.clientid = value.clientid
    // ).then(
    //   value => this.userAuth.setup(value)
    // )
    // this.httpService.getLoginUri().then(
    //   value=>{
    //     console.log(value)
    //     this.loginUri = value.uri
    //     value = value.uri
    //   }
    // ).then(
    //   (value)=>window.location.href = this.loginUri
    // )
    const getSpotifyUserLogin = ()=>{
      fetch("http://localhost:8080/api/login")
      .then((response)=>response.text())
      .then((response)=>{
        window.location.replace(response)
      })
    }
    getSpotifyUserLogin()
    console.log("here")
  }

}
