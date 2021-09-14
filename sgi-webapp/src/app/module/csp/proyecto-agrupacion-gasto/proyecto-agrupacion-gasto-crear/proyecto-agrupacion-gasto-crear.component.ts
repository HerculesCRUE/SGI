import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { ActionComponent } from '@core/component/action.component';
import { NGXLogger } from 'ngx-logger';
import { switchMap } from 'rxjs/operators';
import { PROYECTO_AGRUPACION_GASTO_ROUTE_NAMES } from '../proyecto-agrupacion-gasto-route-names';
import { ProyectoAgrupacionGastoActionService } from '../proyecto-agrupacion-gasto.action.service';

const MSG_BUTTON_EDIT = marker('btn.save.entity');
const MSG_BUTTON_SAVE = marker('btn.save.entity');
const MSG_SUCCESS = marker('msg.save.entity.success');
const MSG_ERROR = marker('error.save.entity');
const PROYECTO_AGRUPACION_GASTO_KEY = marker('csp.proyecto-agrupacion-gasto');

@Component({
  selector: 'sgi-proyecto-agrupacion-gasto-crear',
  templateUrl: './proyecto-agrupacion-gasto-crear.component.html',
  styleUrls: ['./proyecto-agrupacion-gasto-crear.component.scss'],
  viewProviders: [
    ProyectoAgrupacionGastoActionService
  ]
})
export class ProyectoAgrupacionGastoCrearComponent extends ActionComponent implements OnInit {
  PROYECTO_AGRUPACION_GASTO_ROUTE_NAMES = PROYECTO_AGRUPACION_GASTO_ROUTE_NAMES;

  textoCrear: string;
  textoCrearSuccess: string;
  textoCrearError: string;

  constructor(
    private readonly logger: NGXLogger,
    protected snackBarService: SnackBarService,
    router: Router,
    route: ActivatedRoute,
    public actionService: ProyectoAgrupacionGastoActionService,
    dialogService: DialogService,
    private readonly translate: TranslateService
  ) {
    super(router, route, actionService, dialogService);
  }

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  private setupI18N(): void {
    this.translate.get(
      PROYECTO_AGRUPACION_GASTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_BUTTON_SAVE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoCrear = value);

    this.translate.get(
      PROYECTO_AGRUPACION_GASTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SUCCESS,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoCrearSuccess = value);

    this.translate.get(
      PROYECTO_AGRUPACION_GASTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_ERROR,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoCrearError = value);
  }

  saveOrUpdate(): void {
    this.actionService.saveOrUpdate().subscribe(
      () => { },
      (error) => {
        this.logger.error(error);
        this.snackBarService.showError(this.textoCrearError);
      },
      () => {
        this.snackBarService.showSuccess(this.textoCrearSuccess);
        const agrupacionGastoId = this.actionService.getFragment(this.actionService.FRAGMENT.DATOS_GENERALES).getKey();
        this.router.navigate([`../${agrupacionGastoId}`], { relativeTo: this.activatedRoute });
      }
    );
  }

  cancel(): void {
    this.router.navigate(['../'], { relativeTo: this.activatedRoute });
  }
}
