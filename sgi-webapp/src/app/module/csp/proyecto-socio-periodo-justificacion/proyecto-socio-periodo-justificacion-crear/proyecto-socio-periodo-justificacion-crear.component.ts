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
import { PROYECTO_SOCIO_PERIODO_JUSTIFICACION_ROUTE_NAMES } from '../proyecto-socio-periodo-justificacion-names';
import { ProyectoSocioPeriodoJustificacionActionService } from '../proyecto-socio-periodo-justificacion.action.service';

const MSG_BUTTON_SAVE = marker('btn.save.entity');
const MSG_SUCCESS = marker('msg.save.entity.success');
const MSG_ERROR = marker('error.save.entity');
const SOLICITUD_PROYECTO_PERIODO_JUSTIFICACION = marker('csp.solicitud-proyecto-periodo-justificacion');

@Component({
  selector: 'sgi-proyecto-socio-periodo-justificacion-crear',
  templateUrl: './proyecto-socio-periodo-justificacion-crear.component.html',
  styleUrls: ['./proyecto-socio-periodo-justificacion-crear.component.scss'],
  viewProviders: [
    ProyectoSocioPeriodoJustificacionActionService
  ]
})
export class ProyectoSocioPeriodoJustificacionCrearComponent extends ActionComponent implements OnInit {
  PROYECTO_SOCIO_PERIODO_JUSTIFICACION_ROUTE_NAMES = PROYECTO_SOCIO_PERIODO_JUSTIFICACION_ROUTE_NAMES;

  textoCrear: string;
  textoCrearSuccess: string;
  textoCrearError: string;

  constructor(
    private readonly logger: NGXLogger,
    protected snackBarService: SnackBarService,
    router: Router,
    route: ActivatedRoute,
    public actionService: ProyectoSocioPeriodoJustificacionActionService,
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
      SOLICITUD_PROYECTO_PERIODO_JUSTIFICACION,
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
      SOLICITUD_PROYECTO_PERIODO_JUSTIFICACION,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SUCCESS,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoCrearSuccess = value);

    this.translate.get(
      SOLICITUD_PROYECTO_PERIODO_JUSTIFICACION,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_ERROR,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoCrearError = value);
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
          this.snackBarService.showError(this.textoCrearError);
        }
      },
      () => {
        this.snackBarService.showSuccess(this.textoCrearSuccess);
        const proyectoSocioPeriodoJustificacionId = this.actionService.getFragment(this.actionService.FRAGMENT.DATOS_GENERALES).getKey();
        this.router.navigate([`../${proyectoSocioPeriodoJustificacionId}`], { relativeTo: this.activatedRoute });
      }
    );
  }

  cancel(): void {
    this.router.navigate(['../'], { relativeTo: this.activatedRoute });
  }

}
