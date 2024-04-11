import { Component, Input, OnChanges, OnInit, Output, SimpleChanges, inject } from '@angular/core';
import { HttpService } from '../../http.service';
import { Message } from '../../models';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { Store } from '@ngrx/store';
import { selectAllUsers } from '../../state/state.selectors';
import { Subject } from 'rxjs';

@Component({
  selector: 'app-messaging',
  templateUrl: './messaging.component.html',
  styleUrl: './messaging.component.css'
})
export class MessagingComponent implements OnInit,OnChanges{

  @Input()
  chatId!:string

  @Input()
  recipient!:string
  recipientImage!:string

  @Output()
  chatIdSubject = new Subject<string>


  username!:string

  private httpService = inject(HttpService)
  private fb = inject(FormBuilder)
  private store = inject(Store)
  private document = inject(Document)
  
  messages: Message[] =[]
  messageDisplay: Message[] = []

  contentForm !: FormGroup

  scroller = this.document.querySelector("#message-scroller")

  scrollHeight:number = 300

  ngOnInit(): void {
    this.contentForm = this.createForm()
    this.getUsername()

  }

  createForm(){
    return this.fb.group({
      content: this.fb.control<string>('',Validators.required)
    })

  }

  getUsername(){
    this.store.select(selectAllUsers).subscribe({
      next:(response)=>{
        if(response.username!=''&& response.username!=null){
          this.username = response.username
          if(this.chatIdNotEmpty()){
            this.getMessages()
            this.getChatInfo()
          }
        }
      }
    })
  }

  ngOnChanges(changes: SimpleChanges): void {

      if(this.chatIdNotEmpty()){
        this.recipient = ''
        this.recipientImage=''
        this.messages = []
        this.getChatInfo()
        this.getMessages()
      }
 
 
  
      
  }

  chatIdNotEmpty(){
    return this.chatId && this.chatId.length>0
  }

  getMessages(){
    this.httpService.getMessages(this.chatId, this.messages.length).then(
      (response)=>{
        for (let r of response){
          this.messages.push(r)
        }
        this.messageDisplay = this.messages.slice().reverse()
      }
    )
  }

  sendMessage(event:any){
    if (!event.shiftKey){
      event.preventDefault()
    }
    const content = this.contentForm.value['content'].trim()

    const payload: Message = {
      sender: this.username,
      recipient: this.recipient,
      content: content,
      chat_id: this.chatId
    }
    this.httpService.sendMessage(payload).then(
      (response)=>{
        this.contentForm.reset()
        // this.messages.unshift(response as Message)
        // this.messageDisplay = this.messages.slice().reverse()
        // this.chatIdSubject.next(this.chatId)
      }

    ).catch(
      ()=>alert("Message could not be sent. Try again.")
    )

  }

  addLatestMessage(message:Message){
    this.messages.unshift(message)
    console.log("message: ", message)
    this.chatIdSubject.next(message.chat_id)
  }

  sendMessageWithButton(){
    const content = this.contentForm.value['content'].trim()
    const payload: Message = {
      sender: this.username,
      recipient: this.recipient,
      content: content,
      chat_id: this.chatId
    }
    this.httpService.sendMessage(payload).then(
      (response)=>{
        this.contentForm.reset()
        this.messages.unshift(response as Message)
        this.messageDisplay = this.messages.slice().reverse()
        this.chatIdSubject.next(this.chatId)
      }

    ).catch(
      ()=>alert("Message could not be sent. Try again.")
    )
  }

  scrollUp(event:any){
    var offsetHeight = event.target.offsetHeight
    var scrollTop = event.target.scrollTop
    var scrollHeight = event.target.scrollHeight
    var scrollValue = scrollTop+(scrollHeight-this.scrollHeight)
    if(scrollValue<=50){
      this.getMessages()
    }
  }

  getChatInfo(){
    this.httpService.getChatInfo(this.username, this.chatId).then(
      (response)=>{
        this.recipient = response.username_display
        this.recipientImage = response.image
      }
    )
  }



}
