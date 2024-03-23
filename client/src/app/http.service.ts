import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { last, lastValueFrom } from 'rxjs';
import { Artist, User, UserCreate, UserSession } from './models';

@Injectable({
  providedIn: 'root'
})
export class HttpService {

  baseUrl: string = "http://localhost:8080/api"
  friendBaseUrl: string = "http://localhost:8080/friend"
  notificationBaseUrl: string = "http://localhost:8080/notification"

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

  getTopGenres(username:string, duration:string){
    const params = new HttpParams().set("username",username).set("duration",duration)
    return lastValueFrom(this.httpClient.get<any>(this.baseUrl+"/genres",{params:params}))
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

  getUserProfileGenres(username:string){
    const url = this.baseUrl+"/profile/genres"
    const params = new HttpParams().set("username",username)
    return lastValueFrom(this.httpClient.get<any>(url,{params:params}))
  }

  submitGenres(genres:string[], username:string){
    const url = this.baseUrl+"/profile/genres/"+username
    const params = new HttpParams().set("username",username)
    return lastValueFrom(this.httpClient.post<any>(url,genres))

  }

  searchUser(searchTerm:string){
    const url = this.baseUrl+"/search/user"
    const params = new HttpParams().set("searchTerm", searchTerm)
    return lastValueFrom(this.httpClient.get<any>(url,{params:params}))
  }

  checkFriendStatus(username:string, friend:string){
    const params = new HttpParams().set("username",username).set("friend", friend)
    const url = this.friendBaseUrl+"/status"
    return lastValueFrom(this.httpClient.get<any>(url,{params:params}))
  }

  addFriendRequest(username:string, friend:string){
    const params = new HttpParams().set("username",username).set("friend", friend)
    const url = this.friendBaseUrl+"/add"
    return lastValueFrom(this.httpClient.get<any>(url,{params:params}))
  }

  deleteFriendRequest(username:string, friend:string){
    const params = new HttpParams().set("username",username).set("friend", friend)
    const url = this.friendBaseUrl+"/deleterequest"
    return lastValueFrom(this.httpClient.delete<any>(url,{params:params}))
  }

  getFriendRequests(username:string){
    const params = new HttpParams().set("username",username)
    const url = this.friendBaseUrl+"/requests"
    return lastValueFrom(this.httpClient.get<any>(url,{params:params}))
  }

  acceptFriendRequest(username:string, friend:string){
    const params = new HttpParams().set("username",username).set("friend",friend)
    const url = this.friendBaseUrl+"/accept"
    return lastValueFrom(this.httpClient.get<any>(url,{params:params}))
  }

  deleteFriend(username:string, friend:string){
    const params = new HttpParams().set("username",username).set("friend",friend)
    const url = this.friendBaseUrl+"/delete"
    return lastValueFrom(this.httpClient.delete<any>(url,{params:params}))
  }

  addNotification(payload:any){
    const url = this.notificationBaseUrl+"/add"
    return lastValueFrom(this.httpClient.post<any>(url, payload))

  }

  getNotifications(username:string){
    const url = this.notificationBaseUrl+"/get"
    const params = new HttpParams().set("username",username)
    return lastValueFrom(this.httpClient.get<any>(url, {params:params}))

  }

  readNotification(id:number){
    const params = new HttpParams().set("id",id)
    const url = this.notificationBaseUrl+"/read"
    return lastValueFrom(this.httpClient.get<any>(url,{params:params}))

  }

  getNumberOfUnreadNotifications(username:string){
    const url = this.notificationBaseUrl+"/unreadnumber"
    const params = new HttpParams().set("username",username)
    return lastValueFrom(this.httpClient.get<any>(url,{params:params}))
  }
}
