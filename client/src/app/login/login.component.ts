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
    this.httpService.getLoginUri().then(
      response =>{
        console.log(response)
        window.location.replace(response.uri)
      }
    )
    console.log("here")
  }

}
