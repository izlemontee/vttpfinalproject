import { Component, OnInit, inject } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-rejected',
  templateUrl: './rejected.component.html',
  styleUrl: './rejected.component.css'
})
export class RejectedComponent implements OnInit{

  private router = inject(Router)

  ngOnInit(): void {
      alert("Link to spotify unsuccessful. Please try again.")
      this.router.navigate(['/'])
  }

}
