import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpService } from '../../http.service';
import { Instrument, Post, User } from '../../models';
import { Store } from '@ngrx/store';
import { selectAllUsers } from '../../state/state.selectors';
import { NotificationstemplateService } from '../../notificationstemplate.service';
import { MatDialog } from '@angular/material/dialog';
import { ProfilesetupComponent } from '../profilesetup/profilesetup.component';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent implements OnInit{

  private activatedRoute = inject(ActivatedRoute)
  private httpService = inject(HttpService)
  private store = inject(Store)
  private notifTemplate = inject(NotificationstemplateService)
  private router = inject(Router)
  private dialog = inject(MatDialog)

  usernameInProfile!:string

  user!:User
  instruments: Instrument[] =[]
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

  numberOfFriends: number = 0

  skip:number=0

  posts:Post[]=[]

  constructor(){

  }

  ngOnInit(): void {
    this.activatedRoute.params.subscribe(
      (response)=>{
        this.usernameInProfile = response['username']
        this.getUserProfile()
        this.getUserInstruments()
        this.checkUsernameWithStore()
        this.getGenres()
        this.getNumberOfFriends()
        this.skip = 0
        this.getPosts()
      }
    )

  }


  getUserProfile(){
    this.httpService.getUserProfile(this.usernameInProfile).then(
      response =>{
        this.user = response as User
      }
    )
  }

  getUserInstruments(){
    this.httpService.getUserInstruments(this.usernameInProfile).then(
      response =>{
        for(let i of response.instruments){
          const imageName = i.replace(' ','_')
          const image = "https://izlemonteebucket.sgp1.digitaloceanspaces.com/images/"+imageName+".png"
          const instrument: Instrument= {
            name:i,
            image:image,
            alt:"https://izlemonteebucket.sgp1.digitaloceanspaces.com/images/note.png"
          }
          this.instruments.push(instrument)
        }
      }
    )
  }

  checkUsernameWithStore(){
    this.store.select(selectAllUsers).subscribe({
      next:(response)=>{
        this.myUsername = response.username
        if(response.username==this.usernameInProfile){
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
    const payload = this.notifTemplate.createAddFriendNotification(this.myUsername, this.usernameInProfile)
    this.httpService.addNotification(payload)
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

  setupUser(){
    this.dialog.open(ProfilesetupComponent)
  }

  getNumberOfFriends(){
    this.httpService.getNumberOfFriends(this.usernameInProfile).then(
      (response)=>{
        this.numberOfFriends = response.friends
      }
    )
  }

  getPosts(){
    this.httpService.getPostsByUser(this.usernameInProfile, this.skip).then(
      (response)=>{
        console.log("posts from user:", response)
        this.posts = response as Post[]
      }
    )
  }


}
