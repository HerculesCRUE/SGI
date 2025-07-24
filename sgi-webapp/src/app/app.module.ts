import { registerLocaleData } from '@angular/common';
import { HTTP_INTERCEPTORS, HttpClient, HttpClientModule } from '@angular/common/http';
import localeEs from '@angular/common/locales/es';
import localeEu from '@angular/common/locales/eu';
import { APP_INITIALIZER, LOCALE_ID, NgModule } from '@angular/core';
import { CoreModule } from '@angular/flex-layout';
import { MatPaginatorIntl } from '@angular/material/paginator';
import { BrowserModule, Meta } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { SgiErrorHttpInterceptor } from '@core/error-http-interceptor';
import { Language } from '@core/i18n/language';
import { LocaleId } from '@core/i18n/locale-id';
import { SgiLanguageHttpInterceptor } from '@core/languague-http-interceptor';
import { SgiRequestHttpInterceptor } from '@core/request-http-interceptor';
import { ResourcePublicService } from '@core/services/cnf/resource-public.service';
import { LanguageService } from '@core/services/language.service';
import { TimeZoneService } from '@core/services/timezone.service';
import { TIME_ZONE } from '@core/time-zone';
import { environment } from '@env';
import { AppMatPaginatorIntl } from '@material/app-mat-paginator-intl';
import { MaterialDesignModule } from '@material/material-design.module';
import { FormlyModule } from '@ngx-formly/core';
import { TranslateCompiler, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { SGI_AUTH_CONFIG, SgiAuthMode, SgiAuthModule } from '@sgi/framework/auth';
import { CKEDITOR_CONFIG, CkEditorConfig, DEFAULT_CKEDITOR_CONFIG } from '@shared/sgi-ckeditor-config';
import { CookieService } from 'ngx-cookie-service';
import { LoggerModule } from 'ngx-logger';
import { TranslateMessageFormatCompiler } from 'ngx-translate-messageformat-compiler';
import { Observable, forkJoin, of } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BlockModule } from './block/block.module';
import { ConfigService } from './core/services/config.service';
import { HomeComponent } from './home/home.component';

export class SgiTranslateLoader implements TranslateLoader {
  constructor(
    private httpClient: HttpClient,
    private resourcesService: ResourcePublicService
  ) { }

  getTranslation(lang: string): Observable<any> {
    return forkJoin({
      baseI18n: this.httpClient.get(`/assets/i18n/${lang}.json`),
      customI18n: this.loadCustomI18n(lang)
    }).pipe(
      map(({ baseI18n, customI18n }) => {
        if (!customI18n) {
          return baseI18n;
        }

        return Object.assign(baseI18n, JSON.parse(customI18n));
      })
    );
  }

  private loadCustomI18n(lang: string): Observable<string> {
    return this.resourcesService.download(`web-i18n-${lang}`).pipe(
      switchMap(response => response.text()),
      catchError((_) => {
        return of(void 0);
      })
    );
  }

}

// Load supported locales
registerLocaleData(localeEs);
registerLocaleData(localeEu);

const appInitializerFn = (appConfig: ConfigService) => {
  return () => {
    return appConfig.loadAppConfig();
  };
};

const bootstrapLanguageService = (languageService: LanguageService) => {
  return () => {
    return languageService._bootstrap();
  };
};

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent
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
        useFactory: (http: HttpClient, resourcesService: ResourcePublicService) => {
          return new SgiTranslateLoader(http, resourcesService);
        },
        deps: [HttpClient, ResourcePublicService]
      },
      compiler: {
        provide: TranslateCompiler,
        useClass: TranslateMessageFormatCompiler
      },
      defaultLanguage: Language.ES.code
    }),
    BlockModule,
    HttpClientModule,
    SgiAuthModule.forRoot(),
    FormlyModule.forRoot()
  ],
  providers: [
    Meta,
    ConfigService,
    CookieService,
    {
      provide: APP_INITIALIZER,
      useFactory: appInitializerFn,
      multi: true,
      deps: [ConfigService]
    },
    {
      provide: APP_INITIALIZER,
      useFactory: bootstrapLanguageService,
      multi: true,
      deps: [LanguageService]
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
      useClass: LocaleId,
      deps: [LanguageService]
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: SgiLanguageHttpInterceptor,
      multi: true
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: SgiErrorHttpInterceptor,
      multi: true
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: SgiRequestHttpInterceptor,
      multi: true
    },
    {
      provide: TIME_ZONE,
      useFactory: (timeZoneService: TimeZoneService) => timeZoneService.zone$,
      deps: [TimeZoneService]
    },
    {
      provide: CKEDITOR_CONFIG,
      useFactory: (languageService: LanguageService) => {
        const defaultConfig = { ...DEFAULT_CKEDITOR_CONFIG }
        //CkEditor internally use the spread operator. Because spread operator won't work for class getter/setter properties we need to use anonymous object. 
        //See https://github.com/microsoft/TypeScript/issues/26547
        return {
          toolbar: defaultConfig.toolbar,
          link: defaultConfig.link,
          get language() {
            return languageService.getLanguage().code;
          },
          table: defaultConfig.table
        } as CkEditorConfig
      },
      deps: [LanguageService]
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
