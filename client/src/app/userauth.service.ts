import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable, OnInit, inject } from '@angular/core';
import { lastValueFrom } from 'rxjs';
import { SearchParams } from './models';

@Injectable({
  providedIn: 'root'
})
export class UserauthService{

  private httpClient = inject(HttpClient)

  
  clientId!:string
  redirectUri: string = "http://localhost:4200/redirect"
  codeChallenge!:any

  scope: string = "user-read-private user-read-email"
  authUrl = new URL("https://accounts.spotify.com/authorize")
  // spotifyKeyUrl :string = "https://api.spotify.com/v1/api/token"
  spotifyKeyUrl: string="https://accounts.spotify.com/api/token"

  // the PKCE flow starts with generating a code verifier
  // generateRandomString = (length:number) => {
  //   const possible = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
  //   const values = crypto.getRandomValues(new Uint8Array(length));
  //   return values.reduce((acc, x) => acc + possible[x % possible.length], "");
  // }

  generateRandomString(length:number){
    const possible = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    const values = crypto.getRandomValues(new Uint8Array(length));
    return values.reduce((acc, x) => acc + possible[x % possible.length], "");
  }
  
  // call this to generate a random string with 64 characters long
  // codeVerifier  = this.generateRandomString(64);
  codeVerifier!:string
  constructor() { this.codeVerifier = this.generateRandomString(64)}

  


  // once the verifier code has been generated, transform(hash) it using SHA256 algorithm
  // this will be sent within the user authorisation request
 sha256 = async (plain:any) => {
    const encoder = new TextEncoder()
    const data = encoder.encode(plain)

    // window.crypto.subtle.digest generates the value using the SHA256 algo
    return window.crypto.subtle.digest('SHA-256', data)
  }


  // next, this returns the base64 representation of the digest just calculated with the sha256 function
  base64encode = (input:ArrayBuffer) => {
    return btoa(String.fromCharCode(...new Uint8Array(input)))
      .replace(/=/g, '')
      .replace(/\+/g, '-')
      .replace(/\//g, '_');
  }


  // put it all together to implement the code challenge generation
  implementCodeChallenge(){
    var hashed = this.sha256(this.codeVerifier).then(
      value=>{var codeChallenge:string = this.base64encode(value)
      this.codeChallenge = codeChallenge
      console.log('this.codechallenge',this.codeChallenge)}
    )
    // console.log('hashed', hashed)
    // var codeChallenge:string = this.base64encode(hashed)
    // console.log("codeChallenge: ", codeChallenge)
    //this.codeChallenge = codeChallenge

  }


  // to request user authorisation
  // this generates a PKCE code challenge and redirects to the spotify authorisation login page 
  requestUserAuth(){
    window.localStorage.setItem('code-verifier', this.codeVerifier)
    var hashed = this.sha256(this.codeVerifier).then(
      value=>{var codeChallenge:string = this.base64encode(value)
      this.codeChallenge = codeChallenge
      console.log('this.codechallenge',this.codeChallenge)
      const params={
        response_type: 'code',
        client_id: this.clientId,
        scope: this.scope,
        code_challenge_method: 'S256',
        code_challenge: this.codeChallenge,
        redirect_uri: this.redirectUri
      }
      console.log(params)
      this.authUrl.search = new URLSearchParams(params).toString();
      window.location.href = this.authUrl.toString();
      // // parse the URL to retrieve the code parameter
      // const urlParams = new URLSearchParams(window.location.search);
      // let code = urlParams.get('code')
      // console.log(code)
    }
    )

  }

  setup(clientId: string){
    this.clientId = clientId
    console.log("clientid", this.clientId)
    this.requestUserAuth()

  }

  getApiToken(clientid:string,code:string){
    let codeVerifier = localStorage.getItem('code-verifier')
    console.log("codeVerifier 1:",this.codeVerifier)
    console.log("clientid",clientid)
    // const headers = new HttpHeaders().set('Content-Type', 'application/x-www-form-urlencoded')
    const headers = new HttpHeaders({
      'Content-Type': 'application/x-www-form-urlencoded'
    });
    

    var searchParams: SearchParams={
      client_id:clientid,
      grant_type:'authorization_code',
      code:code,
      redirect_uri:this.redirectUri,
      code_verifier:this.codeVerifier
    }

    const body = new URLSearchParams();
    body.set('grant_type', 'authorization_code');
    body.set('code', code);
    body.set('redirect_uri', this.redirectUri);
    body.set('client_id', clientid);
    body.set('code_verifier', this.codeVerifier);
    console.log("payload:",searchParams)
    return lastValueFrom (this.httpClient.post<any>(this.spotifyKeyUrl,body,{headers}))
    
  }

  
  
}
