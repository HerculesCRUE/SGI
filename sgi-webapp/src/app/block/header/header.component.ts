import { Component, OnDestroy } from '@angular/core';
import { Language } from '@core/i18n/language';
import { Module } from '@core/module';
import { ConfigPublicService } from '@core/services/cnf/config-public.service';
import { ResourcePublicService } from '@core/services/cnf/resource-public.service';
import { LanguageService } from '@core/services/language.service';
import { LayoutService } from '@core/services/layout.service';
import { Subscription } from 'rxjs';


@Component({
  selector: 'sgi-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnDestroy {
  Module = Module;
  anchoPantalla: number;

  module: Module;
  numLogosCabecera: number;
  private subscriptions: Subscription[] = [];

  get languages(): Language[] {
    return this._languages;
  }
  private _languages: Language[] = [];

  get selectedLanguage(): Language {
    return this._selectedLanguage;
  }
  private _selectedLanguage: Language = null;

  constructor(
    private readonly layout: LayoutService,
    private readonly resourceService: ResourcePublicService,
    private readonly configService: ConfigPublicService,
    private readonly languageService: LanguageService
  ) {
    this.anchoPantalla = window.innerWidth;
    this.subscriptions.push(this.layout.activeModule$.subscribe((res) => this.module = res));
    this.subscriptions.push(this.configService.getNumeroLogosCabecera().subscribe((num) => this.numLogosCabecera = Number(num)));
    this._languages = languageService.getEnabledLanguages();
    this._selectedLanguage = languageService.getLanguage();
    this.subscriptions.push(languageService.onEnabledLanguagesChange().subscribe((langs) => this._languages = langs));
    this.subscriptions.push(languageService.languageChange$.subscribe((lang) => this._selectedLanguage = lang));
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(x => x.unsubscribe());
  }

  getUrlResource(id: string): string {
    return this.resourceService.getUrlResource(id);
  }

  getUrlSetResource(id: string, versiones: string[]): string {
    return versiones.map(version => `${this.getUrlResource(version ? id + version : id)} ${version}`).join(', ');
  }

  selectLanguage(language: Language) {
    this.languageService.switchLanguage(language);
  }

}
