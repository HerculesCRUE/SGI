import { Component, OnDestroy, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IEvaluacionWithIsEliminable } from '@core/models/eti/evaluacion-with-is-eliminable';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { DialogService } from '@core/services/dialog.service';
import { EvaluacionService } from '@core/services/eti/evaluacion.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { BehaviorSubject, Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { ConvocatoriaReunionActionService } from '../../../convocatoria-reunion.action.service';
import { ConvocatoriaReunionAsignacionMemoriasModalComponent, ConvocatoriaReunionAsignacionMemoriasModalComponentData } from '../convocatoria-reunion-asignacion-memorias-modal/convocatoria-reunion-asignacion-memorias-modal.component';
import { ConvocatoriaReunionAsignacionMemoriasListadoFragment } from './convocatoria-reunion-asignacion-memorias-listado.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const MEMORIA_KEY = marker('eti.memoria');
@Component({
  selector: 'sgi-convocatoria-reunion-asignacion-memorias-listado',
  templateUrl: './convocatoria-reunion-asignacion-memorias-listado.component.html',
  styleUrls: ['./convocatoria-reunion-asignacion-memorias-listado.component.scss']
})
export class ConvocatoriaReunionAsignacionMemoriasListadoComponent extends FragmentComponent implements OnInit, OnDestroy {

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  displayedColumns: string[];

  datasource: MatTableDataSource<StatusWrapper<IEvaluacionWithIsEliminable>> =
    new MatTableDataSource<StatusWrapper<IEvaluacionWithIsEliminable>>();
  private evaluaciones$: BehaviorSubject<StatusWrapper<IEvaluacionWithIsEliminable>[]> =
    new BehaviorSubject<StatusWrapper<IEvaluacionWithIsEliminable>[]>([]);

  private listadoFragment: ConvocatoriaReunionAsignacionMemoriasListadoFragment;
  disableAsignarMemorias: boolean;
  private subscriptions: Subscription[] = [];

  msgParamEnity = {};
  textoDelete: string;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  get readonly(): boolean {
    return this.actionService.readonly;
  }

  constructor(
    private readonly matDialog: MatDialog,
    protected readonly evaluacionService: EvaluacionService,
    protected readonly dialogService: DialogService,
    protected readonly personaService: PersonaService,
    protected readonly snackBarService: SnackBarService,
    private actionService: ConvocatoriaReunionActionService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.ASIGNACION_MEMORIAS, actionService);
    this.listadoFragment = this.fragment as ConvocatoriaReunionAsignacionMemoriasListadoFragment;
    this.evaluaciones$ = (this.fragment as ConvocatoriaReunionAsignacionMemoriasListadoFragment).evaluaciones$;

    this.displayedColumns = ['numReferencia', 'evaluador1', 'evaluador2', 'acciones'];
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.actionService.initializeDatosGenerales();
    this.evaluaciones$.subscribe((evaluaciones) => {
      this.datasource.data = evaluaciones;
    });

    this.datasource.sortingDataAccessor =
      (wrapper: StatusWrapper<IEvaluacionWithIsEliminable>, property: string) => {
        switch (property) {
          case 'numReferencia':
            return wrapper.value.memoria?.numReferencia;
          case 'evaluador1':
            return wrapper.value.evaluador1?.persona?.nombre
              + ' ' + wrapper.value.evaluador1?.persona?.apellidos;
          case 'evaluador2':
            return wrapper.value.evaluador2?.persona?.nombre
              + ' ' + wrapper.value.evaluador2?.persona?.apellidos;
          default:
            return wrapper.value[property];
        }
      };
  }

  private setupI18N(): void {
    this.translate.get(
      MEMORIA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEnity = { entity: value, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      MEMORIA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_DELETE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoDelete = value);
  }

  ngOnDestroy() {
    this.subscriptions?.forEach(x => x.unsubscribe());
  }

  /**
   * Abre una ventana modal para añadir una nueva asignación de memoria al listado de memorias de una evaluación.
   *
   * Esta ventana solo se debería abrir si tenemos idConvocatoriaReunion (estamos modificando) o cuando estamos
   * creando una nueva ConvocatoriaReunion y ya tenemos establecidos los valores necesarios para obtener las memorias
   * asignables (Comité, Tipo Convocatoria y Fecha Límite)
   *
   */
  openDialogAsignarMemoria(): void {
    const evaluacion: IEvaluacionWithIsEliminable = {
      activo: true,
      comite: null,
      convocatoriaReunion: this.actionService.getDatosGeneralesConvocatoriaReunion(),
      dictamen: null,
      esRevMinima: null,
      evaluador1: null,
      evaluador2: null,
      fechaDictamen: null,
      id: null,
      memoria: null,
      tipoEvaluacion: null,
      version: null,
      comentario: null,
      eliminable: true
    };

    const data: ConvocatoriaReunionAsignacionMemoriasModalComponentData = {
      idConvocatoria: this.listadoFragment.getKey() as number,
      filterMemoriasAsignables: this.actionService.getDatosAsignacion(),
      memoriasAsignadas: this.listadoFragment.evaluaciones$.value.map(evc => evc.value.memoria),
      evaluacion,
      readonly: this.actionService.readonly
    };
    const config = {
      panelClass: 'sgi-dialog-container',
      data
    };

    const dialogRef = this.matDialog.open(ConvocatoriaReunionAsignacionMemoriasModalComponent, config);

    this.subscriptions.push(
      dialogRef.afterClosed().subscribe(
        (modelData: ConvocatoriaReunionAsignacionMemoriasModalComponentData) => {
          if (modelData && modelData.evaluacion) {
            this.listadoFragment.addEvaluacion(modelData.evaluacion);
          }
        }
      ));
  }

  /**
   * Elimina la evaluación.
   * @param evaluacionId id de la evaluacion a eliminar.
   * @param event evento lanzado.
   */
  borrar(wrappedEvaluacion: StatusWrapper<IEvaluacionWithIsEliminable>): void {
    const dialogSubscription = this.dialogService.showConfirmation(
      this.textoDelete
    ).subscribe((aceptado) => {
      if (aceptado) {
        this.listadoFragment.deleteEvaluacion(wrappedEvaluacion);
      }
    });

    this.subscriptions.push(dialogSubscription);
  }

  /**
   * Abre la ventana modal para modificar una evaluación
   *
   * @param evaluacion evaluación a modificar
   */
  openUpdateModal(evaluacion: StatusWrapper<IEvaluacionWithIsEliminable>): void {
    const data: ConvocatoriaReunionAsignacionMemoriasModalComponentData = {
      idConvocatoria: this.listadoFragment.getKey() as number,
      filterMemoriasAsignables: this.actionService.getDatosAsignacion(),
      memoriasAsignadas: this.listadoFragment.evaluaciones$.value.map(evc => evc.value.memoria),
      evaluacion: evaluacion.value,
      readonly: this.actionService.readonly
    };
    const config = {
      panelClass: 'sgi-dialog-container',
      data
    };

    const dialogRef = this.matDialog.open(ConvocatoriaReunionAsignacionMemoriasModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (modelData: ConvocatoriaReunionAsignacionMemoriasModalComponentData) => {
        if (modelData && modelData.evaluacion) {
          evaluacion.setEdited();
          this.fragment.setChanges(true);
          this.fragment.setComplete(true);
        }
      }
    );
  }
}
