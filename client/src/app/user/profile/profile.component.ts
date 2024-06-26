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

  isLoading: boolean = true

  // disable the buttons while waiting to senda  request to the server
  pendingServer: boolean = false

  constructor(){

  }

  ngOnInit(): void {
    this.activatedRoute.params.subscribe(
      (response)=>{
        this.resetPage()
        this.usernameInProfile = response['username']
        this.getUserProfile()
        this.getUserInstruments()
        this.checkUsernameWithStore()
        this.getGenres()
        this.getNumberOfFriends()
        this.skip = 0
        this.getPosts()
        this.isLoading = false
      }
    )

  }
  
  // resets the page when browsing another profile
  resetPage(){
    this.isLoading = true
    this.posts = []
    this.instruments = []
    this.user = {
      username:'',
      firstName:'',
      lastName:'',
      bio:'',
      image:''
    }
    
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
    this.pendingServer = true
    this.pendingRequest = true
    this.httpService.addFriendRequest(this.myUsername, this.usernameInProfile).then(
      ()=>{
        this.checkFriendStatus()
        const payload = this.notifTemplate.createAddFriendNotification(this.myUsername, this.usernameInProfile)
        this.httpService.addNotification(payload)
        this.pendingRequest = false
        this.pendingServer = false
      }
    ).catch(
      ()=>{
        alert("Error sending request. Try again.")
        this.pendingRequest = false
      }
    )

  }

  acceptRequest(){
    this.pendingServer = true
    const friend = this.myUsername
    const username = this.usernameInProfile
    this.httpService.acceptFriendRequest(username, friend).then(
      ()=>{
        const payload = this.notifTemplate.createRequestAcceptedNotification(friend, username)
        this.httpService.addNotification(payload)
        this.pendingServer = false
        location.reload()
      }
    ).catch(
      ()=>{
        alert("Something went wrong. Please try again.")
        this.pendingServer = false
      }
    )
  }

  rejectRequest(){
    this.pendingServer = true
    const friend = this.myUsername
    const username = this.usernameInProfile
    this.httpService.deleteFriendRequest(username, friend)
    this.pendingServer = false
    location.reload()
    
  }

  deleteRequest(){
    this.pendingServer = true
    this.pendingRequest = true
    this.httpService.deleteFriendRequest(this.myUsername, this.usernameInProfile).then(
      ()=>{
        this.checkFriendStatus()
        this.pendingRequest = false
        this.pendingServer = false
      }
    )
  }

  checkFriendStatus(){
    this.httpService.checkFriendStatus(this.myUsername,this.usernameInProfile).then(
      (response)=>{
        // console.log("friend status: ",response)
        this.pendingRequest = response.pending
        this.isMyFriend = response.friends
        this.addedMe = response.addedme
      }
    )
  }

  deleteFriend(){
    this.pendingRequest = true
    this.httpService.deleteFriend(this.usernameInProfile,this.myUsername).then(
      (response)=>{
        this.pendingRequest = false
        this.isMyFriend = false
        this.addedMe = false
        this.pendingRequest = false
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
    this.skip = this.posts.length
    this.httpService.getPostsByUser(this.usernameInProfile, this.skip).then(
      (response)=>{
        for(let r of response){
          this.posts.push(r as Post)
        }
      }
    )
  }

  openChat(){
    this.pendingRequest = true
    this.httpService.openChat(this.myUsername, this.usernameInProfile).then(
      (response)=>{
        const chatId = response.id
        this.router.navigate(['/chats',chatId])
      }
    ).catch(
      ()=>{
        alert("Server error.")
      }
    )
  }

}
