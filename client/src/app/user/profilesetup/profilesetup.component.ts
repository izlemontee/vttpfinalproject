import { AfterContentInit, Component, OnChanges, OnInit, SimpleChanges, inject } from '@angular/core';
import { HttpService } from '../../http.service';
import { SessionService } from '../../session.service';
import { Artist, User } from '../../models';
import { FormBuilder, FormGroup } from '@angular/forms';
import { first, last } from 'rxjs';
import { Router } from '@angular/router';

@Component({
  selector: 'app-profilesetup',
  templateUrl: './profilesetup.component.html',
  styleUrl: './profilesetup.component.css'
})
export class ProfilesetupComponent implements OnInit, AfterContentInit,OnChanges{

  private httpService = inject(HttpService)
  private session = inject(SessionService)
  private fb = inject(FormBuilder)
  private router = inject(Router)

  username!:string
  artists: Artist[] = []
  spotify_linked: boolean = false

  user:User={
    firstName:'',
    lastName:'',
    bio:''
  }

  profileForm !: FormGroup

  artistSelection: boolean = false

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
    
  }
  ngAfterContentInit(): void {
    this.profileForm = this.createForm()
  }

  ngOnChanges(changes: SimpleChanges): void {
      this.profileForm = this.createForm()
  }

  createForm(){
    console.log(this.user)
    return this.fb.group({
      firstName: this.fb.control<string>(''),
      lastName : this.fb.control<string>(''),
      bio: this.fb.control<string>('')
    })
  }

  processForm(){
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
        this.router.navigate(['/user',this.username])
      }
    )
  }

  loginToSpotify(){
    this.httpService.getLoginUri().then(
      response =>{
        console.log(response)
        window.location.replace(response.uri)
      }
    )
  }


  getTopArtists(){
    this.artistSelection = true
  }
  deactivateArtistSelection(){
    this.artistSelection = false
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
        this.spotify_linked = response.spotify_linked
        this.user = user
        this.profileForm.patchValue({
          firstName: this.user.firstName,
          lastName: this.user.lastName,
          bio: this.user.bio
        })

      }
    )
  }

}
