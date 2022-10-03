import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { ActionComponent } from '@core/component/action.component';
import { MSG_PARAMS } from '@core/i18n';
import { DialogService } from '@core/services/dialog.service';
import { TranslateService } from '@ngx-translate/core';
import { switchMap } from 'rxjs/operators';
import { CONVOCATORIA_CONCEPTO_GASTO_PUBLIC_ROUTE_NAMES } from '../convocatoria-concepto-gasto-public-route-names';
import { ConvocatoriaConceptoGastoPublicActionService } from '../convocatoria-concepto-gasto-public.action.service';

const MSG_BUTTON_EDIT = marker('btn.save.entity');
const CONVOCATORIA_CONCEPTO_GASTO_KEY = marker('csp.convocatoria-elegibilidad.concepto-gasto.concepto-gasto');

@Component({
  selector: 'sgi-convocatoria-concepto-gasto-public-editar',
  templateUrl: './convocatoria-concepto-gasto-public-editar.component.html',
  styleUrls: ['./convocatoria-concepto-gasto-public-editar.component.scss'],
  viewProviders: [
    ConvocatoriaConceptoGastoPublicActionService
  ]
})
export class ConvocatoriaConceptoGastoPublicEditarComponent extends ActionComponent implements OnInit {

  CONVOCATORIA_CONCEPTO_GASTO_ROUTE_NAMES = CONVOCATORIA_CONCEPTO_GASTO_PUBLIC_ROUTE_NAMES;

  textoEditar: string;

  constructor(
    router: Router,
    route: ActivatedRoute,
    public actionService: ConvocatoriaConceptoGastoPublicActionService,
    dialogService: DialogService,
    private readonly translate: TranslateService
  ) {
    super(router, route, actionService, dialogService);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  private setupI18N(): void {
    this.translate.get(
      CONVOCATORIA_CONCEPTO_GASTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_BUTTON_EDIT,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoEditar = value);

  }

  saveOrUpdate(): void {
    throw new Error('Method not implemented.');
  }

  cancel(): void {
    this.returnUrl();
  }

  private returnUrl() {
    this.router.navigate(['../'], { relativeTo: this.activatedRoute });
  }
}
