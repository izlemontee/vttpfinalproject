import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { UserCreate } from '../../models';
import {MatIconModule} from '@angular/material/icon';
import { HttpService } from '../../http.service';

@Component({
  selector: 'app-usercreation',
  templateUrl: './usercreation.component.html',
  styleUrl: './usercreation.component.css'
})
export class UsercreationComponent implements OnInit{

  private fb = inject(FormBuilder)
  private httpService = inject(HttpService)

  creationForm !: FormGroup
  passwordHidden!: boolean

  usernameExists: boolean = false
  emailExists : boolean = false

  ngOnInit(): void {
      this.creationForm = this.createForm()
      this.passwordHidden = false
  }

  createForm(){
    return this.fb.group({
      username: this.fb.control<string>(''),
      password: this.fb.control<string>(''),
      email: this.fb.control<string>(''),
      firstName : this.fb.control<string>(''),
      lastName : this.fb.control<string>('')
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
        ()=>{console.log("ok")
      }
      )
    }
    )


    // this.httpService.createUser(userCreate).then
    // (()=>console.log("ok")).catch(
    //   error =>console.log(error)
    // )
    
  }

  togglePasswordVisibility(){
    this.passwordHidden = !this.passwordHidden
  }

}
