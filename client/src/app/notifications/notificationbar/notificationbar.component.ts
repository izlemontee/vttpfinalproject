import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { Store } from '@ngrx/store';
import { selectAllUsers } from '../../state/state.selectors';
import { HttpService } from '../../http.service';
import { environment } from '../../../environments/environment';


export const TYPE_SESSION_USERNAME = "session_username"

@Component({
  selector: 'app-notificationbar',
  templateUrl: './notificationbar.component.html',
  styleUrl: './notificationbar.component.css'
})
export class NotificationbarComponent implements OnInit{

  // webSocketUrl: string = "ws://localhost:8080/notif-websocket"
  // webSocketUrl : string="ws://prime-rate-production.up.railway.app/notif-websocket"
  webSocketUrl : string=environment.websocket_url


  myUsername!:string

  unreadCount: number = 0

  ws!:WebSocket
  private cdr = inject(ChangeDetectorRef)
  private store = inject(Store)
  private httpService = inject(HttpService)

  ngOnInit(): void {
      this.getUsernameFromStore()
  }

  getUsernameFromStore(){
    this.store.select(selectAllUsers).subscribe({
      next:(response)=>{
        this.myUsername = response.username
        if(response.username !=''){
          // console.log("connected")
          this.connectToWebsocket()
          this.getNumberOfUnreadNotifications()
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
        // console.error("Websocket error", error)
    }
  }
}
  // using the native websocket library
  onWebSocketOpen(){
    // console.log("websocket connected in app component")
    // console.log(this.webSocketUrl)
    this.sendUsernameToServer()

  }

  onWebSocketClose(){
    // console.log('websocket closed')
  }

  onWebSocketMessage(evt: {data:string}){
    let received_msg = JSON.parse(evt.data)
    // console.log("received_msg", received_msg)
  }

  onReceive(event:MessageEvent<any>){
    try{
      // console.log("message received")
      let receivedMsg = JSON.parse(event.data)
      let payload = receivedMsg.payload
      this.unreadCount = payload
      this.cdr.detectChanges()


    }catch(error){
      // console.error("error parsing message ", error)
    }

  }

  disconnectToWebSocket(){
    if(this.ws){
      this.ws.close()
      // console.log("closed")
    }
  }

  sendUsernameToServer(){
    if(this.ws && this.ws.readyState === WebSocket.OPEN){
      const payload={
        type:TYPE_SESSION_USERNAME,
        username:this.myUsername
      }

      this.ws.send(JSON.stringify(payload))
      // console.log("send username success")
    }
    else{
      // console.error("failed to send message. might still be connecting")
    }
  }

  getNumberOfUnreadNotifications(){
    this.httpService.getNumberOfUnreadNotifications(this.myUsername).then(
      (response)=>{
        this.unreadCount = response.count
      }
    )
  }
}
