import { Injectable } from '@angular/core';


export const POST_LIKE ="post_like"
export const POST_COMMENT ="post_comment"
export const NEW_FRIEND_REQUEST ="new_friend_request"
export const FRIEND_REQUEST_ACCEPTED ="friend_request_accepted"

@Injectable({
  providedIn: 'root'
})

export class NotificationstemplateService {

  
  constructor() { }

  createAddFriendNotification(friendUsername:string, myUsername:string){
    const text:string = friendUsername+" has added you as a friend."
    // use client side routing endpoints for the url
    const endpoint = '/requests'
    const payload={
      username:myUsername,
      text:text,
      url:endpoint,
      type:NEW_FRIEND_REQUEST
    }
    return payload
  }

  createRequestAcceptedNotification(friendUsername:string, myUsername:string){
    const text: string = friendUsername+" accepted your request."
    const endpoint = "/user/"+friendUsername
    const payload={
      username:myUsername,
      text:text,
      url:endpoint,
      type:FRIEND_REQUEST_ACCEPTED
    }
    return payload
  }

  createCommentNotification(content:string, to:string, from:string, id:string){
    const text:string = from+" commented on your post:\n"+content
    const endpoint = "/post/"+id
    const payload={
      username:to,
      text:text,
      url:endpoint,
      type:POST_COMMENT
    }
    return payload

  }

}
