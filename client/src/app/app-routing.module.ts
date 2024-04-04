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
import { FriendsComponent } from './user/friends/friends.component';
import { PostdisplayComponent } from './postdisplay/postdisplay.component';
import { MusiciansearchComponent } from './musician/musiciansearch/musiciansearch.component';


const routes: Routes = [
  {path:"",component:MainComponent},
  {path:'redirect',component:LoginredirectComponent},
  {path:'user/:username', component:ProfileComponent},
  {path:"search", component:SearchresultsComponent},
  {path:"notifications", component:NotificationsComponent,canActivate:[isLoggedIn]},
  // {path:"requests",component:FriendrequestComponent, canActivate:[isLoggedIn]},
  {path:"requests",component:FriendrequestComponent,},
  {path:"user/:username/friends",component:FriendsComponent},
  {path:"post/:id", component:PostdisplayComponent},
  {path:"musicians/search", component:MusiciansearchComponent}

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
