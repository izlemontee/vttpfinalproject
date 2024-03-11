import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpService } from '../../http.service';
import { User } from '../../models';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent implements OnInit{

  private activatedRoute = inject(ActivatedRoute)
  private httpService = inject(HttpService)

  usernameInProfile!:string

  user!:User

  constructor(){

  }

  ngOnInit(): void {
      this.usernameInProfile = this.activatedRoute.snapshot.params['username']
      this.getUserProfile()
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



}
