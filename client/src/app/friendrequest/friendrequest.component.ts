import { Component, OnInit, inject } from '@angular/core';
import { HttpService } from '../http.service';
import { Store } from '@ngrx/store';
import { selectAllUsers } from '../state/state.selectors';
import { Request, User } from '../models';
import { NotificationstemplateService } from '../notificationstemplate.service';

@Component({
  selector: 'app-friendrequest',
  templateUrl: './friendrequest.component.html',
  styleUrl: './friendrequest.component.css'
})
export class FriendrequestComponent implements OnInit{

  private httpService = inject(HttpService)
  private store = inject(Store)
  private notifTemplate = inject(NotificationstemplateService)

  username: string=''
  names:string[]=[]
  users:Request[]=[]

  // to disable the button while waiting for server
  pendingServer : boolean = false

  ngOnInit(): void {
      this.getUsernameForSession()
  }

  getUsernameForSession(){
    this.store.select(selectAllUsers).subscribe({
      next:(response)=>{
        this.username = response.username
        if(this.username!=''){
          this.httpService.getFriendRequests(this.username).then(
            (response)=>{
              // console.log(response)
              for(let i in response){
                const user = response[i].username
                this.names.push(user)
              }
              return this.names
            }
          ).then(
            (names)=>{
              for(let n of names){
                this.getUserInfo(n)
              }
            }
          )

        }
      }
    })
  }

  getUserInfo(username:string){
    this.httpService.getUserProfile(username).then(
      (response)=>{
        const username = response.username
        const firstName = response.firstName
        const lastName = response.lastName
        const image= response.image
        const user:Request={
          username:username,
          firstName:firstName,
          lastName:lastName,
          image:image,
          accepted:false,
          rejected:false
        }
        this.users.push(user)
      }
    )
  }

  acceptRequest(index:number){
    this.pendingServer = true
    const friend = this.username
    const username = this.users[index].username
    if(username!=''){
      this.httpService.acceptFriendRequest(username, friend).then(
        ()=>{
          const payload = this.notifTemplate.createRequestAcceptedNotification(friend,username)
          this.httpService.addNotification(payload)
          this.users[index].accepted = true
          this.pendingServer = false
        }
      ).catch(
        ()=>{alert("something went wrong.")
          this.pendingServer = false
        }
      )
    }
  }

  deleteRequest(index:number){
    this.pendingServer = true
    const friend = this.username
    const username = this.users[index].username
    if(username!=''){
      this.httpService.deleteFriendRequest(username, friend).then(
        ()=>{
          this.users[index].rejected = true
          this.pendingServer = false
        }
      ).catch(
        ()=>{alert("something went wrong.")
          this.pendingServer = false
        }
      )
    }
  }

}
