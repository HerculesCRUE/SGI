import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IGrupoResponsableEconomico } from '@core/models/csp/grupo-responsable-economico';
import { GrupoService } from '@core/services/csp/grupo/grupo.service';
import { DialogService } from '@core/services/dialog.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { getPersonaEmailListConcatenated } from 'src/app/esb/sgp/shared/pipes/persona-email.pipe';
import { GrupoActionService } from '../../grupo.action.service';
import { GrupoResponsableEconomicoModalComponent, GrupoResponsableEconomicoModalData } from '../../modals/grupo-responsable-economico-modal/grupo-responsable-economico-modal.component';
import { GrupoResponsableEconomicoFragment } from './grupo-responsable-economico.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const PROYECTO_EQUIPO_MIEMBRO_KEY = marker('csp.grupo-responsable-economico');
const MODAL_TITLE_KEY = marker('csp.grupo-responsable-economico');

@Component({
  selector: 'sgi-grupo-responsable-economico',
  templateUrl: './grupo-responsable-economico.component.html',
  styleUrls: ['./grupo-responsable-economico.component.scss']
})
export class GrupoResponsableEconomicoComponent extends FragmentComponent implements OnInit, OnDestroy {

  private subscriptions: Subscription[] = [];
  formPart: GrupoResponsableEconomicoFragment;

  elementosPagina = [5, 10, 25, 100];
  displayedColumns = ['email', 'nombre', 'apellidos', 'fechaInicio', 'fechaFin', 'acciones'];

  modalTitleEntity: string;
  msgParamEntity = {};
  textoDelete: string;

  disabledAdd = false;

  dataSource = new MatTableDataSource<StatusWrapper<IGrupoResponsableEconomico>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    protected proyectoService: GrupoService,
    public actionService: GrupoActionService,
    private matDialog: MatDialog,
    private dialogService: DialogService,
    private readonly translate: TranslateService,
  ) {
    super(actionService.FRAGMENT.RESPONSABLE_ECONOMICO, actionService);
    this.formPart = this.fragment as GrupoResponsableEconomicoFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor =
      (wrapper: StatusWrapper<IGrupoResponsableEconomico>, property: string) => {
        switch (property) {
          case 'email':
            return getPersonaEmailListConcatenated(wrapper.value.persona);
          case 'nombre':
            return wrapper.value.persona.nombre;
          case 'apellidos':
            return wrapper.value.persona.apellidos;
          case 'fechaInicio':
            return wrapper.value.fechaInicio;
          case 'fechaFin':
            return wrapper.value.fechaFin;
          default:
            return wrapper[property];
        }
      };
    this.dataSource.sort = this.sort;
    this.subscriptions.push(this.formPart.responsablesEconomicos$.subscribe(elements => {
      this.disabledAdd = elements.length === 1 && !!!elements[0].value.fechaInicio;
      this.dataSource.data = elements;
    }));
  }

  private setupI18N(): void {
    this.translate.get(
      PROYECTO_EQUIPO_MIEMBRO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      MODAL_TITLE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.modalTitleEntity = value);

    this.translate.get(
      PROYECTO_EQUIPO_MIEMBRO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_DELETE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoDelete = value);

  }

  /**
   * Apertura de modal de responsable económico (edición/creación)
   *
   * @param wrapper StatusWrapper<IGrupoResponsableEconomico>
   * @param rowIndex índice de la fila del listado
   */
  openModal(wrapper?: StatusWrapper<IGrupoResponsableEconomico>, rowIndex?: number): void {
    // Necesario para sincronizar los cambios de orden de registros dependiendo de la ordenación y paginación
    this.dataSource.sortData(this.dataSource.filteredData, this.dataSource.sort);
    const row = (this.paginator.pageSize * this.paginator.pageIndex) + rowIndex;

    const data: GrupoResponsableEconomicoModalData = {
      titleEntity: this.modalTitleEntity,
      entidad: wrapper?.value ?? {} as IGrupoResponsableEconomico,
      selectedEntidades: this.dataSource.data.map(element => element.value),
      fechaInicioMin: this.actionService.grupo.fechaInicio,
      fechaFinMax: this.actionService.grupo.fechaFin ?? LuxonUtils.fromBackend('2500-01-01T23:59:59Z'),
      isEdit: Boolean(wrapper),
    };

    if (wrapper) {
      const filtered = Object.assign([], data.selectedEntidades);
      filtered.splice(row, 1);
      data.selectedEntidades = filtered;
    }

    const config: MatDialogConfig = {
      data
    };
    const dialogRef = this.matDialog.open(GrupoResponsableEconomicoModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (modalData: GrupoResponsableEconomicoModalData) => {
        if (modalData) {
          if (!wrapper) {
            modalData.entidad.grupo = this.actionService.grupo;
            this.formPart.addGrupoResponsableEconomico(modalData.entidad as IGrupoResponsableEconomico);
          } else if (!wrapper.created) {
            const entidad = new StatusWrapper<IGrupoResponsableEconomico>(modalData.entidad as IGrupoResponsableEconomico);
            this.formPart.updateGrupoResponsableEconomico(entidad);
          }
        }
      }
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  /**
   * Eliminar grupo equipo
   */
  deleteEquipo(wrapper: StatusWrapper<IGrupoResponsableEconomico>) {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.textoDelete).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.deleteGrupoResponsableEconomico(wrapper);
          }
        }
      )
    );
  }

}
