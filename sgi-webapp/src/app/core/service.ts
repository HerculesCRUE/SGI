import { environment } from '@env';

export class Service {

  private constructor(
    public readonly prefix: string
  ) { }

  public static readonly CNF = new Service(`${environment.serviceServers.cnf}/`);
  public static readonly COM = new Service(`${environment.serviceServers.com}/`);
  public static readonly CSP = new Service(`${environment.serviceServers.csp}/`);
  public static readonly EER = new Service(`${environment.serviceServers.eer}/`);
  public static readonly ETI = new Service(`${environment.serviceServers.eti}/`);
  public static readonly PII = new Service(`${environment.serviceServers.pii}/`);
  public static readonly PRC = new Service(`${environment.serviceServers.prc}/`);
  public static readonly REL = new Service(`${environment.serviceServers.rel}/`);
  public static readonly REP = new Service(`${environment.serviceServers.rep}/`);
  public static readonly SGDOC = new Service(`${environment.serviceServers.sgdoc}/`);
  public static readonly SGE = new Service(`${environment.serviceServers.sge}/`);
  public static readonly SGEMP = new Service(`${environment.serviceServers.sgemp}/`);
  public static readonly SGEPII = new Service(`${environment.serviceServers.sgepii}/`);
  public static readonly SGO = new Service(`${environment.serviceServers.sgo}/`);
  public static readonly SGP = new Service(`${environment.serviceServers.sgp}/`);
  public static readonly TP = new Service(`${environment.serviceServers.tp}/`);
  public static readonly USR = new Service(`${environment.serviceServers.usr}/`);

  static get values(): Service[] {
    return [
      this.CNF,
      this.COM,
      this.CSP,
      this.EER,
      this.ETI,
      this.PII,
      this.PRC,
      this.REL,
      this.REP,
      this.SGDOC,
      this.SGE,
      this.SGEMP,
      this.SGEPII,
      this.SGO,
      this.SGP,
      this.TP,
      this.USR
    ];
  }

  public static fromUrl(url: string): Service {
    return this.values.find(service => url.startsWith(service.prefix));
  }

}
