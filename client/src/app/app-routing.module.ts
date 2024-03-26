import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { MainComponent } from './main/main.component';
import { LoginredirectComponent } from './loginredirect/loginredirect.component';
import { UsercreationComponent } from './user/usercreation/usercreation.component';
import { ProfilesetupComponent } from './user/profilesetup/profilesetup.component';
import { ProfileComponent } from './user/profile/profile.component';
import { isLoggedIn, isLoggedOut } from './guards';
import { SearchresultsComponent } from './searchresults/searchresults.component';
import { NotificationsComponent } from './notifications/notifications.component';
import { FriendrequestComponent } from './friendrequest/friendrequest.component';


const routes: Routes = [
  {path:"",component:MainComponent},
  {path:'redirect',component:LoginredirectComponent},
  {path:'createuser',component:UsercreationComponent},
  {path:'setup',component:ProfilesetupComponent, canActivate:[isLoggedIn]},
  // {path:'setup',component:ProfilesetupComponent},
  {path:'user/:username', component:ProfileComponent},
  {path:"search", component:SearchresultsComponent},
  {path:"notifications", component:NotificationsComponent},
  {path:"requests",component:FriendrequestComponent}

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
