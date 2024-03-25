import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';


import {MatToolbarModule} from '@angular/material/toolbar';
import {MatInputModule} from '@angular/material/input';
import {MatBadgeModule} from '@angular/material/badge';
import {MatButtonModule} from '@angular/material/button';



@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    MatToolbarModule,
    MatInputModule,
    MatBadgeModule,
    MatButtonModule
  ],
  exports:[
    MatToolbarModule,
    MatInputModule,
    MatBadgeModule,
    MatButtonModule
  ]
})
export class MaterialsModule { }
