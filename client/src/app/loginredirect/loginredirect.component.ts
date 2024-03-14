import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpService } from '../http.service';
import { SessionService } from '../session.service';

@Component({
  selector: 'app-loginredirect',
  templateUrl: './loginredirect.component.html',
  styleUrl: './loginredirect.component.css'
})
export class LoginredirectComponent implements OnInit{

  private activatedRoute = inject(ActivatedRoute)
  private httpService = inject(HttpService)
  private router = inject(Router)
  private sessionService = inject(SessionService)


  tempId!: string
  username!:string

  ngOnInit(): void {
      this.tempId = this.activatedRoute.snapshot.queryParams['id']
      console.log("tempid",this.tempId)
      this.sessionService.getSession().then(
        response =>{
          this.username = response[0].username
          this.httpService.addAccessKeyToUser(this.tempId,this.username)
        }
      )

      this.router.navigate(['/setup'])
  }

}
