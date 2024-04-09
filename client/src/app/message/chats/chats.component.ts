import { Component, OnInit, inject } from '@angular/core';
import { HttpService } from '../../http.service';
import { Store } from '@ngrx/store';
import { selectAllUsers } from '../../state/state.selectors';
import { Chat } from '../../models';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-chats',
  templateUrl: './chats.component.html',
  styleUrl: './chats.component.css'
})
export class ChatsComponent implements OnInit{

  private httpService = inject(HttpService)
  private store = inject(Store)
  private activatedRoute = inject(ActivatedRoute)
  private router = inject(Router)

  username!:string
  id!:string
  chats: Chat[]=[]

  recipient!:string


  chatId!:string
  ngOnInit(): void {
      this.getUsername()
  }

  getUsername(){
    this.store.select(selectAllUsers).subscribe({
      next:(response)=>{
        if(response != null){
          if(!this.usernameOrIdEmpty(response.username, response.id)){
            this.username = response.username
            this.id = response.id
            this.getChats()
            this.getChatById()
          }
        }
      }
    })
  }

  getChatById(){
    this.activatedRoute.params.subscribe(
      (response)=>{
        this.chatId = response['id']
        console.log("username and chat id", this.username, this.chatId)
        if(!this.userNameOrChatIdEmpty()){
          this.readChat()
          this.getChatInfo()
        }
      }
    )
  }

  usernameOrIdEmpty(username:string, id:string):boolean{
    const usernameEmpty: boolean = (username.trim().length==0)
    const idEmpty: boolean = (id.trim().length==0)
    return usernameEmpty || idEmpty

  }

  userNameOrChatIdEmpty(){
    const chatIdEmpty = !this.chatId || this.chatId.trim().length==0
    const usernameEmpty = !this.username || this.username.trim().length==0
    return chatIdEmpty || usernameEmpty
  }

  getChats(){
    this.httpService.getChats(this.username, this.chats.length).then(
      (response)=>{
        for(let r of response){
          // has "read" as a boolean
          this.chats.push(r)
        }
      }
    )
  }

  readChat(){
    this.httpService.readChat(this.username, this.chatId).then(
      ()=>{
      }
    ).catch(
      ()=>{
        alert("Server error reading chat. Try again later.")
      }
    )
  }

  goToChat(id:string){
    this.router.navigate(['/chats',id])
  }

  getChatInfo(){
    this.httpService.getChatInfo(this.username, this.chatId).then(
      (response)=>{
        this.recipient = response.username_display
      }
    )
  }

}
