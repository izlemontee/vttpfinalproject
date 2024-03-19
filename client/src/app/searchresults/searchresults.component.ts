import { Component, OnChanges, OnInit, SimpleChanges, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { UserSearch } from '../models';
import { HttpService } from '../http.service';

@Component({
  selector: 'app-searchresults',
  templateUrl: './searchresults.component.html',
  styleUrl: './searchresults.component.css'
})
export class SearchresultsComponent implements OnInit{

  private activatedRoute = inject(ActivatedRoute)
  private httpService = inject(HttpService)

  searchTerm!:string

  searchResults: UserSearch[] =[]

  ngOnInit(): void {

    this.activatedRoute.queryParams.subscribe(
      params=>{
        this.searchTerm = params['searchTerm']
        this.httpService.searchUser(this.searchTerm).then(
          response=>{
            if(response.length>0){
              this.searchResults = response
            }
            else{
              this.searchResults = []
            }
          }
        )
      }
    )
      // this.searchTerm = this.activatedRoute.snapshot.queryParams['searchTerm']
      // console.log(this.searchTerm)
      // this.httpService.searchUser(this.searchTerm).then(
      //   response=>{
      //     console.log(response)
      //     if(response.length>0){
      //       this.searchResults = response
      //     }
      //   }
      // )
  }

}
