
import { marker } from '@biesbjerg/ngx-translate-extract-marker';

export class Module {

  private constructor(
    public readonly code: string,
    public readonly title: string,
    public readonly path: string
  ) { }

  public static readonly CSP = new Module('CSP', marker('csp'), 'csp');
  public static readonly ETI = new Module('ETI', marker('eti'), 'eti');
  public static readonly INV = new Module('INV', marker('inv'), 'inv');
  public static readonly PII = new Module('PII', marker('pii'), 'pii');

  static get values(): Module[] {
    return [
      this.CSP,
      this.ETI,
      this.INV,
      this.PII
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
      default:
        return undefined;
    }
  }

  public toString(): string {
    return this.path;
  }
}


