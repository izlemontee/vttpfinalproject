import { AfterContentInit, Component, Input, OnInit, Output, inject } from '@angular/core';
import { HttpService } from '../../http.service';
import { SessionService } from '../../session.service';
import { FormBuilder } from '@angular/forms';
import { Artist } from '../../models';
import { Subject } from 'rxjs';

@Component({
  selector: 'app-artists',
  templateUrl: './artists.component.html',
  styleUrl: './artists.component.css'
})
export class ArtistsComponent {

  private httpService = inject(HttpService)
  private session = inject(SessionService)
  private fb = inject(FormBuilder)

  
  @Input()
  username!:string

  @Output()
  deactivateArtistSelection = new Subject<void>

  artists: Artist[] = []
  genres: string[]=[]

  term!:string
  pendingServer: boolean = false


  getArtists(term:string){
    this.pendingServer = true
    this.artists = []
    this.changeTerm(term)
    this.httpService.getTopArtists(this.username,term).then(
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
        this.pendingServer = false
      }
      
    ).catch(
      error =>{
        alert("Something went wrong. Try again.")
        this.pendingServer = false
      }
    )
    this.getGenres(term)
  }

  submitArtists(){
    this.pendingServer = true
    this.httpService.updateUserArtists(this.username,this.artists).then(
      ()=>{
        this.submitGenres()
        this.deactivateArtistSelection.next()
        this.pendingServer = false
      }
    ).catch(
      ()=>{
        alert("Sending to server failed. Please try again.")
      }
    )



  }

  cancel(){
    this.deactivateArtistSelection.next()
  }

  getGenres(term:string){
    this.httpService.getTopGenres(this.username,term).then(
      (response)=>{
        this.genres = response
      }
    )
  }

  submitGenres(){
    this.httpService.submitGenres(this.genres, this.username).then(
      ()=>{
        alert("Success!")
      }
    ).catch(
      ()=>{
        alert("Something went wrong. Please try again.")
      }
    )
  }

  changeTerm(term:string){
    switch(term){
      case "short_term":{
        this.term = "Short Term"
        break;}
      case "medium_term":{
        this.term = "Medium Term"
        break;}
      case "long_term":{
        this.term = "Long Term"
        break;}
    }

  }

}
