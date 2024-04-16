import { Component, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { Subject } from 'rxjs';
import { Message } from '../../models';
import { environment } from '../../../environments/environment';


export const TYPE_SESSION_MESSAGE_LIVE = "session_message_live"

@Component({
  selector: 'app-messagelive',
  templateUrl: './messagelive.component.html',
  styleUrl: './messagelive.component.css'
})
export class MessageliveComponent implements OnChanges{

  @Input()
  chatId!:string

  @Output()
  messageSubject = new Subject<Message>

  ws!:WebSocket
  webSocketUrl : string=environment.websocket_url

  ngOnChanges(changes: SimpleChanges): void {
    if(this.ws){
      this.ws.close()
    }
    this.connectToWebsocket()
  }

  connectToWebsocket(){
    if("WebSocket" in window){
      this.ws = new WebSocket(this.webSocketUrl)
      this.ws.onopen = this.onWebSocketOpen.bind(this)
      this.ws.onmessage = this.onReceive.bind(this)
      this.ws.onclose = this.onWebSocketClose.bind(this)

      this.ws.onerror = (error) =>{
        console.error("Websocket error", error)
    }
  }
}

    // using the native websocket library
    onWebSocketOpen(){
      this.sendIdToServer()
  
    }
  
    onWebSocketClose(){
      // console.log('websocket closed')
    }

    onReceive(event:MessageEvent<any>){
      try{
        // console.log(event)
        let receivedMsg = JSON.parse(event.data)
        let payload = receivedMsg.payload
        var message = JSON.parse(payload) as Message
        this.messageSubject.next(message)

  
      }catch(error){
        // console.error("error parsing message ", error)
      }
  
    }

    disconnectToWebSocket(){
      if(this.ws){
        this.ws.close()
        // console.log("closed")
      }else{
        // console.error("No websocket connection")
    }
    }

    sendIdToServer(){
      if(this.ws && this.ws.readyState === WebSocket.OPEN){
        const payload={
          type:TYPE_SESSION_MESSAGE_LIVE,
          id: this.chatId
        }
  
        this.ws.send(JSON.stringify(payload))
      }
      else{
        // console.error("failed to send message. might still be connecting")
      }
    }

}
