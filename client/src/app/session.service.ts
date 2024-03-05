import { Injectable } from '@angular/core';
import Dexie from 'dexie';
import { UserSession } from './models';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SessionService extends Dexie{
  
  sessions: Dexie.Table<UserSession,string>
  sessionObservable = new Subject<UserSession[]>
  forLoginObservable = new Subject<void>

  constructor() { 
    super("sessionDB")
    this.version(1).stores({
      sessions: 'id'
    })
    this.sessions = this.table('sessions')
  }

  addSession(userSession:UserSession){
    this.sessions.add(userSession)
    console.log("userSession in addSession",userSession)
    this.getSession().then(
      (value)=>{
        this.sessionObservable.next(value)
      }
    )
    console.log('added')
  }

  getSession(){
    return this.sessions.toArray()
  }

  deleteSession(id:string){
    this.sessions.delete(id)
  }

  disableLoginButton(){
    this.forLoginObservable.next()
  }
}
