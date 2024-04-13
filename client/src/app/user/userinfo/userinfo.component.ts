import { Component, Input, OnChanges, OnInit, Output, SimpleChanges, inject } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { User } from '../../models';
import { HttpService } from '../../http.service';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';

@Component({
  selector: 'app-userinfo',
  templateUrl: './userinfo.component.html',
  styleUrl: './userinfo.component.css'
})
export class UserinfoComponent implements OnInit, OnChanges{

  profileForm !: FormGroup

  private httpService = inject(HttpService)
  private router = inject(Router)
  private fb = inject(FormBuilder)

  @Output()
  deactivateUserSetup = new Subject<void>

  @Input()
  username!:string

  pendingServer: boolean = false

  user:User={
    firstName:'',
    lastName:'',
    bio:''
  }

  ngOnInit(): void {
      this.profileForm = this.createForm()

      
      
  }

  ngOnChanges(changes: SimpleChanges): void {

      if(this.username && this.username != ''){
        // console.log("username in userinfo: ",this.username)
        this.setupInit(this.username)
      }
  }

  createForm(){
    // console.log(this.user)
    return this.fb.group({
      firstName: this.fb.control<string>(''),
      lastName : this.fb.control<string>(''),
      bio: this.fb.control<string>('')
    })
  }


  processForm(){
    this.pendingServer = true
    const firstname = this.profileForm.value['firstName'].toLowerCase()
    const lastname = this.profileForm.value['lastName'].toLowerCase()
    const bio = this.profileForm.value['bio']
    const user: User = {
      firstName : firstname,
      lastName: lastname,
      bio:bio
    }
    this.httpService.updateUserProfile(user, this.username).then(
      ()=>{
        alert("Profile updated!")
        this.profileForm.reset()
        this.deactivate()
      }
    ).catch(
      ()=>{
        alert("Something went wrong. Please try again.")
        this.pendingServer = false
      }
    )
  }

  setupInit(username:string){
    // console.log("setupinit username", username)
    this.httpService.initUserSetup(username).then(
      response=>{
        
        const user: User={
          firstName: response.firstName,
          lastName: response.lastName,
          bio: response.bio
        }
        this.user = user
        this.profileForm.patchValue({
          firstName: this.user.firstName,
          lastName: this.user.lastName,
          bio: this.user.bio
        })

      }
    )
  }


  deactivate(){
    this.deactivateUserSetup.next()
  }
}
