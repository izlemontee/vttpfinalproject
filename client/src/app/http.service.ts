import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { last, lastValueFrom } from 'rxjs';
import { UserCreate, UserSession } from './models';

@Injectable({
  providedIn: 'root'
})
export class HttpService {

  baseUrl: string = "http://localhost:8080/api"

  private httpClient = inject(HttpClient)

  constructor() { }

  getLoginUri(){
    return lastValueFrom(this.httpClient.get<any>(this.baseUrl+"/authenticate"))
  }

  createUser(user:UserCreate){
    return lastValueFrom(this.httpClient.post<any>(this.baseUrl+"/createuser",user))
  }

  checkUserExists(username:string){
    const params = new HttpParams().set("username",username)
    return lastValueFrom(this.httpClient.get<any>(this.baseUrl+"/userexists",{params:params}))
  }

  usernamePasswordMatch(userSession:UserSession){
    return lastValueFrom(this.httpClient.post<any>(this.baseUrl+"/login",userSession))
  }

  deleteSession(id:string){
    const params = new HttpParams().set("id",id)
    return lastValueFrom(this.httpClient.delete<any>(this.baseUrl+"/logout", {params:params}))
  }

  addAccessKeyToUser(tempId:string, username:string){
    const body={
      tempId:tempId,
      username:username
    }

    return lastValueFrom (this.httpClient.post<any>(this.baseUrl+"/addaccesskey",body))

  }

  refreshUserAccessKey(username:string){
    const params = new HttpParams().set("username",username)
    return lastValueFrom(this.httpClient.get<any>(this.baseUrl+"/refresh",{params:params}))
  }

  getTopArtists(username:string){
    const params = new HttpParams().set("username",username)
    return lastValueFrom(this.httpClient.get<any>(this.baseUrl+"/topartists",{params:params}))
  }
}
