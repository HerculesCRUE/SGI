import { Component, OnInit } from '@angular/core';
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
import { SEGUIMIENTO_JUSTIFICACION_REQUERIMIENTO_ROUTE_NAMES } from '../seguimiento-justificacion-requerimiento-route-names';
import { SeguimientoJustificacionRequerimientoActionService } from '../seguimiento-justificacion-requerimiento.action.service';

const MSG_BUTTON_SAVE = marker('btn.save');
const MSG_SUCCESS = marker('msg.save.entity.success');
const MSG_ERROR = marker('error.save.entity');
const REQUERIMIENTO_JUSTIFICACION_KEY = marker('csp.requerimiento-justificacion');

@Component({
  selector: 'sgi-seguimiento-justificacion-requerimiento-crear',
  templateUrl: './seguimiento-justificacion-requerimiento-crear.component.html',
  styleUrls: ['./seguimiento-justificacion-requerimiento-crear.component.scss'],
  viewProviders: [
    SeguimientoJustificacionRequerimientoActionService
  ]
})
export class SeguimientoJustificacionRequerimientoCrearComponent extends ActionComponent implements OnInit {
  textoActualizar = MSG_BUTTON_SAVE;
  textoActualizarSuccess: string;
  textoActualizarError: string;

  get SEGUIMIENTO_JUSTIFICACION_REQUERIMIENTO_ROUTE_NAMES() {
    return SEGUIMIENTO_JUSTIFICACION_REQUERIMIENTO_ROUTE_NAMES;
  }

  constructor(
    private readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    router: Router,
    route: ActivatedRoute,
    public actionService: SeguimientoJustificacionRequerimientoActionService,
    dialogService: DialogService,
    private readonly translate: TranslateService,
  ) {
    super(router, route, actionService, dialogService);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  private setupI18N(): void {

    this.translate.get(
      REQUERIMIENTO_JUSTIFICACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SUCCESS,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoActualizarSuccess = value);

    this.translate.get(
      REQUERIMIENTO_JUSTIFICACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_ERROR,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoActualizarError = value);
  }

  saveOrUpdate(): void {
    this.actionService.saveOrUpdate().subscribe(
      () => { },
      (error) => {
        this.logger.error(error);
        if (error instanceof SgiError) {
          if (!!!error.managed) {
            this.snackBarService.showError(error);
          }
        }
        else {
          this.snackBarService.showError(this.textoActualizarError);
        }
      },
      () => {
        this.snackBarService.showSuccess(this.textoActualizarSuccess);
        const requerimientoJustificacionId = this.actionService.getFragment(this.actionService.FRAGMENT.DATOS_GENERALES).getKey();
        this.router.navigate([`../${requerimientoJustificacionId}`], { relativeTo: this.activatedRoute });
      }
    );
  }

  cancel(): void {
    this.router.navigate(['../'], { relativeTo: this.activatedRoute });
  }
}
