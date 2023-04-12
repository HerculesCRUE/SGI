import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { DEDICACION_MAP, IGrupoEquipo } from '@core/models/csp/grupo-equipo';
import { GrupoEquipoService } from '@core/services/csp/grupo-equipo/grupo-equipo.service';
import { DialogService } from '@core/services/dialog.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { of, Subscription } from 'rxjs';
import { catchError, switchMap } from 'rxjs/operators';
import { getPersonaEmailListConcatenated } from 'src/app/esb/sgp/shared/pipes/persona-email.pipe';
import { GrupoActionService } from '../../grupo.action.service';
import { GrupoEquipoModalComponent, GrupoEquipoModalData } from '../../modals/grupo-equipo-modal/grupo-equipo-modal.component';
import { GrupoEquipoInvestigacionFragment, IGrupoEquipoListado } from './grupo-equipo-investigacion.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const GRUPO_EQUIPO_MIEMBRO_KEY = marker('csp.grupo-equipo.miembro');
const MODAL_TITLE_KEY = marker('csp.grupo-equipo.miembro-equipo');
const MSG_NO_DELETE = marker('csp.grupo-equipo.adscrito');

@Component({
  selector: 'sgi-grupo-equipo-investigacion',
  templateUrl: './grupo-equipo-investigacion.component.html',
  styleUrls: ['./grupo-equipo-investigacion.component.scss']
})
export class GrupoEquipoInvestigacionComponent extends FragmentComponent implements OnInit, OnDestroy {

  private subscriptions: Subscription[] = [];
  formPart: GrupoEquipoInvestigacionFragment;

  elementosPagina = [5, 10, 25, 100];
  displayedColumns = ['persona', 'nombre', 'apellidos', 'rolEquipo', 'categoria', 'fechaInicio', 'fechaFin', 'dedicacion', 'participacion', 'acciones'];

  modalTitleEntity: string;
  msgParamEntity = {};
  textoDelete: string;
  textoNoDelete: string;

  dataSource = new MatTableDataSource<StatusWrapper<IGrupoEquipoListado>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  get DEDICACION_MAP() {
    return DEDICACION_MAP;
  }

  constructor(
    protected grupoEquipoService: GrupoEquipoService,
    public actionService: GrupoActionService,
    private matDialog: MatDialog,
    private dialogService: DialogService,
    private readonly translate: TranslateService,
    private readonly logger: NGXLogger
  ) {
    super(actionService.FRAGMENT.EQUIPO_INVESTIGACION, actionService);
    this.formPart = this.fragment as GrupoEquipoInvestigacionFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor =
      (wrapper: StatusWrapper<IGrupoEquipoListado>, property: string) => {
        switch (property) {
          case 'persona':
            return getPersonaEmailListConcatenated(wrapper.value.persona);
          case 'nombre':
            return wrapper.value.persona.nombre;
          case 'apellidos':
            return wrapper.value.persona.apellidos;
          case 'rolEquipo':
            return wrapper.value.rol.nombre;
          case 'categoria':
            return wrapper.value.categoriaProfesional?.nombre;
          case 'fechaInicio':
            return wrapper.value.fechaInicio;
          case 'fechaFin':
            return wrapper.value.fechaFin;
          case 'dedicacion':
            return wrapper.value.dedicacion;
          case 'participacion':
            return wrapper.value.participacion;
          default:
            return wrapper[property];
        }
      };
    this.dataSource.sort = this.sort;
    this.subscriptions.push(this.formPart.equipos$.subscribe(elements => {
      this.dataSource.data = elements;
    }));
  }

  private setupI18N(): void {
    this.translate.get(
      GRUPO_EQUIPO_MIEMBRO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      MODAL_TITLE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.modalTitleEntity = value);

    this.translate.get(
      GRUPO_EQUIPO_MIEMBRO_KEY,
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
   * Apertura de modal de equipos de investigación (edición/creación)
   *
   * @param wrapper StatusWrapper<IGrupoEquipo>
   * @param rowIndex índice de la fila del listado
   */
  openModal(wrapper?: StatusWrapper<IGrupoEquipo>, rowIndex?: number): void {
    // Necesario para sincronizar los cambios de orden de registros dependiendo de la ordenación y paginación
    this.dataSource.sortData(this.dataSource.filteredData, this.dataSource.sort);
    const row = (this.paginator.pageSize * this.paginator.pageIndex) + rowIndex;
    const entidad = wrapper?.value ?? {} as IGrupoEquipo;
    entidad.grupo = this.actionService.grupo;
    const data: GrupoEquipoModalData = {
      titleEntity: this.modalTitleEntity,
      entidad,
      selectedEntidades: this.dataSource.data.map(element => element.value),
      fechaInicioMin: this.actionService.grupo.fechaInicio,
      fechaFinMax: this.actionService.grupo.fechaFin,
      dedicacionMinimaGrupo: this.getDedicacionMinima(),
      grupo: this.actionService.grupo
    };

    if (wrapper) {
      const filtered = Object.assign([], data.selectedEntidades);
      filtered.splice(row, 1);
      data.selectedEntidades = filtered;
    }

    const config: MatDialogConfig = {
      data
    };
    const dialogRef = this.matDialog.open(GrupoEquipoModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (modalData: GrupoEquipoModalData) => {
        if (modalData) {
          if (!wrapper) {
            modalData.entidad.grupo = this.actionService.grupo;
            this.formPart.addGrupoEquipoInvestigacion(modalData.entidad as IGrupoEquipoListado);
          } else if (!wrapper.created) {
            const entidad = new StatusWrapper<IGrupoEquipoListado>(modalData.entidad as IGrupoEquipoListado);
            this.formPart.updateGrupoEquipoInvestigacion(entidad);
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
  deleteEquipo(wrapper: StatusWrapper<IGrupoEquipoListado>) {
    if (!!!wrapper.value.id) {
      this.deleteRow(wrapper);
      return;
    }

    this.grupoEquipoService.existsLineaInvestigadorInFechasGrupoEquipo(wrapper.value.id)
      .pipe(
        catchError(er => {
          this.logger.error(er);
          return of(void (0));
        })
      )
      .subscribe(res => {
        if (res) {
          this.subscriptions.push(this.dialogService.showConfirmation(this.textoNoDelete).subscribe());
        } else {
          this.deleteRow(wrapper);
        }
      });
  }


  private deleteRow(wrapper: StatusWrapper<IGrupoEquipoListado>) {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.textoDelete).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.deleteGrupoEquipoInvestigacion(wrapper);
          }
        }
      )
    );
  }

  /**
   * Recupera la dedicacion minima de la configuracion y si no esta definada o esta en 0 la decicacion minima sera 1
   * @returns la dedicacion minima a un grupo
   */
  private getDedicacionMinima(): number {
    return !!this.formPart.configuracion.dedicacionMinimaGrupo ? this.formPart.configuracion.dedicacionMinimaGrupo : 1;
  }

}
