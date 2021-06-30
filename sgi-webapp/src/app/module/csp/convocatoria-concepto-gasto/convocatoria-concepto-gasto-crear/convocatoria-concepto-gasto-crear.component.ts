import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { ActionComponent } from '@core/component/action.component';
import { MSG_PARAMS } from '@core/i18n';
import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { switchMap } from 'rxjs/operators';
import { CONVOCATORIA_CONCEPTO_GASTO_ROUTE_NAMES } from '../convocatoria-concepto-gasto-route-names';
import { ConvocatoriaConceptoGastoActionService } from '../convocatoria-concepto-gasto.action.service';

const MSG_BUTTON_SAVE = marker('btn.save.entity');
const MSG_SUCCESS = marker('msg.convocatoria-concepto-gasto.create.success');
const MSG_ERROR = marker('error.convocatoria-concepto-gasto.create');
const MSG_CONVOCATORIA_CONCEPTO_GASTO_KEY = marker('csp.convocatoria-elegibilidad.concepto-gasto.concepto-gasto');

@Component({
  selector: 'sgi-convocatoria-concepto-gasto-crear',
  templateUrl: './convocatoria-concepto-gasto-crear.component.html',
  styleUrls: ['./convocatoria-concepto-gasto-crear.component.scss'],
  viewProviders: [
    ConvocatoriaConceptoGastoActionService
  ]
})
export class ConvocatoriaConceptoGastoCrearComponent extends ActionComponent implements OnInit {
  CONVOCATORIA_CONCEPTO_GASTO_ROUTE_NAMES = CONVOCATORIA_CONCEPTO_GASTO_ROUTE_NAMES;

  textoCrear: string;

  constructor(
    protected logger: NGXLogger,
    protected snackBarService: SnackBarService,
    router: Router,
    route: ActivatedRoute,
    public actionService: ConvocatoriaConceptoGastoActionService,
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
      MSG_CONVOCATORIA_CONCEPTO_GASTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_BUTTON_SAVE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoCrear = value);
  }

  saveOrUpdate(): void {
    this.actionService.saveOrUpdate().subscribe(
      () => { },
      () => {
        this.snackBarService.showError(MSG_ERROR);
      },
      () => {
        this.snackBarService.showSuccess(MSG_SUCCESS);
        this.router.navigate(['../'], { relativeTo: this.activatedRoute });
      }
    );
  }

  cancel(): void {
    this.router.navigate(['../'], { relativeTo: this.activatedRoute });
  }
}
