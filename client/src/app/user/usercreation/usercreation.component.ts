import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UserCreate, UserSession } from '../../models';
import {MatIconModule} from '@angular/material/icon';
import { HttpService } from '../../http.service';
import { SessionService } from '../../session.service';
import { Subject } from 'rxjs';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { Store } from '@ngrx/store';
import { createUserSession } from '../../state/state.actions';

@Component({
  selector: 'app-usercreation',
  templateUrl: './usercreation.component.html',
  styleUrl: './usercreation.component.css'
})
export class UsercreationComponent implements OnInit, OnDestroy{

  private fb = inject(FormBuilder)
  private httpService = inject(HttpService)
  private session = inject(SessionService)
  private router = inject(Router)
  private dialog = inject(MatDialog)
  private store = inject(Store)

  creationForm !: FormGroup
  passwordHidden!: boolean


  usernameExists: boolean = false
  emailExists : boolean = false
  username!:string
  pendingServer : boolean = false


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
    this.pendingServer = true
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
        this.pendingServer = false
      }
    ).catch(
      ()=>{
        this.usernameExists = false

        this.httpService.createUser(userCreate).then(
        ()=>{     
          this.session.disableLoginButton()
          var userSession:UserSession={
            username:username,
            password:password
          }
          this.login(userSession)
          // this.router.navigate(['/'])
          // this.dialog.closeAll()
      }
      ).catch(
        ()=>{
          this.pendingServer = false
          alert("User creation failed. Please try again.")
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

  // auto log in after successful registration
  login(userSession:UserSession){
    this.httpService.usernamePasswordMatch(userSession).then(
      (response)=>{
        const sessionForStorage: UserSession = {
          username: userSession.username,
          id: response.id
        }
        this.session.addSession(sessionForStorage)
        const payload = {
          username:userSession.username,
          id:response.id,
          loggedIn:true
        }
        this.store.dispatch(createUserSession(payload))
        this.dialog.closeAll()
        this.router.navigate([''])
      }
    )
  }



}
