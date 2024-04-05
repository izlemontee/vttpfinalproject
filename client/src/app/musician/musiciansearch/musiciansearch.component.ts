import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HttpService } from '../../http.service';
import { UserSearch } from '../../models';

@Component({
  selector: 'app-musiciansearch',
  templateUrl: './musiciansearch.component.html',
  styleUrl: './musiciansearch.component.css'
})
export class MusiciansearchComponent implements OnInit{

  private fb = inject(FormBuilder)
  private httpService = inject(HttpService)


  form !: FormGroup

  users: UserSearch[]=[]
  searchFinished : boolean = false
  searchTerm!:string

  ngOnInit(): void {
      this.form = this.createForm()
  }

  createForm(){
    return this.fb.group({
      instrument : this.fb.control<string>('', Validators.required)
    })
  }


  processForm(){
    this.searchFinished = false
    const instrument = this.form.value['instrument'].toLowerCase()
    this.searchTerm = instrument
    this.httpService.searchForMusiciansByInstrument(instrument).then(
      (response)=>{
        this.users = response as UserSearch[]
        this.searchFinished = true
      }
    ).catch(
      ()=>{
        alert("Server error. Try again later.")
      }
    )

    this.form.reset()
    
  }
}
