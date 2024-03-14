import { Component, OnInit, inject } from '@angular/core';
import { Store } from '@ngrx/store';
import { selectAllUsers } from '../state/state.selectors';

@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
  styleUrl: './main.component.css'
})
export class MainComponent implements OnInit{

  private store = inject(Store)

  loggedIn:boolean = false
  username!:string
  id!:string

  ngOnInit(): void {
      this.getLoginStatus()
  }

  getLoginStatus(){
    this.store.select(selectAllUsers).subscribe({
      next:(response)=>{
        if(response != null){
          if(!this.usernameOrIdEmpty(response.username, response.id)){
            this.username = response.username
            this.id = response.id
            this.loggedIn = true
          }
          else{
            this.loggedIn = false
          }
        }
        else{
          this.loggedIn = false
        }
      }
    })
  }



  usernameOrIdEmpty(username:string, id:string):boolean{
    const usernameEmpty: boolean = (username.trim().length==0)
    const idEmpty: boolean = (id.trim().length==0)
    return usernameEmpty || idEmpty

  }

}
