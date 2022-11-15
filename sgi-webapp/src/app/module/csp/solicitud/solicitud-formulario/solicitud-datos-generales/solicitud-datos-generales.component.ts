import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort, MatSortable } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { FormularioSolicitud, FORMULARIO_SOLICITUD_MAP } from '@core/enums/formulario-solicitud';
import { MSG_PARAMS } from '@core/i18n';
import { ESTADO_MAP } from '@core/models/csp/estado-solicitud';
import { ISolicitud, TipoSolicitudGrupo, TIPO_SOLICITUD_GRUPO_MAP } from '@core/models/csp/solicitud';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { TipoColectivo } from 'src/app/esb/sgp/shared/select-persona/select-persona.component';
import { SolicitudModalidadEntidadConvocanteModalComponent, SolicitudModalidadEntidadConvocanteModalData } from '../../modals/solicitud-modalidad-entidad-convocante-modal/solicitud-modalidad-entidad-convocante-modal.component';
import { SolicitudActionService } from '../../solicitud.action.service';
import { SolicitudDatosGeneralesFragment, SolicitudModalidadEntidadConvocanteListado } from './solicitud-datos-generales.fragment';

const SOLICITUD_CODIGO_EXTERNO_KEY = marker('csp.solicitud.codigo-externo');
const SOLICITUD_CONVOCATORIA_EXTERNA_KEY = marker('csp.solicitud.convocatoria-externa');
const SOLICITUD_OBSERVACIONES_KEY = marker('csp.solicitud.observaciones');
const SOLICITUD_UNIDAD_GESTION_KEY = marker('csp.solicitud.unidad-gestion');
const SOLICITUD_ENTIDAD_CONVOCANTE_KEY = marker('csp.solicitud-entidad-convocante');
const SOLICITUD_TITULO_KEY = marker('csp.solicitud.titulo');
const SOLICITUD_FORMULARIO_SOLICITUD_KEY = marker('csp.solicitud.tipo-formulario');
const SOLICITUD_CONVOCATORIA_KEY = marker('csp.solicitud.convocatoria');
const SOLICITUD_SOLICITANTE_KEY = marker('csp.solicitud.solicitante');
const SOLICITUD_TIPO_SOLICITUD_GRUPO_KEY = marker('csp.solicitud.tipo-solicitud-grupo');
const SOLICITUD_GRUPO_KEY = marker('csp.solicitud.grupo');

@Component({
  selector: 'sgi-solicitud-datos-generales',
  templateUrl: './solicitud-datos-generales.component.html',
  styleUrls: ['./solicitud-datos-generales.component.scss']
})
export class SolicitudDatosGeneralesComponent extends FormFragmentComponent<ISolicitud> implements OnInit, OnDestroy {

  formPart: SolicitudDatosGeneralesFragment;

  totalElementos: number;
  displayedColumns: string[];
  elementosPagina: number[];

  msgParamConvocatoriaExternaEntity = {};
  msgParamCodigoExternoEntity = {};
  msgParamEntidadConvocanteEntity = {};
  msgParamObservacionesEntity = {};
  msgParamUnidadGestionEntity = {};
  msgParamTituloEntity = {};
  msgParamFormularioSolicitudEntity = {};
  msgParamTipoSolicitudGrupoEntity = {};
  msgParamConvocatoriaEntity = {};
  msgParamSolicitanteEntity = {};
  msgParamGrupoEntity = {};

