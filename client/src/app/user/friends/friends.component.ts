import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpService } from '../../http.service';
import { User, UserSearch } from '../../models';

@Component({
  selector: 'app-friends',
  templateUrl: './friends.component.html',
  styleUrl: './friends.component.css'
})
export class FriendsComponent implements OnInit{

  private activatedRoute = inject(ActivatedRoute)
  private httpService = inject(HttpService)

  usernameInProfile!:string

  skip: number = 0

  friends:UserSearch[]=[]

  ngOnInit(): void {
      this.usernameInProfile = this.activatedRoute.snapshot.params['username']
      if(this.usernameInProfile && this.usernameInProfile!=''){
        this.getFriends()
      }

  }

  getFriends(){
    this.httpService.getFriends(this.usernameInProfile, this.skip).then(
      (response)=>{
        for(let r of response){
          this.friends.push(r)
        }
        this.skip = this.friends.length
      }
    )
  }


}
