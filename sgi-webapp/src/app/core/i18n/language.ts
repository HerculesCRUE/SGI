import { marker } from "@biesbjerg/ngx-translate-extract-marker";

export class Language {

  private constructor(public readonly code: string, public readonly codeExtended: string, public readonly translateKey: string) {

  }

  public static readonly ES = new Language('es', 'spa', marker('language.es'));
  public static readonly EN = new Language('en', 'eng', marker('language.en'));
  public static readonly EU = new Language('eu', 'eus', marker('language.eu'));

  public static values(): Language[] {
    return [this.ES, this.EN, this.EU];
  }

  public static fromCode(code: string): Language {
    for (const value of this.values()) {
      if (code?.toLocaleLowerCase() === value.code) {
        return value;
      }
    }

    return null;
  }

  public toString = (): string => {
    return this.code;
  }
}