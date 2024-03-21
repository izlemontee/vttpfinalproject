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
  instruments: string[] =[]
  genres: string[]=[]

  // checks if this profile is the current user's profile, otherwise have an add option
  isMyProfile: boolean = false

  // check if i have added this person already, pending confirmation
  pendingRequest: boolean = false
  // check if this person is my friend
  isMyFriend: boolean = false
  // check if this person sent me a request
  addedMe :boolean = false

  myUsername!: string

  constructor(){

  }

  ngOnInit(): void {
      this.usernameInProfile = this.activatedRoute.snapshot.params['username']
      this.getUserProfile()
      this.getUserInstruments()
      this.checkUsernameWithStore()
      this.getGenres()
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

  getUserInstruments(){
    this.httpService.getUserInstruments(this.usernameInProfile).then(
      response =>{
        this.instruments = response.instruments
      }
    )
  }

  checkUsernameWithStore(){
    this.store.select(selectAllUsers).subscribe({
      next:(response)=>{
        this.myUsername = response.username
        if(response.username==this.usernameInProfile){
          console.log(true)
          this.isMyProfile = true
        }
        else{
          this.isMyProfile = false
          this.checkFriendStatus()
        }
      }
    })
  }

  getGenres(){
    this.httpService.getUserProfileGenres(this.usernameInProfile).then(
      (response)=>{
        this.genres = response.genres
      }
    )
  }

  addFriend(){
    this.httpService.addFriendRequest(this.myUsername, this.usernameInProfile).then(
      ()=>{
        this.checkFriendStatus()
      }
    )
  }

  deleteRequest(){
    this.httpService.deleteFriendRequest(this.myUsername, this.usernameInProfile).then(
      ()=>{
        this.checkFriendStatus()
      }
    )
  }

  checkFriendStatus(){
    this.httpService.checkFriendStatus(this.myUsername,this.usernameInProfile).then(
      (response)=>{
        console.log("friend status: ",response)
        this.pendingRequest = response.pending
        this.isMyFriend = response.friends
        this.addedMe = response.addedme
      }
    )
  }

  deleteFriend(){
    this.httpService.deleteFriend(this.usernameInProfile,this.myUsername).then(
      (response)=>{
        console.log("friend status: ",response)
        this.pendingRequest = false
        this.isMyFriend = false
        this.addedMe = false
      }
    )
  }



}
