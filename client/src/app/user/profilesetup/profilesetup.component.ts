import { Component, OnInit, inject } from '@angular/core';
import { HttpService } from '../../http.service';
import { SessionService } from '../../session.service';
import { Artist, User } from '../../models';
import { FormBuilder, FormGroup } from '@angular/forms';
import { first, last } from 'rxjs';

@Component({
  selector: 'app-profilesetup',
  templateUrl: './profilesetup.component.html',
  styleUrl: './profilesetup.component.css'
})
export class ProfilesetupComponent implements OnInit{

  private httpService = inject(HttpService)
  private session = inject(SessionService)
  private fb = inject(FormBuilder)

  username!:string
  artists: Artist[] = []

  user!:User

  profileForm !: FormGroup

  ngOnInit(): void {
    this.session.getSession().then(
      response =>{
        if(response.length>0){
          this.username = response[0].username
          console.log("username",this.username)
          this.setupInit(this.username)
         
        }
      }
    )
    this.profileForm = this.createForm()

   

  }

  createForm(){
    return this.fb.group({
      firstname: this.fb.control<string>(''),
      lastname : this.fb.control<string>(''),
      bio: this.fb.control<string>('')
    })
  }

  processForm(){
    const firstname = this.profileForm.value['firstname'].toLowerCase()
    const lastname = this.profileForm.value['lastname'].toLowerCase()
    const bio = this.profileForm.value['bio']
    const user: User = {
      firstName : firstname,
      lastName: lastname,
      bio:bio
    }
    this.httpService.updateUserProfile(user, this.username)
  }

  loginToSpotify(){
    this.httpService.getLoginUri().then(
      response =>{
        console.log(response)
        window.location.replace(response.uri)
      }
    )

  }

  refreshAccessToken(){
    this.session.getSession().then(
      response =>{
        if(response.length>0){
          const username = response[0].username
          this.httpService.refreshUserAccessKey(username)
        }
      }
    )
  }

  getTopArtists(){
    this.httpService.getTopArtists(this.username).then(
      response=>{
        for(let a of response.items){
          const artist: Artist = {
            name: a.name,
            external_url: a.external_urls.spotify,
            image: a.images[2].url,
            genres : a.genres
          }
          this.artists.push(artist)
        }
      }
    )
  }

  getUsername(){
    this.session.getSession().then(
      response=>{
        this.username = response[0].username
      }
    )
  }

  setupInit(username:string){
    this.httpService.initUserSetup(username).then(
      response=>{
        
        const user: User={
          firstName: response.firstName,
          lastName: response.lastName,
          bio: response.bio
        }
        this.user = user
        console.log(this.user)

      }
    )
  }

}
