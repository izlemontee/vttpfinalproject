import { Component, Input, OnInit, Output, inject } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { HttpService } from '../../http.service';
import { Subject } from 'rxjs';

@Component({
  selector: 'app-picture',
  templateUrl: './picture.component.html',
  styleUrl: './picture.component.css'
})
export class PictureComponent implements OnInit{

  form !: FormGroup
  file !: any

  @Input()
  username!:string

  @Output()
  deactivateImageUploader = new Subject<void>

  private fb = inject(FormBuilder)
  private httpService = inject(HttpService)

  ngOnInit(): void {
      this.form = this.createForm()
  }

  createForm(){
    return this.fb.group({
      image: this.fb.control<any>('')
    })
  }

  imageDetected(event:any){
    this.file = event.target.files[0]

  }

  uploadImage(){
    this.httpService.uploadUserProfilePicture(this.file, this.username).then(
      ()=>{
        this.form.reset()
        this.deactivateImageUploader.next()
      }
    ).catch(
      ()=>{
        alert("Image upload failed. Try again.")
        this.form.reset()
      }
    )
  }

  deactivate(){
    this.deactivateImageUploader.next()
  }

}
