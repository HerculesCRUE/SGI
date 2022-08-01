import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IGrupoLineaInvestigador } from '@core/models/csp/grupo-linea-investigador';
import { DialogService } from '@core/services/dialog.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { getPersonaEmailListConcatenated } from 'src/app/esb/sgp/shared/pipes/persona-email.pipe';
import { GrupoLineaInvestigacionActionService } from '../../grupo-linea-investigacion.action.service';
import { GrupoLineaInvestigadorModalComponent, GrupoLineaInvestigadorModalData } from '../../modals/grupo-linea-investigador-modal/grupo-linea-investigador-modal.component';
import { GrupoLineaInvestigadorFragment } from './grupo-linea-investigador.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const GRUPO_LINEA_INVESTIGADOR_KEY = marker('csp.grupo-linea-investigador');
const MODAL_TITLE_KEY = marker('csp.grupo-linea-investigador');

@Component({
  selector: 'sgi-grupo-linea-investigador',
  templateUrl: './grupo-linea-investigador.component.html',
  styleUrls: ['./grupo-linea-investigador.component.scss']
})
export class GrupoLineaInvestigadorComponent extends FragmentComponent implements OnInit, OnDestroy {

  private subscriptions: Subscription[] = [];
  formPart: GrupoLineaInvestigadorFragment;

  elementosPagina = [5, 10, 25, 100];
  displayedColumns = ['email', 'nombre', 'apellidos', 'fechaInicio', 'fechaFin', 'acciones'];

  modalTitleEntity: string;
  msgParamEntity = {};
  textoDelete: string;

  disabledAdd = false;

  dataSource = new MatTableDataSource<StatusWrapper<IGrupoLineaInvestigador>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    public actionService: GrupoLineaInvestigacionActionService,
    private matDialog: MatDialog,
    private dialogService: DialogService,
    private readonly translate: TranslateService,
  ) {
    super(actionService.FRAGMENT.LINEA_INVESTIGADOR, actionService);
    this.formPart = this.fragment as GrupoLineaInvestigadorFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor =
      (wrapper: StatusWrapper<IGrupoLineaInvestigador>, property: string) => {
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
    this.subscriptions.push(this.formPart.lineasInvestigadores$.subscribe(elements => {
      this.disabledAdd = elements.length === 1 && !!!elements[0].value.fechaInicio;
      this.dataSource.data = elements;
    }));
  }

  private setupI18N(): void {
    this.translate.get(
      GRUPO_LINEA_INVESTIGADOR_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      MODAL_TITLE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.modalTitleEntity = value);

    this.translate.get(
      GRUPO_LINEA_INVESTIGADOR_KEY,
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
   * Apertura de modal de la línea investigador (edición/creación)
   *
   * @param wrapper StatusWrapper<IGrupoLineaInvestigador>
   * @param rowIndex índice de la fila del listado
   */
  openModal(wrapper?: StatusWrapper<IGrupoLineaInvestigador>, rowIndex?: number): void {
    // Necesario para sincronizar los cambios de orden de registros dependiendo de la ordenación y paginación
    this.dataSource.sortData(this.dataSource.filteredData, this.dataSource.sort);
    const row = (this.paginator.pageSize * this.paginator.pageIndex) + rowIndex;

    const data: GrupoLineaInvestigadorModalData = {
      titleEntity: this.modalTitleEntity,
      idGrupo: this.formPart.idGrupo,
      entidad: wrapper?.value ?? {} as IGrupoLineaInvestigador,
      selectedEntidades: this.dataSource.data.map(element => element.value),
      fechaInicioMin: this.actionService.grupoListadoInvestigacion?.fechaInicio,
      fechaFinMax: this.actionService.grupoListadoInvestigacion?.fechaFin ?? LuxonUtils.fromBackend('2500-01-01T23:59:59Z'),
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
    const dialogRef = this.matDialog.open(GrupoLineaInvestigadorModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (modalData: GrupoLineaInvestigadorModalData) => {
        if (modalData) {
          if (!wrapper) {
            modalData.entidad.grupoLineaInvestigacion = this.actionService.grupoLineaInvestigacion;
            this.formPart.addGrupoLineaInvestigador(modalData.entidad as IGrupoLineaInvestigador);
          } else if (!wrapper.created) {
            const entidad = new StatusWrapper<IGrupoLineaInvestigador>(modalData.entidad as IGrupoLineaInvestigador);
            this.formPart.updateGrupoLineaInvestigador(entidad);
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
  deleteEquipo(wrapper: StatusWrapper<IGrupoLineaInvestigador>) {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.textoDelete).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.deleteGrupoLineaInvestigador(wrapper);
          }
        }
      )
    );
  }

}
