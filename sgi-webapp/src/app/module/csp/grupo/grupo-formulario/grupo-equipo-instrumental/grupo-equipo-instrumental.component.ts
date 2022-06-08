import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IGrupoEquipoInstrumental } from '@core/models/csp/grupo-equipo-instrumental';
import { GrupoEquipoInstrumentalService } from '@core/services/csp/grupo-equipo-instrumental/grupo-equipo-instrumental.service';
import { GrupoService } from '@core/services/csp/grupo/grupo.service';
import { DialogService } from '@core/services/dialog.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { GrupoActionService } from '../../grupo.action.service';
import { GrupoEquipoInstrumentalModalComponent, GrupoEquipoInstrumentalModalData } from '../../modals/grupo-equipo-instrumental-modal/grupo-equipo-instrumental-modal.component';
import { GrupoEquipoInstrumentalFragment } from './grupo-equipo-instrumental.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const MSG_NO_DELETE = marker('csp.grupo-equipo.equipo-instrumental.delete');
const GRUPO_EQUIPO_INSTRUMENTAL_KEY = marker('csp.grupo-equipo.equipo-instrumental');
const MODAL_TITLE_KEY = marker('csp.grupo-equipo.equipo-instrumental');

@Component({
  selector: 'sgi-grupo-equipo-instrumental',
  templateUrl: './grupo-equipo-instrumental.component.html',
  styleUrls: ['./grupo-equipo-instrumental.component.scss']
})
export class GrupoEquipoInstrumentalComponent extends FragmentComponent implements OnInit, OnDestroy {

  private subscriptions: Subscription[] = [];
  formPart: GrupoEquipoInstrumentalFragment;

  elementosPagina = [5, 10, 25, 100];
  displayedColumns = ['numRegistro', 'nombre', 'descripcion', 'acciones'];

  modalTitleEntity: string;
  msgParamEntity = {};
  textoDelete: string;
  textoNoDelete: string;

  dataSource = new MatTableDataSource<StatusWrapper<IGrupoEquipoInstrumental>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    protected grupoEquipoService: GrupoEquipoInstrumentalService,
    public actionService: GrupoActionService,
    private matDialog: MatDialog,
    private dialogService: DialogService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.EQUIPO_INSTRUMENTAL, actionService);
    this.formPart = this.fragment as GrupoEquipoInstrumentalFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor =
      (wrapper: StatusWrapper<IGrupoEquipoInstrumental>, property: string) => {
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

    this.translate.get(
      MSG_NO_DELETE,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.textoNoDelete = value);

  }

  /**
   * Apertura de modal de equipos (edición/creación)
   *
   * @param idEquipo Identificador de equipo a editar.
   */
  openModal(wrapper?: StatusWrapper<IGrupoEquipoInstrumental>, rowIndex?: number): void {
    // Necesario para sincronizar los cambios de orden de registros dependiendo de la ordenación y paginación
    this.dataSource.sortData(this.dataSource.filteredData, this.dataSource.sort);
    const row = (this.paginator.pageSize * this.paginator.pageIndex) + rowIndex;

    const data: GrupoEquipoInstrumentalModalData = {
      titleEntity: this.modalTitleEntity,
      entidad: wrapper?.value ?? {} as IGrupoEquipoInstrumental,
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
    const dialogRef = this.matDialog.open(GrupoEquipoInstrumentalModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (modalData: GrupoEquipoInstrumentalModalData) => {
        if (modalData) {
          if (!wrapper) {
            modalData.entidad.grupo = this.actionService.grupo;
            this.formPart.addGrupoEquipoInstrumental(modalData.entidad as IGrupoEquipoInstrumental);
          } else if (!wrapper.created) {
            const entidad = new StatusWrapper<IGrupoEquipoInstrumental>(modalData.entidad as IGrupoEquipoInstrumental);
            this.formPart.updateGrupoEquipoInstrumental(entidad);
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
  deleteEquipo(wrapper: StatusWrapper<IGrupoEquipoInstrumental>) {
    this.grupoEquipoService.existsGrupoEquipoInstrumentalInGrupoLineaEquipoInstrumental(wrapper.value.id).subscribe(res => {
      if (res) {
        this.subscriptions.push(this.dialogService.showConfirmation(this.textoNoDelete).subscribe());
      } else {
        this.subscriptions.push(
          this.dialogService.showConfirmation(this.textoDelete).subscribe(
            (aceptado) => {
              if (aceptado) {
                this.formPart.deleteGrupoEquipoInstrumental(wrapper);
              }
            }
          )
        );
      }
    });
  }

}
