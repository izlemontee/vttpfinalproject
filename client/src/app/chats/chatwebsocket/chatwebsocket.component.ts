import { Component, Input, OnChanges, OnInit, Output, SimpleChanges, inject } from '@angular/core';
import { environment } from '../../../environments/environment';
import { HttpService } from '../../http.service';
import { Subject } from 'rxjs';


export const TYPE_SESSION_CHATS = "session_chats"

@Component({
  selector: 'app-chatwebsocket',
  templateUrl: './chatwebsocket.component.html',
  styleUrl: './chatwebsocket.component.css'
})
export class ChatwebsocketComponent implements OnInit, OnChanges{

  private httpService = inject(HttpService)

  webSocketUrl : string=environment.websocket_url
  ws!:WebSocket

  @Input()
  username!:string

  @Output()
  chatIdSubject = new Subject<string>

  ngOnInit(): void {

  }

  ngOnChanges(changes: SimpleChanges): void {
      if(this.username && this.username.trim().length>0){
        // console.log("changed: ", this.username)
        this.connectToWebsocket()
      }
  }

  connectToWebsocket(){
    if("WebSocket" in window){
      this.ws = new WebSocket(this.webSocketUrl)
      this.ws.onopen = this.onWebSocketOpen.bind(this)
      // this.ws.onmessage = this.onWebSocketMessage.bind(this)
      this.ws.onmessage = this.onReceive.bind(this)
      this.ws.onclose = this.onWebSocketClose.bind(this)

      this.ws.onerror = (error) =>{
        console.error("Websocket error", error)
    }
  }
}

sendUsernameToServer(){
  if(this.ws && this.ws.readyState === WebSocket.OPEN){
    const payload={
      type:TYPE_SESSION_CHATS,
      username:this.username
    }
    // console.log('sending to server')
    this.ws.send(JSON.stringify(payload))
  }
  else{
    // console.error("failed to send message. might still be connecting")
  }
}

  // using the native websocket library
  onWebSocketOpen(){
    // console.log("chatwebsocket sending...")
    this.sendUsernameToServer()

  }

  onReceive(event:MessageEvent<any>){
    try{
      this.getIdOfLatestChat()

    }catch(error){
      // console.error("error parsing message ", error)
    }

  }

  onWebSocketClose(){
    // console.log('websocket closed')
  }
  disconnectToWebSocket(){
    if(this.ws){
      this.ws.close()
      // console.log("closed")
    }
  }

  getIdOfLatestChat(){
    this.httpService.getIdOfLatestChat(this.username).then(
      (response)=>{
        const id = response.id
        this.chatIdSubject.next(id)
      }
    )
  }


}
