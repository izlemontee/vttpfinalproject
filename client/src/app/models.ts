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
    id?:string
}