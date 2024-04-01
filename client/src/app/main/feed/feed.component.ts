import { Component, OnInit, inject } from '@angular/core';
import { HttpService } from '../../http.service';
import { Store } from '@ngrx/store';
import { selectAllUsers } from '../../state/state.selectors';
import { Post } from '../../models';

@Component({
  selector: 'app-feed',
  templateUrl: './feed.component.html',
  styleUrl: './feed.component.css'
})
export class FeedComponent implements OnInit{

  private httpService = inject(HttpService)
  private store = inject(Store)

  skip:number = 0
  username!:string
  id!:string
  posts:Post[] =[]

  ngOnInit(): void {
    this.getUsername()
      
  }

  getUsername(){
    this.store.select(selectAllUsers).subscribe({
      next:(response)=>{
        if(response != null){
          this.username = response.username
          this.id = response.id
          this.httpService.getFeed(this.username, this.skip).then(
            (response)=>{
             
              this.posts = response as Post[]
              console.log(this.posts)
            }
          )
          
        }
      }
    })
  }

}
