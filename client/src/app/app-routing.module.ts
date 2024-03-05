import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { MainComponent } from './main/main.component';
import { LoginredirectComponent } from './loginredirect/loginredirect.component';
import { UsercreationComponent } from './user/usercreation/usercreation.component';
import { ProfilesetupComponent } from './user/profilesetup/profilesetup.component';


const routes: Routes = [
  {path:"",component:MainComponent},
  {path:'login',component:LoginComponent},
  {path:'redirect',component:LoginredirectComponent},
  {path:'createuser',component:UsercreationComponent},
  {path:'setup',component:ProfilesetupComponent}

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
