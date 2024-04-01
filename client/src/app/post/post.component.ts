import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Store } from '@ngrx/store';
import { selectAllUsers } from '../state/state.selectors';
import { Post } from '../models';
import { HttpService } from '../http.service';

@Component({
  selector: 'app-post',
  templateUrl: './post.component.html',
  styleUrl: './post.component.css'
})
export class PostComponent implements OnInit{


  private fb = inject(FormBuilder)
  private store = inject(Store)
  private httpService = inject(HttpService)

  postInput!: FormGroup
  username!:string
  id!:string

  ngOnInit(): void {
      this.postInput = this.createForm()
      this.getUsername()
  }

  createForm(){
    return this.fb.group({
      content:this.fb.control<string>('',[Validators.required, Validators.maxLength(1024)])
    })
  }

  processForm(){
    var content = this.postInput.value['content']
    console.log(content)
    console.log(this.username)
    if(!this.usernameEmpty()){
      const post: Post={
        username: this.username,
        content:content,
        has_picture: false
      }
      this.httpService.addNewPost(post).then(
        ()=>{
          location.reload()
        }
      ).catch(
        ()=>{
          alert("Post could not be posted. Try again.")
        }
      )
    }
    else{
      this.postInput.reset()
      alert("Error: username is empty.")
    }

  }

  getUsername(){
    this.store.select(selectAllUsers).subscribe({
      next:(response)=>{
        if(response != null){
          this.username = response.username
          this.id = response.id
        }
      }
    })
  }

  usernameOrIdEmpty(username:string, id:string):boolean{
    const usernameEmpty: boolean = (username.trim().length==0)
    const idEmpty: boolean = (id.trim().length==0)
    return usernameEmpty || idEmpty

  }

  usernameEmpty():boolean{
    console.log("usernameEmpty:", this.username)
    console.log("null? ", this.username!=null)
    console.log("empty?", this.username.trim() != '')
    return ((this.username==null) || (this.username.trim()==''))
  }
}
