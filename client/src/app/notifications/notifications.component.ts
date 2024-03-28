import { Component, OnInit, inject } from '@angular/core';
import { Store } from '@ngrx/store';
import { selectAllUsers } from '../state/state.selectors';
import { HttpService } from '../http.service';
import { Notification } from '../models';
import { Router } from '@angular/router';

@Component({
  selector: 'app-notifications',
  templateUrl: './notifications.component.html',
  styleUrl: './notifications.component.css'
})
export class NotificationsComponent implements OnInit{
  private store = inject(Store)
  private httpService = inject(HttpService)
  private router = inject(Router)
  
  username!: string

  notifications : Notification[]=[]

  notificationOffset:number = 0

  ngOnInit(): void {
      this.getUsernameFromStore()
  }

  getUsernameFromStore(){
    this.store.select(selectAllUsers).subscribe({
      next:(response)=>{
        if(response.username!=''&& response.username!=null){
          this.username = response.username
          this.getNotifications()
        }
      }
    })
  }

  getNotifications(){
    this.httpService.getNotifications(this.username, this.notificationOffset).then(
      (response)=>{
        console.log(response)
        for (let r of response){
          r = r as Notification
          this.notifications.push(r)
          this.notificationOffset++
        }
        // this.notifications  = response as Notification[]

      }
    )
  }

  readNotification(notification:Notification){
    if(!notification.read){
      this.httpService.readNotification(notification.id).then(
        ()=>{
          this.router.navigate([notification.url])
        }
      ).catch(
        ()=>alert("Error.")
      )
    }
    else{
      this.router.navigate([notification.url])
    }
  
  }

  onScroll(){
    this.getNotifications()
  }



}
