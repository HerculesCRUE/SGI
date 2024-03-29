import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { ActionComponent } from '@core/component/action.component';
import { SgiError } from '@core/errors/sgi-error';
import { MSG_PARAMS } from '@core/i18n';
import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { switchMap } from 'rxjs/operators';
import { SeguimientoFormularioActionService } from '../../seguimiento-formulario/seguimiento-formulario.action.service';
import { GESTION_SEGUIMIENTO_ROUTE_NAMES } from '../gestion-seguimiento-route-names';
import { GestionSeguimientoActionService } from '../gestion-seguimiento.action.service';

const MSG_BUTTON_SAVE = marker('btn.save');
const MSG_SUCCESS = marker('msg.update.entity.success');
const MSG_ERROR = marker('error.update.entity');
const EVALUACION_SEGUIMIENTO_KEY = marker('eti.evaluacion-seguimiento');

@Component({
  selector: 'sgi-gestion-seguimiento-evaluar',
  templateUrl: './gestion-seguimiento-evaluar.component.html',
  styleUrls: ['./gestion-seguimiento-evaluar.component.scss'],
  encapsulation: ViewEncapsulation.None,
  viewProviders: [
    GestionSeguimientoActionService,
    {
      provide: SeguimientoFormularioActionService,
      useExisting: GestionSeguimientoActionService
    }
  ]
})
export class GestionSeguimientoEvaluarComponent extends ActionComponent implements OnInit {
  GESTION_SEGUIMIENTO_ROUTE_NAMES = GESTION_SEGUIMIENTO_ROUTE_NAMES;

  textoCrear = MSG_BUTTON_SAVE;
  textoSuccess: string;
  textoError: string;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    private readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    route: ActivatedRoute,
    router: Router,
    public actionService: GestionSeguimientoActionService,
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
      EVALUACION_SEGUIMIENTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SUCCESS,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoSuccess = value);

    this.translate.get(
      EVALUACION_SEGUIMIENTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_ERROR,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoError = value);
  }

  saveOrUpdate(): void {
    this.actionService.saveOrUpdate().subscribe(
      () => { },
      (error) => {
        this.logger.error(error);
        if (error instanceof SgiError) {
          this.snackBarService.showError(error);
        }
        else {
          this.snackBarService.showError(this.textoError);
        }
      },
      () => {
        this.snackBarService.showSuccess(this.textoSuccess);
      }
    );
  }
}
