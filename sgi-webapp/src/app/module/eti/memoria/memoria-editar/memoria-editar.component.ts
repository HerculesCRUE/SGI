import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { ActionComponent } from '@core/component/action.component';
import { SgiError } from '@core/errors/sgi-error';
import { MSG_PARAMS } from '@core/i18n';
import { COMITE } from '@core/models/eti/comite';
import { ESTADO_MEMORIA } from '@core/models/eti/tipo-estado-memoria';
import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { switchMap } from 'rxjs/operators';
import { MEMORIA_ROUTE_NAMES } from '../memoria-route-names';
import { MemoriaActionService } from '../memoria.action.service';
import { IndicarSubsanacionModalComponent, IndicarSubsanacionModalComponentData } from '../modals/indicar-subsanacion-modal/indicar-subsanacion-modal.component';

const MSG_BUTTON_SAVE = marker('btn.save');
const MSG_SUCCESS = marker('msg.update.entity.success');
const MSG_ERROR = marker('error.update.entity');
const MSG_INDICAR_SUBSANACION_SUCCESS = marker('msg.csp.indicar-subsanacion.success');
const MEMORIA_KEY = marker('eti.memoria');
const MSG_ERROR_FORMULARIO = marker('eti.memoria.formulario.error')

@Component({
  selector: 'sgi-memoria-editar',
  templateUrl: './memoria-editar.component.html',
  styleUrls: ['./memoria-editar.component.scss'],
  viewProviders: [
    MemoriaActionService
  ]
})
export class MemoriaEditarComponent extends ActionComponent implements OnInit {
  MEMORIA_ROUTE_NAMES = MEMORIA_ROUTE_NAMES;

  textoActualizar = MSG_BUTTON_SAVE;
  textoActualizarSuccess: string;
  textoActualizarError: string;
  textoErrorFormulario: string;

  showIndicarSubsanacionBtn = false;

  private readonly estadosEnableIndicarSubsanacion = [ESTADO_MEMORIA.EN_SECRETARIA];

  private from: string;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  get COMITE() {
    return COMITE;
  }

  constructor(
    private readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    router: Router,
    route: ActivatedRoute,
    public actionService: MemoriaActionService,
    dialogService: DialogService,
    private matDialog: MatDialog,
    private readonly translate: TranslateService
  ) {
    super(router, route, actionService, dialogService);
    this.from = history.state.from;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.showIndicarSubsanacionBtn = this.estadosEnableIndicarSubsanacion.includes(this.actionService.getEstadoMemoria().id) && !this.actionService.isModuleInv();
  }

  private setupI18N(): void {
    this.translate.get(
      MEMORIA_KEY,
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
      MEMORIA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_ERROR,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoActualizarError = value);

    this.translate.get(
      MSG_ERROR_FORMULARIO
    ).subscribe((value) => this.textoErrorFormulario = value);
  }

  saveOrUpdate(action: 'save' | 'indicar-subsanacion'): void {
    if (action === 'indicar-subsanacion') {
      this.openIndicarSubsanacion();
    } else {
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
            if (error === MSG_ERROR_FORMULARIO) {
              this.snackBarService.showError(this.textoErrorFormulario);
            } else {
              this.snackBarService.showError(this.textoActualizarError);
            }
          }
        },
        () => {
          this.snackBarService.showSuccess(this.textoActualizarSuccess);
        }
      );
    }
  }

  cancel(): void {
    if (this.from) {
      this.router.navigateByUrl(this.from);
    }
    else {
      super.cancel();
    }
  }

  /**
   * Apertura de modal para indicar subsanacion
   */
  private openIndicarSubsanacion(): void {
    const data: IndicarSubsanacionModalComponentData = {
      memoriaId: this.actionService.getMemoria().id
    };
    const config = {
      data
    };
    const dialogRef = this.matDialog.open(IndicarSubsanacionModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (modalData: IndicarSubsanacionModalComponentData) => {
        if (modalData) {
          this.snackBarService.showSuccess(MSG_INDICAR_SUBSANACION_SUCCESS);
          this.router.navigate(['../'], { relativeTo: this.activatedRoute });
        }
      }
    );
  }

}
