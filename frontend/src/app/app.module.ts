import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {HttpClientModule} from '@angular/common/http';
import {AuthModule, provideAuth} from 'angular-auth-oidc-client';
import {authConfig} from './core/auth/auth.config';
import {CommonModule} from '@angular/common';
import { FormsModule } from '@angular/forms';

@NgModule({
  declarations: [AppComponent],
  imports: [BrowserModule, CommonModule, AppRoutingModule, HttpClientModule,FormsModule, AuthModule.forRoot(authConfig)],
  providers: [provideAuth(authConfig)],
  bootstrap: [AppComponent]
})
export class AppModule {
}
