import { Component, OnInit, inject } from '@angular/core';
import { HttpService } from '../../http.service';
import { SessionService } from '../../session.service';
import { Artist } from '../../models';

@Component({
  selector: 'app-profilesetup',
  templateUrl: './profilesetup.component.html',
  styleUrl: './profilesetup.component.css'
})
export class ProfilesetupComponent implements OnInit{

  private httpService = inject(HttpService)
  private session = inject(SessionService)

  username!:string
  artists: Artist[] = []

  ngOnInit(): void {
    this.session.getSession().then(
      response =>{
        if(response.length>0){
          this.username = response[0].username
        }
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
    console.log("here")
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
    //this.httpService.refreshUserAccessKey()
  }

  getTopArtists(){
    this.httpService.getTopArtists(this.username).then(
      response=>{
        for(let a of response.items){
          const artist: Artist = {
            name: a.name,
            external_url: a.external_urls.spotify,
            image: a.images[1].url,
            genres : a.genres
          }
          this.artists.push(artist)
        }
      }
    )
  }

}