  dataSourceEntidadesConvocantes: MatTableDataSource<SolicitudModalidadEntidadConvocanteListado>;
  @ViewChild(MatPaginator, { static: false }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: false }) sort: MatSort;

  private subscriptions = [] as Subscription[];

  get tipoColectivoSolicitante() {
    return TipoColectivo.SOLICITANTE_CSP;
  }

  get FORMULARIO_SOLICITUD_MAP() {
    return FORMULARIO_SOLICITUD_MAP;
  }

  get FormularioSolicitud() {
    return FormularioSolicitud;
  }

  get TIPO_SOLICITUD_GRUPO_MAP() {
    return TIPO_SOLICITUD_GRUPO_MAP;
  }

  get TipoSolicitudGrupo() {
    return TipoSolicitudGrupo;
  }

  get ESTADO_MAP() {
    return ESTADO_MAP;
  }

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    public readonly actionService: SolicitudActionService,
    private matDialog: MatDialog,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.DATOS_GENERALES, actionService);
    this.formPart = this.fragment as SolicitudDatosGeneralesFragment;

    this.elementosPagina = [5, 10, 25, 100];
    this.displayedColumns = ['entidadConvocante', 'plan', 'programaConvocatoria', 'modalidadSolicitud', 'acciones'];
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();

    this.dataSourceEntidadesConvocantes = new MatTableDataSource<SolicitudModalidadEntidadConvocanteListado>();

    this.dataSourceEntidadesConvocantes.sortingDataAccessor =
      (entidadConvocanteModalidad: SolicitudModalidadEntidadConvocanteListado, property: string) => {
        switch (property) {
          case 'entidadConvocante':
            return entidadConvocanteModalidad.entidadConvocante.entidad.nombre;
          case 'plan':
            return entidadConvocanteModalidad.plan?.nombre;
          case 'programaConvocatoria':
            return entidadConvocanteModalidad.entidadConvocante.programa?.padre?.id ?
              entidadConvocanteModalidad.entidadConvocante.programa?.nombre : '';
          case 'modalidadSolicitud':
            return entidadConvocanteModalidad.modalidad?.value.programa?.nombre;
          default:
            return entidadConvocanteModalidad[property];
        }
      };

    this.subscriptions.push(this.formPart.entidadesConvocantesModalidad$.subscribe(elements => {
      this.dataSourceEntidadesConvocantes.data = elements;

      setTimeout(() => {
        this.dataSourceEntidadesConvocantes.paginator = this.paginator;
        this.sort?.sort(({ id: 'entidadConvocante', start: 'asc' }) as MatSortable);
        this.dataSourceEntidadesConvocantes.sort = this.sort;
      }, 0);
    }));
  }

  private setupI18N(): void {
    this.translate.get(
      SOLICITUD_CODIGO_EXTERNO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamCodigoExternoEntity =
      { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      SOLICITUD_CONVOCATORIA_EXTERNA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamConvocatoriaExternaEntity =
      { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      SOLICITUD_OBSERVACIONES_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamObservacionesEntity =
      { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.PLURAL });

    this.translate.get(
      SOLICITUD_UNIDAD_GESTION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamUnidadGestionEntity =
      { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      SOLICITUD_ENTIDAD_CONVOCANTE_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamEntidadConvocanteEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE });

    this.translate.get(
      SOLICITUD_TITULO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamTituloEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      SOLICITUD_FORMULARIO_SOLICITUD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFormularioSolicitudEntity =
      { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      SOLICITUD_TIPO_SOLICITUD_GRUPO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamTipoSolicitudGrupoEntity =
      { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      SOLICITUD_GRUPO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamGrupoEntity =
      { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      SOLICITUD_CONVOCATORIA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamConvocatoriaEntity =
      { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      SOLICITUD_SOLICITANTE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamSolicitanteEntity =
      { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  /**
   * Apertura de modal de modadidad solicitud
   *
   * @param entidadConvocanteModalidad EntidadConvocanteModalidad que se carga en el modal para modificarlo.
   */
  openModalSelectModalidad(entidadConvocanteModalidad: SolicitudModalidadEntidadConvocanteListado): void {
    const data: SolicitudModalidadEntidadConvocanteModalData = {
      entidad: entidadConvocanteModalidad.entidadConvocante.entidad,
      plan: entidadConvocanteModalidad.plan,
      programa: entidadConvocanteModalidad.entidadConvocante.programa,
      modalidad: entidadConvocanteModalidad.modalidad?.value,
      readonly: this.formPart.readonly
    };

    const config = {
      data
    };

    const dialogRef = this.matDialog.open(SolicitudModalidadEntidadConvocanteModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (entidadConvocanteModalidadModal: SolicitudModalidadEntidadConvocanteModalData) => {

        if (!entidadConvocanteModalidadModal) {
          return;
        }

        if (!entidadConvocanteModalidad.modalidad) {
          this.formPart.addSolicitudModalidad(entidadConvocanteModalidadModal.modalidad, entidadConvocanteModalidadModal.programa?.id);
        } else if (!entidadConvocanteModalidadModal.modalidad) {
          this.formPart.deleteSolicitudModalidad(entidadConvocanteModalidad.modalidad);
        } else if (!entidadConvocanteModalidad.modalidad.created) {
          this.formPart.updateSolicitudModalidad(entidadConvocanteModalidadModal.modalidad);
        }
      }
    );
  }
}
