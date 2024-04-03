import { AfterContentInit, Component, OnChanges, OnDestroy, OnInit, SimpleChanges, inject } from '@angular/core';
import { HttpService } from '../../http.service';
import { SessionService } from '../../session.service';
import { Artist, User } from '../../models';
import { FormBuilder, FormGroup } from '@angular/forms';
import { first, last } from 'rxjs';
import { Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { selectAllUsers } from '../../state/state.selectors';

@Component({
  selector: 'app-profilesetup',
  templateUrl: './profilesetup.component.html',
  styleUrl: './profilesetup.component.css'
})
export class ProfilesetupComponent implements OnInit, AfterContentInit,OnChanges, OnDestroy{

  private httpService = inject(HttpService)
  private session = inject(SessionService)
  private fb = inject(FormBuilder)
  private router = inject(Router)
  private store = inject(Store)
  

  username!:string
  sessionId!:string
  artists: Artist[] = []
  spotify_linked: boolean = true

  user:User={
    firstName:'',
    lastName:'',
    bio:''
  }

  profileForm !: FormGroup

  artistSelection: boolean = false
  instrumentSelection: boolean = false
  profilePictureUpload : boolean = false
  userInfoSetup : boolean = false
  manualArtistSelection: boolean = false

  allComponentsDeactivated: boolean = true

  ngOnInit(): void {
    this.store.select(selectAllUsers).subscribe({
      next:(response)=>{
        this.username = response.username
      }
    })
    
  }
  ngAfterContentInit(): void {
    this.profileForm = this.createForm()
    
  }

  ngOnChanges(changes: SimpleChanges): void {
      this.profileForm = this.createForm()
  }

  ngOnDestroy(): void {
      location.reload()
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
    ).catch(
      ()=>{
        alert("Something went wrong. Please try again.")
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

  activateUserSetup(){
    this.userInfoSetup = true
    this.artistSelection = false
    this.instrumentSelection = false
    this.profilePictureUpload = false
    this.manualArtistSelection = false

    this.allComponentsDeactivated = this.checkAllComponentsDeactivated()
  }

  deactivateUserSetup(){
    this.userInfoSetup = false
    this.allComponentsDeactivated = this.checkAllComponentsDeactivated()
  }

  getTopArtists(){
    this.artistSelection = true
    this.userInfoSetup = false
    this.instrumentSelection = false;
    this.profilePictureUpload = false
    this.manualArtistSelection = false

    this.allComponentsDeactivated = this.checkAllComponentsDeactivated()
  }
  deactivateArtistSelection(){
    this.artistSelection = false
    this.allComponentsDeactivated = this.checkAllComponentsDeactivated()
  }

  manuallySelectArtists(){
    this.manualArtistSelection = true
    this.instrumentSelection = false
    this.userInfoSetup = false
    this.artistSelection = false;
    this.profilePictureUpload = false

    this.allComponentsDeactivated = this.checkAllComponentsDeactivated()
  }

  deactivateManualArtistSelection(){
    this.manualArtistSelection = false
    this.allComponentsDeactivated = this.checkAllComponentsDeactivated()
  }

  addInstruments(){
    this.instrumentSelection = true
    this.userInfoSetup = false
    this.artistSelection = false;
    this.profilePictureUpload = false
    this.manualArtistSelection = false

    this.allComponentsDeactivated = this.checkAllComponentsDeactivated()
  }
  deactivateInstrumentAdd(){
    this.instrumentSelection = false
    this.allComponentsDeactivated = this.checkAllComponentsDeactivated()
  }

  uploadProfilePicture(){
    this.profilePictureUpload = true
    this.userInfoSetup = false
    this.artistSelection = false
    this.instrumentSelection = false
    this.manualArtistSelection = false

    this.allComponentsDeactivated = this.checkAllComponentsDeactivated()
  }

  deactivatePictureUpload(){
    this.profilePictureUpload = false
    this.allComponentsDeactivated = this.checkAllComponentsDeactivated()
  }

  getUsername(){
    this.session.getSession().then(
      response=>{
        this.username = response[0].username
      }
    )
  }



  checkAllComponentsDeactivated():boolean{
    const userinfoSetupDeactivated:boolean = this.userInfoSetup == false
    const artistSelectionDeactivated :boolean = this.artistSelection == false
    const profilePictureUploadDeactivated: boolean = this.profilePictureUpload ==false
    const instrumentSelectionDeactivated: boolean = this.instrumentSelection == false
    const manualArtistSelectionDeactivated : boolean = this.manualArtistSelection == false

    return (userinfoSetupDeactivated && artistSelectionDeactivated 
            && profilePictureUploadDeactivated && instrumentSelectionDeactivated
            && manualArtistSelectionDeactivated)
  }
}
