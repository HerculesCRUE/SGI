import { registerLocaleData } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import localeEs from '@angular/common/locales/es';
import { APP_INITIALIZER, LOCALE_ID, NgModule } from '@angular/core';
import { CoreModule } from '@angular/flex-layout';
import { MatPaginatorIntl } from '@angular/material/paginator';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { environment } from '@env';
import { AppMatPaginatorIntl } from '@material/app-mat-paginator-intl';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateCompiler, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { SgiAuthMode, SgiAuthModule, SGI_AUTH_CONFIG } from '@sgi/framework/auth';
import { LoggerModule } from 'ngx-logger';
import { TranslateMessageFormatCompiler } from 'ngx-translate-messageformat-compiler';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BlockModule } from './block/block.module';
import { ConfigService } from './core/services/config.service';
import { HomeComponent } from './home/home.component';

// Load supported locales
registerLocaleData(localeEs);

const appInitializerFn = (appConfig: ConfigService) => {
  return () => {
    return appConfig.loadAppConfig();
  };
};

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
  ],
  imports: [
    AppRoutingModule,
    BrowserModule,
    BrowserAnimationsModule,
    CoreModule,
    LoggerModule.forRoot(environment.loggerConfig),
    MaterialDesignModule,
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: (http: HttpClient) => {
          return new TranslateHttpLoader(http);
        },
        deps: [HttpClient]
      },
      compiler: {
        provide: TranslateCompiler,
        useClass: TranslateMessageFormatCompiler
      },
      defaultLanguage: 'es'
    }),
    BlockModule,
    HttpClientModule,
    SgiAuthModule.forRoot()
  ],
  providers: [
    ConfigService,
    {
      provide: APP_INITIALIZER,
      useFactory: appInitializerFn,
      multi: true,
      deps: [ConfigService]
    },
    {
      provide: MatPaginatorIntl,
      useClass: AppMatPaginatorIntl,
    },
    {
      provide: SGI_AUTH_CONFIG,
      useValue: environment.authConfig
    },
    {
      provide: SgiAuthMode,
      useValue: environment.authConfig.mode
    },
    {
      provide: LOCALE_ID,
      useValue: 'es'
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
