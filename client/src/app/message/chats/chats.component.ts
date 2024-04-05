import { Component, OnInit, inject } from '@angular/core';
import { HttpService } from '../../http.service';
import { Store } from '@ngrx/store';
import { selectAllUsers } from '../../state/state.selectors';
import { Chat } from '../../models';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-chats',
  templateUrl: './chats.component.html',
  styleUrl: './chats.component.css'
})
export class ChatsComponent implements OnInit{

  private httpService = inject(HttpService)
  private store = inject(Store)
  private activatedRoute = inject(ActivatedRoute)

  username!:string
  id!:string
  chats: Chat[]=[]


  chatId!:string
  ngOnInit(): void {
      this.getUsername()
      this.getChatById()
  }

  getUsername(){
    this.store.select(selectAllUsers).subscribe({
      next:(response)=>{
        if(response != null){
          if(!this.usernameOrIdEmpty(response.username, response.id)){
            this.username = response.username
            this.id = response.id
            this.getChats()
          }
        }
      }
    })
  }

  getChatById(){
    this.activatedRoute.params.subscribe(
      (response)=>{
        console.log("id: ",response['id'])
      }
    )
  }

  usernameOrIdEmpty(username:string, id:string):boolean{
    const usernameEmpty: boolean = (username.trim().length==0)
    const idEmpty: boolean = (id.trim().length==0)
    return usernameEmpty || idEmpty

  }

  getChats(){
    this.httpService.getChats(this.username, this.chats.length).then(
      (response)=>{
        console.log("chats: ", response)
      }
    )
  }

}
