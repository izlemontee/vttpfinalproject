import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpService } from '../../http.service';
import { User } from '../../models';
import { Store } from '@ngrx/store';
import { selectAllUsers } from '../../state/state.selectors';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent implements OnInit{

  private activatedRoute = inject(ActivatedRoute)
  private httpService = inject(HttpService)
  private store = inject(Store)

  usernameInProfile!:string

  user!:User

  // checks if this profile is the current user's profile, otherwise have an add option
  isMyProfile: boolean = false

  constructor(){

  }

  ngOnInit(): void {
      this.usernameInProfile = this.activatedRoute.snapshot.params['username']
      this.getUserProfile()
      this.checkUsernameWithStore()
  }


  getUserProfile(){
    this.httpService.getUserProfile(this.usernameInProfile).then(
      response =>{
        console.log(response)
        this.user = response as User
        console.log(this.user)
      }
    )
  }

  checkUsernameWithStore(){
    this.store.select(selectAllUsers).subscribe({
      next:(response)=>{
        console.log("check username", response)
        if(response.username==this.usernameInProfile){
          console.log(true)
          this.isMyProfile = true
        }
        else{
          this.isMyProfile = false
        }
      }
    })
  }



}
