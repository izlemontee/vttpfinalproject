import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-post',
  templateUrl: './post.component.html',
  styleUrl: './post.component.css'
})
export class PostComponent implements OnInit{


  private fb = inject(FormBuilder)

  postInput!: FormGroup

  ngOnInit(): void {
      this.postInput = this.createForm()
  }

  createForm(){
    return this.fb.group({
      content:this.fb.control<string>('',[Validators.required, Validators.maxLength(1024)])
    })
  }

  processForm(){
    var content = this.postInput.value['content']
    console.log(content)
  }
}
