
import { marker } from '@biesbjerg/ngx-translate-extract-marker';

export class Module {

  private constructor(
    public readonly code: string,
    public readonly title: string,
    public readonly path: string,
    public readonly name: string,
    public readonly icon: string
  ) { }

  public static readonly CSP = new Module('CSP', marker('csp'), 'csp', marker('title.modulo.csp'), 'business_center');
  public static readonly ETI = new Module('ETI', marker('eti'), 'eti', marker('title.modulo.eti'), 'balance');
  public static readonly INV = new Module('INV', marker('inv'), 'inv', marker('title.modulo.inv'), 'cast_for_education');
  public static readonly PII = new Module('PII', marker('pii'), 'pii', marker('title.modulo.pii'), 'tips_and_updates');
  public static readonly PRC = new Module('PRC', marker('prc'), 'prc', marker('title.modulo.prc'), 'category');
  public static readonly EER = new Module('EER', marker('eer'), 'eer', marker('title.modulo.eer'), 'business');

  static get values(): Module[] {
    return [
      this.CSP,
      this.EER,
      this.ETI,
      this.INV,
      this.PII,
      this.PRC
    ];
  }

  public static fromPath(path: string): Module {
    switch (path) {
      case Module.CSP.path:
        return Module.CSP;
      case Module.ETI.path:
        return Module.ETI;
      case Module.INV.path:
        return Module.INV;
      case Module.PII.path:
        return Module.PII;
      case Module.PRC.path:
        return Module.PRC;
      case Module.EER.path:
        return Module.EER;
      default:
        return undefined;
    }
  }

  public static fromCode(code: string): Module {
    switch (code) {
      case Module.CSP.code:
        return Module.CSP;
      case Module.ETI.code:
        return Module.ETI;
      case Module.INV.code:
        return Module.INV;
      case Module.PII.code:
        return Module.PII;
      case Module.PRC.code:
        return Module.PRC;
      case Module.EER.code:
        return Module.EER;
      default:
        return undefined;
    }
  }

  public toString(): string {
    return this.path;
  }
}


