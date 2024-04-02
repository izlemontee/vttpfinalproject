import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpService } from '../http.service';
import { Comment, Post } from '../models';

@Component({
  selector: 'app-postdisplay',
  templateUrl: './postdisplay.component.html',
  styleUrl: './postdisplay.component.css'
})
export class PostdisplayComponent implements OnInit{

  private activatedRoute = inject(ActivatedRoute)
  private httpService = inject(HttpService)
  postId!:string
  skip:number=0

  comments: Comment[]=[]
  post: Post={
    username:'',
    content:'',
    has_picture:false,
    number_of_comments:0
  }

  ngOnInit(): void {
    this.postId =  this.activatedRoute.snapshot.params['id']
    this.getPostById()
     
  }

  getPostById(){
    this.httpService.getPostById(this.postId).then(
      (response)=>{
        this.post = response as Post
        this.getComments()
      }
    )
  }

  getComments(){
    this.httpService.getCommentsByPostId(this.postId, this.skip).then(
      (response)=>{
        for(let r of response){
          this.comments.push(r)
        }
        this.skip=this.comments.length
      }
    )

  }

  addComment(event:any){
    this.comments.push(event)
    this.skip = this.comments.length

  }

}
