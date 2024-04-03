import { Component, Input, OnInit, Output, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Subject } from 'rxjs';
import { Artist } from '../../../models';
import { HttpService } from '../../../http.service';

@Component({
  selector: 'app-artistmanual',
  templateUrl: './artistmanual.component.html',
  styleUrl: './artistmanual.component.css'
})
export class ArtistmanualComponent implements OnInit{

  private fb = inject(FormBuilder)
  private httpService = inject(HttpService)

  artistForm!: FormGroup

  name!:string
  artists: Artist[]=[]

  canSearch: boolean = true

  @Input()
  username!:string

  @Output()
  deactivateManualArtistSelection = new Subject<void>

  ngOnInit(): void {
      this.artistForm = this.createForm()
  }

  createForm(){
    return this.fb.group({
      name: this.fb.control<string>('', Validators.required)
    })
  }

  processForm(){
    this.name = this.artistForm.value['name']
  }

  newArtist(event:any){
    this.artists.push(event)
  }

  deleteArtist(index:number){
    this.artists.splice(index,1)
  }

  updateArtists(){
    this.httpService.updateArtistsManually(this.artists, this.username).then(
      ()=>{
        this.deactivateManualArtistSelection.next()
      }
    ).catch(
      ()=>{
        alert("Server error. Please try again later.")
      }
    )
  }


}
