import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-postdisplay',
  templateUrl: './postdisplay.component.html',
  styleUrl: './postdisplay.component.css'
})
export class PostdisplayComponent implements OnInit{

  private activatedRoute = inject(ActivatedRoute)
  postId!:string

  ngOnInit(): void {
    console.log( this.activatedRoute.snapshot.params['id'])
    this.postId =  this.activatedRoute.snapshot.params['id']
     
  }

}
