import { Component, inject } from '@angular/core';
import { HttpService } from '../../http.service';

@Component({
  selector: 'app-profilesetup',
  templateUrl: './profilesetup.component.html',
  styleUrl: './profilesetup.component.css'
})
export class ProfilesetupComponent {

  private httpService = inject(HttpService)

  loginToSpotify(){
    this.httpService.getLoginUri().then(
      response =>{
        console.log(response)
        window.location.replace(response.uri)
      }
    )
    console.log("here")
  }

}
