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
    genres:string[]
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