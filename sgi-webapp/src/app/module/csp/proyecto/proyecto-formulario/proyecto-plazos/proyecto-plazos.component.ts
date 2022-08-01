import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IProyectoFase } from '@core/models/csp/proyecto-fase';
import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { BehaviorSubject, Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { ProyectoPlazosModalComponent, ProyectoPlazosModalComponentData } from '../../modals/proyecto-plazos-modal/proyecto-plazos-modal.component';
import { ProyectoActionService } from '../../proyecto.action.service';
import { ProyectoPlazosFragment } from './proyecto-plazos.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const PROYECTO_FASE_KEY = marker('csp.proyecto-fase');

@Component({
  selector: 'sgi-proyecto-plazos',
  templateUrl: './proyecto-plazos.component.html',
  styleUrls: ['./proyecto-plazos.component.scss']
})
export class ProyectoPlazosComponent extends FragmentComponent implements OnInit, OnDestroy {
  formPart: ProyectoPlazosFragment;
  private subscriptions: Subscription[] = [];

  displayedColumns = ['fechaInicio', 'fechaFin', 'tipoFase', 'observaciones', 'aviso', 'acciones'];
  elementosPagina = [5, 10, 25, 100];

  msgParamEntity = {};
  textoDelete: string;

  dataSource: MatTableDataSource<StatusWrapper<IProyectoFase>>;
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  plazos$: BehaviorSubject<StatusWrapper<IProyectoFase>[]>;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    protected snackBarService: SnackBarService,
    public actionService: ProyectoActionService,
    private matDialog: MatDialog,
    private dialogService: DialogService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.FASES, actionService);
    this.formPart = this.fragment as ProyectoPlazosFragment;
    this.plazos$ = (this.fragment as ProyectoPlazosFragment).plazos$;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.dataSource = new MatTableDataSource<StatusWrapper<IProyectoFase>>();
    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor = (wrapper, property) => {
      if (property === 'aviso') {
        return !!wrapper.value.aviso1 || !!wrapper.value.aviso2 ? 's' : 'n';
      }
      return wrapper.value[property];
    }
    this.dataSource.sort = this.sort;

    this.subscriptions.push(this.formPart.plazos$.subscribe(elements => {
      this.dataSource.data = elements;
    }));
  }

  private setupI18N(): void {
    this.translate.get(
      PROYECTO_FASE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      PROYECTO_FASE_KEY,
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

  /**
   * Apertura de modal de plazos fase
   * @param plazo Identificador de plazos fase al guardar/editar
   */
  openModalPlazos(plazo?: StatusWrapper<IProyectoFase>): void {
    const datosPlazosFases: ProyectoPlazosModalComponentData = {
      plazos: this.dataSource.data.filter(existing => existing !== plazo).map(wrapper => wrapper.value),
      plazo: plazo ? plazo.value : {} as IProyectoFase,
      idModeloEjecucion: this.actionService.modeloEjecucionId,
      readonly: this.actionService.readonly,
      tituloProyecto: this.actionService.titulo,
      unidadGestionId: this.actionService.unidadGestionId,
      convocatoriaId: this.actionService.convocatoriaId
    };

    const config = {
      data: datosPlazosFases,
    };

    const dialogRef = this.matDialog.open(ProyectoPlazosModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (modalData: ProyectoPlazosModalComponentData) => {
        if (modalData) {
          if (plazo) {
            if (!plazo.created) {
              plazo.setEdited();
            }
            this.formPart.setChanges(true);
          } else {
            this.formPart.addPlazos(modalData.plazo);
          }
        }
      }
    );
  }

  /**
   * Desactivar proyecto fase
   */
  deleteFase(wrapper: StatusWrapper<IProyectoFase>) {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.textoDelete).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.deletePlazo(wrapper);
          }
        }
      )
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }
}
