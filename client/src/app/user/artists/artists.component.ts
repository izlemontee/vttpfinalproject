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


  getArtists(term:string){
    this.artists = []
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
        
      }
      
    ).catch(
      error =>{
        alert("Something went wrong. Try again.")
      }
    )
  }

  submitArtists(){
    this.httpService.updateUserArtists(this.username,this.artists)
    this.deactivateArtistSelection.next()
  }

  cancel(){
    this.deactivateArtistSelection.next()
  }

}
