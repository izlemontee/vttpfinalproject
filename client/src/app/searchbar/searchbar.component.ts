import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { HttpService } from '../http.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-searchbar',
  templateUrl: './searchbar.component.html',
  styleUrl: './searchbar.component.css'
})
export class SearchbarComponent implements OnInit{

  private fb = inject(FormBuilder)
  private httpService = inject(HttpService)
  private router = inject(Router)

  searchForm !: FormGroup

  ngOnInit(): void {
      this.searchForm = this.createForm()
  }

  createForm(){
    return this.fb.group({
      search: this.fb.control<string>('', [Validators.required])
    })
  }

  processForm(){
    const searchTerm = this.searchForm.value.search.trim()
    this.searchForm.reset()
    if(searchTerm!=''){
      this.router.navigate(['/search'], {queryParams:{"searchTerm":searchTerm}})
    }
    else{
      alert("Please enter some text to search.")
    }

  }


}
