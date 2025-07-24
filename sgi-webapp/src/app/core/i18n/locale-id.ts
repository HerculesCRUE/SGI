import { LanguageService } from "@core/services/language.service";
import { Subject } from "rxjs";
import { Language } from "./language";

export class LocaleId extends String {
  get onChange$(): Subject<Language> {
    return this.languageService.languageChange$;
  }

  constructor(private languageService: LanguageService) {
    super();
  }

  toString(): string {
    return this.languageService.getLanguage().code;
  }

  valueOf(): string {
    return this.toString();
  }
}
