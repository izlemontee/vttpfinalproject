import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { last, lastValueFrom, of } from 'rxjs';
import { Artist, Comment, Message, Post, User, UserCreate, UserSession } from './models';
import { environment } from '../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class HttpService {

  baseUrl: string = environment.server_url+"/api"
  friendBaseUrl: string = environment.server_url+"/friend"
  notificationBaseUrl: string = environment.server_url+"/notification"
  postBaseUrl: string = environment.server_url+"/post"
  messageBaseUrl: string = environment.server_url+"/message"

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
    // console.log(url)
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

  getNotifications(username:string, offset:number){
    const url = this.notificationBaseUrl+"/get"
    const params = new HttpParams().set("username",username).set("offset",offset)
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

  getUserImage(username:string){
    const url = this.baseUrl+'/image'
    const params = new HttpParams().set("username",username)
    return lastValueFrom(this.httpClient.get<any>(url,{params:params}))
  }

  getNumberOfFriends(username:string){
    const url = this.friendBaseUrl+'/number'
    const params = new HttpParams().set("username",username)
    return lastValueFrom(this.httpClient.get<any>(url,{params:params}))
  }

  addNewPost(post:Post){
    const url = this.postBaseUrl+"/new"
    return lastValueFrom(this.httpClient.post<any>(url, post))
  }

  getFeed(username:string, skip:number){
    const url = this.postBaseUrl+"/feed"
    const params = new HttpParams().set("username",username).set("skip", skip)
    return lastValueFrom(this.httpClient.get<any>(url,{params:params}))

  }
  
  getPostsByUser(username:string, skip:number){
    const url = this.postBaseUrl+"/posts"
    const params = new HttpParams().set("username",username).set("skip", skip)
    return lastValueFrom(this.httpClient.get<any>(url,{params:params}))
  }

  addNewComment(comment:Comment){
    const url = this.postBaseUrl+"/comment"
    return lastValueFrom(this.httpClient.post<Comment>(url,comment))
  }

  getPostById(id:string){
    const url = this.postBaseUrl+"/get"
    const params = new HttpParams().set("id",id)
    return lastValueFrom(this.httpClient.get<any>(url,{params:params}))
  }

  getCommentsByPostId(id:string, skip:number){
    const url = this.postBaseUrl+"/comments"
    const params = new HttpParams().set("skip", skip).set("id",id)
    return lastValueFrom(this.httpClient.get<any>(url,{params:params}))
  }

  getFriends(username:string, skip:number){
    const url = this.friendBaseUrl+"/myfriends"
    const params = new HttpParams().set("username", username).set("skip",skip)
    return lastValueFrom(this.httpClient.get<any>(url, {params:params}))
  }

  searchArtistsByName(name:string, offset:number){
    const url = this.baseUrl +"/searchartists"
    const params = new HttpParams().set("name",name).set("offset",offset)
    return lastValueFrom(this.httpClient.get<any>(url,{params:params}))
  }

  searchArtistsUsingUrl(apiUrl:string){

    apiUrl = apiUrl.replaceAll('"','')

    const payload = {
      url:apiUrl
    }
    // console.log(payload)
    const url = this.baseUrl +"/searchartists"
    return lastValueFrom(this.httpClient.post<any>(url,payload))
  }

  updateArtistsManually(artists:Artist[], username:string){
    const payload={
      username:username,
      artists:artists
    }
    const url = this.baseUrl+"/artistupdate"
    return lastValueFrom(this.httpClient.post<any>(url,payload))

  }

  searchForMusiciansByInstrument(instrument:string){
    const url = this.baseUrl + "/musiciansearch"
    const params = new HttpParams().set("instrument",instrument)
    return lastValueFrom(this.httpClient.get<any>(url, {params:params}))
  }

  getChats(username:string, offset:number){
    const params = new HttpParams().set("username", username).set("offset",offset)
    const url = this.messageBaseUrl+"/chats"
    return lastValueFrom(this.httpClient.get<any>(url, {params:params}))
  }

  // returns chat id
  openChat(user1:string, user2:string){
    const params = new HttpParams().set("user1", user1).set("user2",user2)
    const url = this.messageBaseUrl + "/startchat"
    return lastValueFrom(this.httpClient.get<any>(url,{params:params}))
  }

  getMessages(id:string, offset:number){
    const params = new HttpParams().set("id", id).set("offset",offset)
    const url = this.messageBaseUrl + "/messages"
    return lastValueFrom(this.httpClient.get<any>(url, {params:params}))

  }

  sendMessage(message: Message){
    const url = this.messageBaseUrl + "/send"
    return lastValueFrom(this.httpClient.post<any>(url, message))
  }

  readChat(username:string, id:string){
    const url = this.messageBaseUrl + "/read"
    const params = new HttpParams().set("username",username).set("id",id)
    return lastValueFrom(this.httpClient.get<any>(url,{params:params}))
  }

  unreadChat(username:string, id:string){
    const url = this.messageBaseUrl + "/unread"
    const params = new HttpParams().set("username",username).set("id",id)
    return lastValueFrom(this.httpClient.get<any>(url,{params:params}))
  }

  getChatInfo(username:string, id:string){
    const url = this.messageBaseUrl + "/chatinfo"
    const params = new HttpParams().set("username",username).set("id",id)
    return lastValueFrom(this.httpClient.get<any>(url,{params:params}))
  }

  getNumberOfUnreadChats(username:string){
    const url = this.messageBaseUrl + "/unreadcount"
    const params = new HttpParams().set("username",username)
    return lastValueFrom(this.httpClient.get<any>(url, {params:params}))
  }

  getIdOfLatestChat(username:string){
    const url = this.messageBaseUrl + "/id"
    const params = new HttpParams().set("username",username)
    return lastValueFrom(this.httpClient.get<any>(url, {params:params}))
  }

}
