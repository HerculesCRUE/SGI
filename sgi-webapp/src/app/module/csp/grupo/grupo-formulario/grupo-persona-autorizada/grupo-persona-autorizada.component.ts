import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IGrupoPersonaAutorizada } from '@core/models/csp/grupo-persona-autorizada';
import { GrupoService } from '@core/services/csp/grupo/grupo.service';
import { DialogService } from '@core/services/dialog.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { LuxonDateAdapter } from '@material/luxon-date-adapter';
import { TranslateService } from '@ngx-translate/core';
import { LuxonDatePipe } from '@shared/luxon-date-pipe';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { getPersonaEmailListConcatenated } from 'src/app/esb/sgp/shared/pipes/persona-email.pipe';
import { GrupoActionService } from '../../grupo.action.service';
import { GrupoPersonaAutorizadaModalComponent, GrupoPersonaAutorizadaModalData } from '../../modals/grupo-persona-autorizada-modal/grupo-persona-autorizada-modal.component';
import { GrupoPersonaAutorizadaFragment } from './grupo-persona-autorizada.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const GRUPO_PERSONA_AUTORIZADA_KEY = marker('csp.grupo-persona-autorizada');
const MODAL_TITLE_KEY = marker('csp.grupo-persona-autorizada');

@Component({
  selector: 'sgi-grupo-persona-autorizada',
  templateUrl: './grupo-persona-autorizada.component.html',
  styleUrls: ['./grupo-persona-autorizada.component.scss']
})
export class GrupoPersonaAutorizadaComponent extends FragmentComponent implements OnInit, OnDestroy {

  private subscriptions: Subscription[] = [];
  formPart: GrupoPersonaAutorizadaFragment;

  elementosPagina = [5, 10, 25, 100];
  displayedColumns = ['email', 'nombre', 'apellidos', 'fechaInicio', 'fechaFin', 'acciones'];

  modalTitleEntity: string;
  msgParamEntity = {};
  textoDelete: string;

  disabledAdd = false;

  dataSource = new MatTableDataSource<StatusWrapper<IGrupoPersonaAutorizada>>();
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
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.PERSONA_AUTORIZADA, actionService);
    this.formPart = this.fragment as GrupoPersonaAutorizadaFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor =
      (wrapper: StatusWrapper<IGrupoPersonaAutorizada>, property: string) => {
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
    this.subscriptions.push(this.formPart.personasAutorizadas$.subscribe(elements => {
      this.disabledAdd = elements.length === 1 && !!!elements[0].value.fechaInicio;
      this.dataSource.data = elements;
    }));
  }

  private setupI18N(): void {
    this.translate.get(
      GRUPO_PERSONA_AUTORIZADA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      MODAL_TITLE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.modalTitleEntity = value);

    this.translate.get(
      GRUPO_PERSONA_AUTORIZADA_KEY,
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
   * Apertura de modal de personas autorizadas (edición/creación)
   *
   * @param wrapper StatusWrapper<IGrupoPersonaAutorizada>
   * @param rowIndex índice de la fila del listado
   */
  openModal(wrapper?: StatusWrapper<IGrupoPersonaAutorizada>, rowIndex?: number): void {
    // Necesario para sincronizar los cambios de orden de registros dependiendo de la ordenación y paginación
    this.dataSource.sortData(this.dataSource.filteredData, this.dataSource.sort);
    const row = (this.paginator.pageSize * this.paginator.pageIndex) + rowIndex;

    const data: GrupoPersonaAutorizadaModalData = {
      titleEntity: this.modalTitleEntity,
      entidad: wrapper?.value ?? {} as IGrupoPersonaAutorizada,
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
    const dialogRef = this.matDialog.open(GrupoPersonaAutorizadaModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (modalData: GrupoPersonaAutorizadaModalData) => {
        if (modalData) {
          if (!wrapper) {
            modalData.entidad.grupo = this.actionService.grupo;
            this.formPart.addGrupoPersonaAutorizada(modalData.entidad as IGrupoPersonaAutorizada);
          } else if (!wrapper.created) {
            const entidad = new StatusWrapper<IGrupoPersonaAutorizada>(modalData.entidad as IGrupoPersonaAutorizada);
            this.formPart.updateGrupoPersonaAutorizada(entidad);
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
  deleteEquipo(wrapper: StatusWrapper<IGrupoPersonaAutorizada>) {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.textoDelete).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.deleteGrupoPersonaAutorizada(wrapper);
          }
        }
      )
    );
  }

}
