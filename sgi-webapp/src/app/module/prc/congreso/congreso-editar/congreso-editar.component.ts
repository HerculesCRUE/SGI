import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { ActionComponent } from '@core/component/action.component';
import { MSG_PARAMS } from '@core/i18n';
import { DialogService } from '@core/services/dialog.service';
import { IEstadoProduccionCientificaRequest } from '@core/services/prc/estado-produccion-cientifica/estado-produccion-cientifica-input';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { switchMap } from 'rxjs/operators';
import { RechazarProduccionCientificaModalComponent } from '../../shared/modals/rechazar-produccion-cientifica-modal/rechazar-produccion-cientifica-modal.component';
import { CONGRESO_ROUTE_NAMES } from '../congreso-route-names';
import { CongresoActionService } from '../congreso.action.service';

const MSG_VALIDATE_SUCCESS = marker('msg.validate.entity.success');
const MSG_VALIDATE_ERROR = marker('error.validate.entity');
const MSG_REJECT_SUCCESS = marker('msg.reject.entity.success');
const MSG_REJECT_ERROR = marker('error.reject.entity');
const PRODUCCION_CIENTIFICA_KEY = marker('prc.produccion-cientifica');

@Component({
  selector: 'sgi-congreso-editar',
  templateUrl: './congreso-editar.component.html',
  styleUrls: ['./congreso-editar.component.scss'],
  viewProviders: [
    CongresoActionService
  ]
})
export class CongresoEditarComponent extends ActionComponent implements OnInit {

  CONGRESO_ROUTE_NAMES = CONGRESO_ROUTE_NAMES;

  isProduccionCientificaEditable: boolean;
  private textoValidateSuccess: string;
  private textoValidateError: string;
  private textoRejectSuccess: string;
  private textoRejectError: string;

  constructor(
    private readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    protected readonly router: Router,
    protected readonly route: ActivatedRoute,
    public readonly actionService: CongresoActionService,
    dialogService: DialogService,
    private matDialog: MatDialog,
    private readonly translate: TranslateService,
  ) {
    super(router, route, actionService, dialogService);
    this.subscriptions.push(
      this.actionService.isProduccionCientificaEditable$()
        .subscribe(isEditable => this.isProduccionCientificaEditable = isEditable)
    );
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  private setupI18N(): void {
    this.translate.get(
      PRODUCCION_CIENTIFICA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_VALIDATE_SUCCESS,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoValidateSuccess = value);

    this.translate.get(
      PRODUCCION_CIENTIFICA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_VALIDATE_ERROR,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoValidateError = value);

    this.translate.get(
      PRODUCCION_CIENTIFICA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_REJECT_SUCCESS,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoRejectSuccess = value);

    this.translate.get(
      PRODUCCION_CIENTIFICA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_REJECT_ERROR,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoRejectError = value);

  }

  saveOrUpdate(action?: 'validar' | 'rechazar'): void {
    if (action === 'validar') {
      this.validar();
    } else if (action === 'rechazar') {
      const dialogRef = this.matDialog.open(RechazarProduccionCientificaModalComponent);
      dialogRef.afterClosed().subscribe(
        (estadoProduccionCientifica: IEstadoProduccionCientificaRequest) => {
          if (estadoProduccionCientifica) {
            this.rechazar(estadoProduccionCientifica);
          }
        }
      );
    }
  }

  validar(): void {
    this.actionService.validar()
      .subscribe(
        () => this.snackBarService.showSuccess(this.textoValidateSuccess),
        (error) => this.snackBarService.showError(this.textoValidateError)
      );
  }

  rechazar(estadoProduccionCientifica: IEstadoProduccionCientificaRequest): void {
    this.actionService.rechazar(estadoProduccionCientifica)
      .subscribe(
        () => this.snackBarService.showSuccess(this.textoRejectSuccess),
        (error) => this.snackBarService.showError(this.textoRejectError)
      );
  }
}
