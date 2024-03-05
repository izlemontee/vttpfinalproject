import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { MainComponent } from './main/main.component';
import { LoginredirectComponent } from './loginredirect/loginredirect.component';


const routes: Routes = [
  {path:"",component:MainComponent},
  {path:'login',component:LoginComponent},
  {path:'redirect',component:LoginredirectComponent}

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
