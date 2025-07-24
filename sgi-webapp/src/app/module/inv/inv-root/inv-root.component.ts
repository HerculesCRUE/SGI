import { Component, OnDestroy } from '@angular/core';
import { MSG_PARAMS } from '@core/i18n';
import { ConfigService } from '@core/services/cnf/config.service';
import { EvaluadorService } from '@core/services/eti/evaluador.service';
import { LanguageService } from '@core/services/language.service';
import { forkJoin, Subscription } from 'rxjs';
import { INV_ROUTE_NAMES } from '../inv-route-names';

@Component({
  selector: 'sgi-inv-root',
  templateUrl: './inv-root.component.html',
  styleUrls: ['./inv-root.component.scss']
})
export class InvRootComponent implements OnDestroy {

  get INV_ROUTE_NAMES() {
    return INV_ROUTE_NAMES;
  }

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  // tslint:disable-next-line: variable-name
  _urlSistemaGestionExterno: string;
  get urlSistemaGestionExterno(): string {
    return this.parseI18nValue(this._urlSistemaGestionExterno);
  }

  // tslint:disable-next-line: variable-name
  _nombreSistemaGestionExterno: string;
  get nombreSistemaGestionExterno(): string {
    return this.parseI18nValue(this._nombreSistemaGestionExterno);
  }

  showEvaluaciones = false;
  showSeguimientos = false;
  showActas = false;

  private subscriptions: Subscription[] = [];

  constructor(
    private evaluadorService: EvaluadorService,
    private configService: ConfigService,
    private languageService: LanguageService
  ) {
    this.subscriptions.push(this.evaluadorService.hasAssignedEvaluaciones().subscribe((res) => this.showEvaluaciones = res));
    this.subscriptions.push(this.evaluadorService.hasAssignedEvaluacionesSeguimiento().subscribe((res) => this.showSeguimientos = res));
    this.subscriptions.push(this.evaluadorService.hasAssignedActas().subscribe((res) => this.showActas = res));
    this.subscriptions.push(
      forkJoin(
        {
          nombre: this.configService.getNombreSistemaGestionExterno(),
          url: this.configService.getUrlSistemaGestionExterno()
        }
      ).subscribe(({ nombre, url }) => {
        this._nombreSistemaGestionExterno = nombre;
        this._urlSistemaGestionExterno = url;
      })
    );
  }

  private parseI18nValue(value: string): string {
    try {
      const parsed = JSON.parse(value);
      if (Array.isArray(parsed)) {
        return this.languageService.getFieldValue(parsed);
      }
      return value;
    } catch {
      return value;
    }
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }
}
