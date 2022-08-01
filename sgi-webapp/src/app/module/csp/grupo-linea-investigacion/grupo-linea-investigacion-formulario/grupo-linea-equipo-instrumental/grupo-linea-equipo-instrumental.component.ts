import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IGrupoLineaEquipoInstrumental } from '@core/models/csp/grupo-linea-equipo-instrumental';
import { DialogService } from '@core/services/dialog.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { GrupoLineaInvestigacionActionService } from '../../grupo-linea-investigacion.action.service';
import { GrupoLineaEquipoInstrumentalModalComponent, GrupoLineaEquipoInstrumentalModalData } from '../../modals/grupo-linea-equipo-instrumental-modal/grupo-linea-equipo-instrumental-modal.component';
import { GrupoLineaEquipoInstrumentalFragment } from './grupo-linea-equipo-instrumental.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const GRUPO_EQUIPO_INSTRUMENTAL_KEY = marker('csp.grupo-equipo.equipo-instrumental');
const MODAL_TITLE_KEY = marker('csp.grupo-equipo.equipo-instrumental');

@Component({
  selector: 'sgi-grupo-equipo-instrumental',
  templateUrl: './grupo-linea-equipo-instrumental.component.html',
  styleUrls: ['./grupo-linea-equipo-instrumental.component.scss']
})
export class GrupoLineaEquipoInstrumentalComponent extends FragmentComponent implements OnInit, OnDestroy {

  private subscriptions: Subscription[] = [];
  formPart: GrupoLineaEquipoInstrumentalFragment;

  elementosPagina = [5, 10, 25, 100];
  displayedColumns = ['numRegistro', 'nombre', 'descripcion', 'acciones'];

  modalTitleEntity: string;
  msgParamEntity = {};
  textoDelete: string;

  dataSource = new MatTableDataSource<StatusWrapper<IGrupoLineaEquipoInstrumental>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    public actionService: GrupoLineaInvestigacionActionService,
    private matDialog: MatDialog,
    private dialogService: DialogService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.EQUIPO_INSTRUMENTAL, actionService);
    this.formPart = this.fragment as GrupoLineaEquipoInstrumentalFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor =
      (wrapper: StatusWrapper<IGrupoLineaEquipoInstrumental>, property: string) => {
        switch (property) {
          default:
            return wrapper[property];
        }
      };
    this.dataSource.sort = this.sort;
    this.subscriptions.push(this.formPart.equiposInstrumentales$.subscribe(elements => {
      this.dataSource.data = elements;
    }));
  }

  private setupI18N(): void {

    this.translate.get(
      GRUPO_EQUIPO_INSTRUMENTAL_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      MODAL_TITLE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.modalTitleEntity = value);

    this.translate.get(
      GRUPO_EQUIPO_INSTRUMENTAL_KEY,
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
   * Apertura de modal del equipo instrumental (edición/creación)
   *
   * @param wrapper StatusWrapper<IGrupoLineaEquipoInstrumental>
   * @param rowIndex índice de la fila del listado
   */
  openModal(wrapper?: StatusWrapper<IGrupoLineaEquipoInstrumental>, rowIndex?: number): void {
    // Necesario para sincronizar los cambios de orden de registros dependiendo de la ordenación y paginación
    this.dataSource.sortData(this.dataSource.filteredData, this.dataSource.sort);
    const row = (this.paginator.pageSize * this.paginator.pageIndex) + rowIndex;

    const data: GrupoLineaEquipoInstrumentalModalData = {
      idGrupo: this.formPart.idGrupo,
      titleEntity: this.modalTitleEntity,
      entidad: wrapper?.value ?? {} as IGrupoLineaEquipoInstrumental,
      selectedEntidades: this.dataSource.data.map(element => element.value),
      isEdit: Boolean(wrapper)
    };

    if (wrapper) {
      const filtered = Object.assign([], data.selectedEntidades);
      filtered.splice(row, 1);
      data.selectedEntidades = filtered;
    }

    const config: MatDialogConfig = {
      data
    };
    const dialogRef = this.matDialog.open(GrupoLineaEquipoInstrumentalModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (modalData: GrupoLineaEquipoInstrumentalModalData) => {
        if (modalData) {
          if (!wrapper) {
            modalData.entidad.grupoLineaInvestigacion = this.actionService.grupoLineaInvestigacion;
            this.formPart.addGrupoLineaEquipoInstrumental(modalData.entidad as IGrupoLineaEquipoInstrumental);
          } else if (!wrapper.created) {
            const entidad = new StatusWrapper<IGrupoLineaEquipoInstrumental>(modalData.entidad as IGrupoLineaEquipoInstrumental);
            this.formPart.updateGrupoLineaEquipoInstrumental(entidad);
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
  deleteEquipo(wrapper: StatusWrapper<IGrupoLineaEquipoInstrumental>) {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.textoDelete).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.deleteGrupoLineaEquipoInstrumental(wrapper);
          }
        }
      )
    );
  }

}
