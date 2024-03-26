import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';


import {MatToolbarModule} from '@angular/material/toolbar';
import {MatInputModule} from '@angular/material/input';
import {MatBadgeModule} from '@angular/material/badge';
import {MatButtonModule} from '@angular/material/button';
import {MatDialogModule} from '@angular/material/dialog';



@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    MatToolbarModule,
    MatInputModule,
    MatBadgeModule,
    MatButtonModule,
    MatDialogModule
  ],
  exports:[
    MatToolbarModule,
    MatInputModule,
    MatBadgeModule,
    MatButtonModule,
    MatDialogModule
  ]
})
export class MaterialsModule { }
