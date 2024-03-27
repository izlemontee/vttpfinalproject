import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UserCreate } from '../../models';
import {MatIconModule} from '@angular/material/icon';
import { HttpService } from '../../http.service';
import { SessionService } from '../../session.service';
import { Subject } from 'rxjs';

@Component({
  selector: 'app-usercreation',
  templateUrl: './usercreation.component.html',
  styleUrl: './usercreation.component.css'
})
export class UsercreationComponent implements OnInit, OnDestroy{

  private fb = inject(FormBuilder)
  private httpService = inject(HttpService)
  private session = inject(SessionService)

  creationForm !: FormGroup
  passwordHidden!: boolean

  usernameExists: boolean = false
  emailExists : boolean = false
  username!:string


  ngOnInit(): void {
      this.creationForm = this.createForm()
      this.passwordHidden = false
      this.session.initialiseSetupButton()

  }

  createForm(){
    return this.fb.group({
      username: this.fb.control<string>('',[Validators.required]),
      password: this.fb.control<string>('',[Validators.required]),
      email: this.fb.control<string>('', [Validators.required]),
      firstName : this.fb.control<string>('' , [Validators.required]),
      lastName : this.fb.control<string>('', [Validators.required])
    })
  }

  processForm(){
   
    const username = this.creationForm.value['username'].trim().toLowerCase()
    const password = this.creationForm.value['password'].trim()
    const email = this.creationForm.value['email'].trim().toLowerCase()
    const firstName = this.creationForm.value['firstName'].trim().toLowerCase()
    const lastName = this.creationForm.value['lastName'].trim().toLowerCase()
   
    const userCreate: UserCreate={
      username: username,
      password:password,
      email: email,
      firstName : firstName,
      lastName : lastName
    }
    this.httpService.checkUserExists(username).then(
      ()=>{
        this.usernameExists =true
        console.log('thrn')
      }
    ).catch(
      ()=>{
        this.usernameExists = false
        console.log('catch')
        this.httpService.createUser(userCreate).then(
        ()=>{
          console.log("ok")
          this.session.disableLoginButton()
      }
      )
    }
    )



    // this.httpService.createUser(userCreate).then
    // (()=>console.log("ok")).catch(
    //   error =>console.log(error)
    // )
    
  }
  ngOnDestroy(): void {
    this.session.disableLoginButton()
}
  togglePasswordVisibility(){
    this.passwordHidden = !this.passwordHidden
  }



}
