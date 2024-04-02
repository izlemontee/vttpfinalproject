import { Component, Input, OnInit, Output, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Store } from '@ngrx/store';
import { selectAllUsers } from '../state/state.selectors';
import { HttpService } from '../http.service';
import { Comment } from '../models';
import { Subject } from 'rxjs';

@Component({
  selector: 'app-comment',
  templateUrl: './comment.component.html',
  styleUrl: './comment.component.css'
})
export class CommentComponent implements OnInit{

  private store = inject(Store)
  private fb = inject(FormBuilder)
  private httpService = inject(HttpService)

  username!:string
  image!:string
  commentForm!:FormGroup

  @Input()
  post_id:string=''

  @Output()
  commentOutput = new Subject<Comment>

  ngOnInit(): void {
      this.getUsername()
      this.commentForm = this.createForm()
  }

  createForm(){
    return this.fb.group({
      content: this.fb.control<string>('', Validators.required)
    })
  }

  processForm(){
    if(this.username == '' || !this.username){
      alert("No username detected. Try again.")
    }
    else{
      const content = this.commentForm.value['content']
      const comment: Comment={
        username: this.username,
        content: content,
        post_id: this.post_id
      }
      this.httpService.addNewComment(comment).then(
        (response)=>{
          this.commentOutput.next(response)
          this.commentForm.reset()
        }
      ).catch(
        ()=>{
          alert("Comment couldn't be posted. Try again later.")
        }
      )
    }
  }

  getUsername(){
    this.store.select(selectAllUsers).subscribe({
      next:(response)=>{
        // console.log("userngrx",response)
        if(response != null){
          if(!this.usernameOrIdEmpty(response.username, response.id)){
            this.username = response.username

            this.httpService.getUserImage(this.username).then(
              (response)=>{
                this.image = response.image
              }
            )
          }
        }
      }
    })
  }

  usernameOrIdEmpty(username:string, id:string):boolean{
    const usernameEmpty: boolean = (username.trim().length==0)
    const idEmpty: boolean = (id.trim().length==0)
    return usernameEmpty || idEmpty

  }

}
