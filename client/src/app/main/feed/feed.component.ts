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

  feedError: boolean = false

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
             this.feedError = false
              this.posts = response as Post[]
              
            }
          ).catch(
            ()=>this.feedError = true
          )
          
        }
      }
    })
  }

  onScroll(){
    this.skip = this.posts.length
    this.httpService.getFeed(this.username, this.skip).then(
      (response)=>{
        for(let r of response){
          this.posts.push(r)
        }
      }
    )
  }

  addComment(event:any, index:number){
    console.log("event: ",event)
    console.log("index: ", index)
    var comments = this.posts[index].comments
    comments?.push(event)

  }

}
