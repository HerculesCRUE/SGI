import { HttpClient } from '@angular/common/http';
import { Injectable, Injector } from '@angular/core';
import { I18nFieldValue } from '@core/i18n/i18n-field';
import { I18nFieldValueResponse } from '@core/i18n/i18n-field-response';
import { Language } from '@core/i18n/language';
import { IConfigValue } from '@core/models/cnf/config-value';
import { environment } from '@env';
import { TranslateService } from '@ngx-translate/core';
import { CookieService } from 'ngx-cookie-service';
import { BehaviorSubject, Observable } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { ConfigGlobal } from 'src/app/module/adm/config-global/config-global.component';


@Injectable({
  providedIn: 'root'
})
export class LanguageService {

  private language: Language = Language.ES;
  private enabledLanguages: Language[] = [Language.ES];
  private languagesPriority: Language[] = [Language.ES];
  private readonly enabledLanguages$: BehaviorSubject<Language[]> = new BehaviorSubject<Language[]>(this.enabledLanguages);
  private readonly languagesPriority$: BehaviorSubject<Language[]> = new BehaviorSubject<Language[]>(this.languagesPriority);
  private cookieService: CookieService;
  private translateService: TranslateService;

  readonly languageChange$: BehaviorSubject<Language> = new BehaviorSubject<Language>(Language.ES);

  constructor(private injector: Injector, private http: HttpClient) {

  }

  async _bootstrap() {
    this.loadEnabledLanguages().pipe(
      switchMap((available) => {
        return this.loadLanguagePriority().pipe(
          map((priorities) => {
            return {
              available,
              priorities
            }
          })
        )
      })
    ).subscribe(
      (config) => {
        this.enabledLanguages = config.available;
        this.languagesPriority = config.priorities;
        this.enabledLanguages$.next(this.enabledLanguages);
        this.languagesPriority$.next(this.languagesPriority);
        this.initialize();
      },
      (error) => {
        console.log("Error obteniendo los idiomas disponibles. Se inicia con un idioma por defecto: " + error);
        this.initialize();
      }
    );
    this.cookieService = this.injector.get<CookieService>(CookieService);
    this.translateService = this.injector.get<TranslateService>(TranslateService);
  }

  public getEnabledLanguages(): Language[] {
    return this.enabledLanguages;
  }

  public onEnabledLanguagesChange(): Observable<Language[]> {
    return this.enabledLanguages$.asObservable();
  }

  public getLanguage(): Language {
    return this.language;
  }

  public switchLanguage(language: Language, updateCookie = true): void {
    this.language = language;
    this.translateService.use(language.code);
    this.languageChange$.next(this.language);
    if (updateCookie) {
      this.setCookieLanguage(language);
    }
  }

  public refresh(): void {
    this._bootstrap().then();
  }

  public getFieldValue(field: I18nFieldValue[]): string {
    const f = this.getField(field);
    if (f) {
      return f.value;
    }
    return '';
  }

  public getField<T extends I18nFieldValue | I18nFieldValueResponse>(field: T[]): T {
    if (!Array.isArray(field) || field?.length === 0) {
      return null;
    }
    let fieldValue = field.filter(f => f.lang.toString() === this.language.code);
    if (fieldValue.length) {
      return fieldValue[0];
    }
    else {
      let other: T[] = [];
      let idx = 0;
      do {
        const lang = this.languagesPriority[idx];
        other = field.filter(f => f.lang.toString() === lang.code);
        idx++;
      } while (other.length === 0 && idx < this.languagesPriority.length)
      if (other.length) {
        return other[0];
      }
      else {
        return null;
      }
    }
  }

  private loadEnabledLanguages(): Observable<Language[]> {
    return this.http.get<IConfigValue>(`${environment.serviceServers.cnf}/public/config/${ConfigGlobal.I18N_ENABLED_LANGUAGES}`).pipe(
      map(response => JSON.parse(response.value).map(m => Language.fromCode(m)))
    );
  }

  private loadLanguagePriority(): Observable<Language[]> {
    return this.http.get<IConfigValue>(`${environment.serviceServers.cnf}/public/config/${ConfigGlobal.I18N_LANGUAGES_PRIORITY}`).pipe(
      map(response => JSON.parse(response.value).map(m => Language.fromCode(m)))
    );
  }

  private initialize(): void {
    const cookieLanguage = Language.fromCode(this.cookieService.get('sgi-locale'));
    const browserLanguage = Language.fromCode(this.translateService.getBrowserLang());
    const defaultLanguage = Language.fromCode(this.translateService.getDefaultLang())
    if (this.enabledLanguages.includes(cookieLanguage)) {
      this.switchLanguage(cookieLanguage, false)
    }
    else if (this.enabledLanguages.includes(browserLanguage)) {
      this.switchLanguage(browserLanguage, true);
    }
    else if (this.enabledLanguages.includes(defaultLanguage)) {
      this.switchLanguage(defaultLanguage, true)
    }
    else {
      this.switchLanguage(this.enabledLanguages[0], true);
    }
  }

  private setCookieLanguage(language: Language): void {
    this.cookieService.set('sgi-locale', language.code, { path: '/', sameSite: 'Strict' });
  }
}