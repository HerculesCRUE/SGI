import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IEquipoTrabajo } from '@core/models/eti/equipo-trabajo';
import { IEquipoTrabajoWithIsEliminable } from '@core/models/eti/equipo-trabajo-with-is-eliminable';
import { ESTADO_MEMORIA } from '@core/models/eti/tipo-estado-memoria';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { DialogService } from '@core/services/dialog.service';
import { EquipoTrabajoService } from '@core/services/eti/equipo-trabajo.service';
import { PeticionEvaluacionService } from '@core/services/eti/peticion-evaluacion.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { SgiAuthService } from '@sgi/framework/auth';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { getPersonaEmailListConcatenated } from 'src/app/esb/sgp/shared/pipes/persona-email.pipe';
import { PeticionEvaluacionActionService } from '../../../peticion-evaluacion.action.service';
import {
  EquipoInvestigadorCrearModalComponent
} from '../equipo-investigador-crear-modal/equipo-investigador-crear-modal.component';
import { EquipoInvestigadorListadoFragment } from './equipo-investigador-listado.fragment';

const MSG_CONFIRM_DELETE = marker('msg.delete.entity');
const MSG_ERROR_INVESTIGADOR_REPETIDO = marker('error.eti.peticion-evaluacion.equipo-investigador.duplicate');
const PETICION_EVALUACION_EQUIPO_INVESTIGADOR_PERSONA_KEY = marker('eti.peticion-evaluacion.equipo-investigador.persona');

@Component({
  selector: 'sgi-equipo-investigador-listado',
  templateUrl: './equipo-investigador-listado.component.html',
  styleUrls: ['./equipo-investigador-listado.component.scss']
})
export class EquipoInvestigadorListadoComponent extends FragmentComponent implements OnInit, OnDestroy {

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  displayedColumns: string[];

  datasource: MatTableDataSource<StatusWrapper<IEquipoTrabajo>> = new MatTableDataSource<StatusWrapper<IEquipoTrabajoWithIsEliminable>>();

  private subscriptions: Subscription[] = [];
  private listadoFragment: EquipoInvestigadorListadoFragment;

  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;
  elementosPagina: number[] = [5, 10, 25, 100];

  msgParamEntity = {};
  textoDelete: string;

  constructor(
    protected matDialog: MatDialog,
    protected readonly dialogService: DialogService,
    protected readonly equipoTrabajoService: EquipoTrabajoService,
    protected readonly peticionEvaluacionService: PeticionEvaluacionService,
    protected readonly sgiAuthService: SgiAuthService,
    protected readonly snackBarService: SnackBarService,
    private actionService: PeticionEvaluacionActionService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.EQUIPO_INVESTIGADOR, actionService);
    this.listadoFragment = this.fragment as EquipoInvestigadorListadoFragment;

    this.displayedColumns = ['persona', 'nombreCompleto', 'vinculacion', 'nivelAcademico', 'acciones'];
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.actionService.initializeMemorias();

    this.datasource.paginator = this.paginator;
    this.datasource.sort = this.sort;
    this.listadoFragment.equiposTrabajo$.subscribe((equiposTrabajo) => {
      this.datasource.data = equiposTrabajo;
    });
    this.datasource.sortingDataAccessor =
      (wrapper: StatusWrapper<IEquipoTrabajoWithIsEliminable>, property: string) => {
        switch (property) {
          case 'persona':
            return getPersonaEmailListConcatenated(wrapper.value?.persona);
          case 'nombreCompleto':
            return wrapper.value?.persona?.nombre
              + ' ' + wrapper.value?.persona?.apellidos;
          default:
            return wrapper.value[property];
        }
      };
  }

  private setupI18N(): void {
    this.translate.get(
      PETICION_EVALUACION_EQUIPO_INVESTIGADOR_PERSONA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE });

    this.translate.get(
      PETICION_EVALUACION_EQUIPO_INVESTIGADOR_PERSONA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_CONFIRM_DELETE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoDelete = value);

  }

  /**
   * Abre la ventana modal para añadir una persona al equipo de trabajo
   */
  openModalAddEquipoTrabajo(): void {
    const dialogRef = this.matDialog.open(EquipoInvestigadorCrearModalComponent);

    dialogRef.afterClosed().subscribe(
      (equipoTrabajoAniadido: IEquipoTrabajoWithIsEliminable) => {
        if (equipoTrabajoAniadido) {
          const isRepetido =
            this.listadoFragment.equiposTrabajo$.value.some(equipoTrabajoWrapper =>
              equipoTrabajoAniadido.persona.id === equipoTrabajoWrapper.value.persona.id);

          if (isRepetido) {
            this.snackBarService.showError(MSG_ERROR_INVESTIGADOR_REPETIDO);
            return;
          }

          this.listadoFragment.addEquipoTrabajo(equipoTrabajoAniadido);
        }
      });

  }

  /**
   * Elimina la persona del equipo de trabajo.
   *
   * @param wrappedEquipoTrabajo equipo de trabajo a eliminar.
   */
  delete(wrappedEquipoTrabajo: StatusWrapper<IEquipoTrabajoWithIsEliminable>): void {
    const dialogSubscription = this.dialogService.showConfirmation(
      this.textoDelete
    ).subscribe((aceptado) => {
      if (aceptado) {
        this.listadoFragment.deleteEquipoTrabajo(wrappedEquipoTrabajo);
        this.actionService.eliminarTareasEquipoTrabajo(wrappedEquipoTrabajo);
      }
    });

    this.subscriptions.push(dialogSubscription);
  }

  ngOnDestroy(): void {
    this.subscriptions?.forEach(x => x.unsubscribe());
  }

  showDeleteEquipoTrabajo(personaRef: string): boolean {
    const memorias = this.listadoFragment.memorias.filter(memoria => {
      return memoria.estadoActual.id === ESTADO_MEMORIA.EN_ELABORACION ||
        memoria.estadoActual.id === ESTADO_MEMORIA.COMPLETADA ||
        memoria.estadoActual.id === ESTADO_MEMORIA.FAVORABLE_PENDIENTE_MODIFICACIONES_MINIMAS ||
        memoria.estadoActual.id === ESTADO_MEMORIA.PENDIENTE_CORRECCIONES ||
        memoria.estadoActual.id === ESTADO_MEMORIA.NO_PROCEDE_EVALUAR;
    });
    return (this.listadoFragment.solicitantePeticionEvaluacion?.id !== personaRef)
      && (memorias.length === this.listadoFragment.memorias.length);
  }

}
