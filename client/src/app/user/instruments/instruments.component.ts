import { HttpClient } from '@angular/common/http';
import { Component, Input, OnInit, Output, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Subject } from 'rxjs';
import { HttpService } from '../../http.service';

@Component({
  selector: 'app-instruments',
  templateUrl: './instruments.component.html',
  styleUrl: './instruments.component.css'
})
export class InstrumentsComponent implements OnInit{
  private fb = inject(FormBuilder)
  private httpService = inject(HttpService)

  form!:FormGroup

  pendingServer : boolean = false

  @Input()
  username!:string

  @Output()
  deactivateInstrumentSelection = new Subject<void>
 
  instruments: string[]= []

  ngOnInit(): void {
    this.getInstruments()
      this.form = this.createForm()
  }

  createForm(){
    return this.fb.group({
      instrument: this.fb.control<string>('',Validators.required)
    })
  }

  processForm(){
    const instrument = this.form.value.instrument
    this.instruments.push(instrument.toLowerCase())
    this.form.reset()
  }

  getInstruments(){
    this.httpService.getUserInstruments(this.username).then(
      response =>{
        // console.log("instruments", response)
        this.instruments = response.instruments
      }
    )
  }

  deleteInstrument(index:number){
    this.instruments.splice(index,1)

  }

  deactivate(){
    this.deactivateInstrumentSelection.next()
  }

  submitInstruments(){
    this.pendingServer = true
    this.httpService.updateUserInstruments(this.instruments, this.username).then(
      ()=>{
        this.deactivateInstrumentSelection.next()
        this.pendingServer = false
      }
    ).catch(
      ()=>{
        alert("Update Error. Try again.")
        this.pendingServer = false
      }
    )
  }

}
