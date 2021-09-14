import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { ActionComponent } from '@core/component/action.component';
import { HttpProblem } from '@core/errors/http-problem';
import { MSG_PARAMS } from '@core/i18n';
import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { switchMap } from 'rxjs/operators';
import {
  EquipoInvestigadorListadoComponent
} from '../peticion-evaluacion-formulario/equipo-investigador/equipo-investigador-listado/equipo-investigador-listado.component';
import {
  PeticionEvaluacionDatosGeneralesComponent
} from '../peticion-evaluacion-formulario/peticion-evaluacion-datos-generales/peticion-evaluacion-datos-generales.component';
import { PETICION_EVALUACION_ROUTE_NAMES } from '../peticion-evaluacion-route-names';
import { PeticionEvaluacionActionService } from '../peticion-evaluacion.action.service';

const MSG_BUTTON_SAVE = marker('btn.save');
const MSG_SUCCESS = marker('msg.save.entity.success');
const MSG_ERROR = marker('error.save.entity');
const PETICION_EVALUACION_KEY = marker('eti.peticion-evaluacion');

@Component({
  selector: 'sgi-peticion-evaluacion-crear',
  templateUrl: './peticion-evaluacion-crear.component.html',
  styleUrls: ['./peticion-evaluacion-crear.component.scss'],
  viewProviders: [
    PeticionEvaluacionActionService
  ]
})
export class PeticionEvaluacionCrearComponent extends ActionComponent implements OnInit {

  PETICION_EVALUACION_ROUTE_NAMES = PETICION_EVALUACION_ROUTE_NAMES;

  @ViewChild('datosGenerales', { static: true }) datosGenerales: PeticionEvaluacionDatosGeneralesComponent;
  @ViewChild('equipoInvestigadorListado', { static: true }) equipoInvestigadorListado: EquipoInvestigadorListadoComponent;

  textoCrear = MSG_BUTTON_SAVE;
  textoCrearSuccess: string;
  textoCrearError: string;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    private readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    router: Router,
    route: ActivatedRoute,
    public readonly actionService: PeticionEvaluacionActionService,
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
      PETICION_EVALUACION_KEY,
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
      PETICION_EVALUACION_KEY,
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
        if (error instanceof HttpProblem) {
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
        const peticionEvaluacionId = this.actionService.getFragment(this.actionService.FRAGMENT.DATOS_GENERALES).getKey();
        this.router.navigate([`../${peticionEvaluacionId}`], { relativeTo: this.activatedRoute });
      }
    );
  }

}
