import { APP_INITIALIZER, NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './login/login.component';
import { MainComponent } from './main/main.component';
import { HttpClientModule } from '@angular/common/http';
import {MatIconModule} from '@angular/material/icon';


import { LoginredirectComponent } from './loginredirect/loginredirect.component';
import { UsercreationComponent } from './user/usercreation/usercreation.component';
import { ReactiveFormsModule } from '@angular/forms';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { ProfilesetupComponent } from './user/profilesetup/profilesetup.component';
import { ArtistsComponent } from './user/artists/artists.component';
import { PictureComponent } from './user/picture/picture.component';
import { ProfileComponent } from './user/profile/profile.component';
import { InstrumentsComponent } from './user/instruments/instruments.component';
import { ImageComponent } from './user/image/image.component';
import { StoreModule } from '@ngrx/store';
import { userReducer } from './state/state.reduce';
import { AppInitialiserService } from './app-initialiser.service';
import { WelcomeComponent } from './main/welcome/welcome.component';
import { FeedComponent } from './main/feed/feed.component';


@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    MainComponent,
    LoginredirectComponent,
    UsercreationComponent,
    ProfilesetupComponent,
    ArtistsComponent,
    PictureComponent,
    ProfileComponent,
    InstrumentsComponent,
    ImageComponent,
    WelcomeComponent,
    FeedComponent,

  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    ReactiveFormsModule,
    MatIconModule,
    StoreModule.forRoot({user:userReducer}, {})
  ],
  providers: [Document, provideAnimationsAsync(),
    {
      provide:APP_INITIALIZER,
      useFactory:(initialiser: AppInitialiserService)=>()=> initialiser.initialise(),
      deps:[AppInitialiserService],
      multi:true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
