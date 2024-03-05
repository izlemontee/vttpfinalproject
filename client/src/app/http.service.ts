import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { lastValueFrom } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class HttpService {

  baseUrl: string = "http://localhost:8080/api"

  private httpClient = inject(HttpClient)

  constructor() { }

  getLoginUri(){
    return lastValueFrom(this.httpClient.get<any>(this.baseUrl+"/login"))
  }
}
