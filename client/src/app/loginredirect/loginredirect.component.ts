import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { UserauthService } from '../userauth.service';
import { HttpService } from '../http.service';

@Component({
  selector: 'app-loginredirect',
  templateUrl: './loginredirect.component.html',
  styleUrl: './loginredirect.component.css'
})
export class LoginredirectComponent implements OnInit{

  private activatedRoute = inject(ActivatedRoute)
  private userAuth = inject(UserauthService)
  private httpService = inject(HttpService)

  code!:string
  clientid!:string

  ngOnInit(): void {
      // this.code = this.activatedRoute.snapshot.queryParams['code']
      // console.log(this.code)
      // this.httpService.getLoginUri().then(
      //   value=> this.clientid = value.clientid
      // ).then(
      //   value=>{
      //     console.log("value,",value)
      //     this.userAuth.getApiToken(value,this.code)
      //   }
      // )
      
  }

}
