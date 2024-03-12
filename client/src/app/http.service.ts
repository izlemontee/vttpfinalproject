import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { last, lastValueFrom } from 'rxjs';
import { Artist, User, UserCreate, UserSession } from './models';

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

  getTopArtists(username:string, duration:string){
    const params = new HttpParams().set("username",username).set("duration",duration)
    return lastValueFrom(this.httpClient.get<any>(this.baseUrl+"/topartists",{params:params}))
  }

  initUserSetup(username:string){
    const url = this.baseUrl+"/setupinit/"+username
    return lastValueFrom(this.httpClient.get<any>(url))
  }

  updateUserProfile(user:User, username:string){
    const url = this.baseUrl+"/update/"+username
    return lastValueFrom(this.httpClient.post<any>(url, user))
  }

  updateUserArtists(username:string, artists:Artist[]){
    const url = this.baseUrl+"/update/"+username+"/artists"
    return lastValueFrom(this.httpClient.post<any>(url,artists))
  }

  getUserProfile(username:string){
    const url = this.baseUrl+"/user/"+username
    console.log(url)
    return lastValueFrom(this.httpClient.get<any>(url))
  }

  getUserInstruments(username:string){
    const url = this.baseUrl+"/"+username+"/instruments"
    return lastValueFrom(this.httpClient.get<any>(url))
  }

  updateUserInstruments(instruments: string[], username:string){
    const url = this.baseUrl+"/addinstruments/"+username
    var payload={
      instruments:instruments
    }
    return lastValueFrom(this.httpClient.post<any>(url,payload))
  }

  uploadUserProfilePicture(file:File, username:string){
    const url = this.baseUrl+"/profilepicture/"+username
    const formData = new FormData()
    formData.set("file", file)
    formData.set("title","profile pic")
    formData.set("text","sample text")

    return lastValueFrom(this.httpClient.post<any>(url, formData))
  }
}
