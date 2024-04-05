export interface SearchParams{
    client_id:string,
    grant_type:string,
    code:string,
    redirect_uri:string,
    code_verifier:string
}

export interface UserCreate{
    username:string,
    password:string,
    email:string,
    firstName:string,
    lastName:string
}

export interface UserSession{
    username:string,
    password?:string,
    id?:string,
    loggedIn?:boolean
}

export interface Artist{
    name:string,
    image:string,
    external_url:string,
    genres:string[],
    id?:string
}

export interface User{
    firstName:string,
    lastName:string,
    bio:string,
    username?:string,
    artists?:Artist[],
    image ?: string
}

export interface UserSearch{
    username:string,
    firstName:string,
    lastName:string,
    image:string
}

export interface Request{
    username:string, 
    firstName:string,
    lastName:string,
    image:string,
    accepted: boolean,
    rejected:boolean
}

export interface Notification{
    username:string,
    text:string,
    url: string,
    id:number,
    type:string,
    read:boolean,
    timestamp:number
}

export interface Instrument{
    name:string,
    image:string,
    alt:string
}


export interface Comment{
    id?:string,
    username:string,
    content:string,
    timestamp?:number,
    post_id:string,
    profile_picture?:string
}

export interface Post{
    id?:string,
    username:string,
    content:string,
    timestamp?:number,
    has_picture:boolean,
    image_url?:string,
    profile_picture?:string,
    comments?:Comment[],
    number_of_comments?:number
}

export interface Chat{
    id:string,
    user1:string,
    user2:string,
    last_updated:number
}

export interface Message{
    sender:string,
    recipient:string,
    content:string,
    chat_id:string,
    timestamp?:number
}