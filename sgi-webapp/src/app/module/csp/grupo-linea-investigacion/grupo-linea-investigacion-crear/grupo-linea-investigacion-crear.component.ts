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
import { GRUPO_LINEA_INVESTIGACION_ROUTE_NAMES } from '../grupo-linea-investigacion-route-names';
import { GrupoLineaInvestigacionActionService } from '../grupo-linea-investigacion.action.service';

const MSG_BUTTON_SAVE = marker('btn.save');
const MSG_SUCCESS = marker('msg.save.entity.success');
const MSG_ERROR = marker('error.save.entity');
const GRUPO_LINEA_INVESTIGACION_KEY = marker('csp.grupo-linea-investigacion');

@Component({
  selector: 'sgi-grupo-linea-investigacion-crear',
  templateUrl: './grupo-linea-investigacion-crear.component.html',
  styleUrls: ['./grupo-linea-investigacion-crear.component.scss'],
  viewProviders: [
    GrupoLineaInvestigacionActionService
  ]
})
export class GrupoLineaInvestigacionCrearComponent extends ActionComponent implements OnInit {
  GRUPO_LINEA_INVESTIGACION_ROUTE_NAMES = GRUPO_LINEA_INVESTIGACION_ROUTE_NAMES;
  textoActualizar = MSG_BUTTON_SAVE;
  textoActualizarSuccess: string;
  textoActualizarError: string;

  constructor(
    private readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    router: Router,
    route: ActivatedRoute,
    public actionService: GrupoLineaInvestigacionActionService,
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
      GRUPO_LINEA_INVESTIGACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SUCCESS,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoActualizarSuccess = value);

    this.translate.get(
      GRUPO_LINEA_INVESTIGACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_ERROR,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
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
        const grupoLineaInvestigacionId = this.actionService.getFragment(this.actionService.FRAGMENT.DATOS_GENERALES).getKey();
        this.router.navigate([`../${grupoLineaInvestigacionId}`], { relativeTo: this.activatedRoute });
      }
    );
  }

  cancel(): void {
    this.router.navigate(['../'], { relativeTo: this.activatedRoute });
  }
}
