import { Component, Input, OnChanges, Output, SimpleChanges, inject } from '@angular/core';
import { Subject } from 'rxjs';
import { HttpService } from '../../../http.service';
import { Artist } from '../../../models';

@Component({
  selector: 'app-artistmanualresults',
  templateUrl: './artistmanualresults.component.html',
  styleUrl: './artistmanualresults.component.css'
})
export class ArtistmanualresultsComponent implements OnChanges{

  private httpService = inject(HttpService)

  skip:number = 0
  next!:string
  prev!:string

  results: Artist[]=[]

  @Input()
  artistName!:string

  @Output()
  artistSelection = new Subject<any>

  ngOnChanges(changes: SimpleChanges): void {
      if(this.artistName && this.artistName != ''){
        this.skip = 0
        this.searchArtistsByName()
      }
  }

  searchArtistsByName(){
    this.httpService.searchArtistsByName(this.artistName,this.skip).then(
      (response)=>{
          console.log(response)
          this.results = response.artists as Artist[]
          this.next = response.next
          this.prev = response.prev
          this.skip = this.results.length
          console.log("next",this.next)
          console.log("prev", this.prev)
      }
    ).catch(
      ()=>alert("Server error. Try again.")
    )
  }

  nextPage(){
    this.searchArtistByUrl(this.next)
  }
  prevPage(){
    this.searchArtistByUrl(this.prev)
  }

  searchArtistByUrl(url:string){
    this.httpService.searchArtistsUsingUrl(url).then(
      (response)=>{
          this.results = response.artists as Artist[]
          this.next = response.next
          this.prev = response.prev
          this.skip = this.results.length
      }
    ).catch(
      ()=>alert("Server error. Try again.")
    )
  }

  addArtist(artist:Artist){
    this.artistSelection.next(artist)
  }
}
