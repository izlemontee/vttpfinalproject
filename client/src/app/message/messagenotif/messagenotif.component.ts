import { ChangeDetectorRef, Component, inject } from '@angular/core';
import { environment } from '../../../environments/environment';
import { Store } from '@ngrx/store';
import { HttpService } from '../../http.service';
import { selectAllUsers } from '../../state/state.selectors';
import { MessagenotifService } from '../../messagenotif.service';


export const TYPE_SESSION_MESSAGE = "session_message"

@Component({
  selector: 'app-messagenotif',
  templateUrl: './messagenotif.component.html',
  styleUrl: './messagenotif.component.css'
})
export class MessagenotifComponent {

  webSocketUrl : string=environment.websocket_url
  myUsername!:string
  unreadCount: number = 0
  ws!:WebSocket
  
  private cdr = inject(ChangeDetectorRef)
  private store = inject(Store)
  private httpService = inject(HttpService)
  private messageNotifService = inject(MessagenotifService)

  ngOnInit(): void {
      this.getUsernameFromStore()
  }

  getUsernameFromStore(){
    this.store.select(selectAllUsers).subscribe({
      next:(response)=>{
        this.myUsername = response.username
        if(response.username !=''){
          console.log("connected")
          this.connectToWebsocket()
          this.getNumberOfUnreadChats()
        }else{
          this.disconnectToWebSocket()
        }
       
      }
    })
  }

  connectToWebsocket(){
    if("WebSocket" in window){
      this.ws = new WebSocket(this.webSocketUrl)
      this.ws.onopen = this.onWebSocketOpen.bind(this)
      this.ws.onmessage = this.onWebSocketMessage.bind(this)
      this.ws.onmessage = this.onReceive.bind(this)
      this.ws.onclose = this.onWebSocketClose.bind(this)

      this.ws.onerror = (error) =>{
        console.error("Websocket error", error)
    }
  }
}
  // using the native websocket library
  onWebSocketOpen(){
    this.sendUsernameToServer()

  }

  onWebSocketClose(){
    // console.log('websocket closed')
  }

  onWebSocketMessage(evt: {data:string}){
    let received_msg = JSON.parse(evt.data)
  }

  onReceive(event:MessageEvent<any>){
    try{

      let receivedMsg = JSON.parse(event.data)
      let payload = receivedMsg.payload
      this.unreadCount = payload
      this.cdr.detectChanges()
      // this.getIdOfLatestChat()

    }catch(error){
      // console.error("error parsing message ", error)
    }

  }

  disconnectToWebSocket(){
    if(this.ws){
      this.ws.close()
      // console.log("closed")
    }else(
      console.error("No websocket connection")
    )
  }

  sendUsernameToServer(){
    if(this.ws && this.ws.readyState === WebSocket.OPEN){
      const payload={
        type:TYPE_SESSION_MESSAGE,
        username:this.myUsername
      }

      this.ws.send(JSON.stringify(payload))
    }
    else{
      // console.error("failed to send message. might still be connecting")
    }
  }

  getNumberOfUnreadChats(){
    this.httpService.getNumberOfUnreadChats(this.myUsername).then(
      (response)=>{
        this.unreadCount = response.unread
      }
    )
  }

  getIdOfLatestChat(){
    this.httpService.getIdOfLatestChat(this.myUsername).then(
      (response)=>{
        const id = response.id
        this.messageNotifService.messageSubject.next(id)
      }
    )
  }

}
