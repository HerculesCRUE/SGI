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
import { ClasificacionPublicModalComponent, TipoClasificacionPublic } from 'src/app/esb/sgo/shared/clasificacion-public-modal/clasificacion-public-modal.component';
import { TipoColectivo } from 'src/app/esb/sgp/shared/select-persona/select-persona.component';
import { SolicitudModalidadEntidadConvocantePublicModalComponent, SolicitudModalidadEntidadConvocantePublicModalData } from '../../modals/solicitud-modalidad-entidad-convocante-public-modal/solicitud-modalidad-entidad-convocante-public-modal.component';
import { SolicitudPublicActionService } from '../../solicitud-public.action.service';
import { SolicitudDatosGeneralesPublicFragment, SolicitudModalidadEntidadConvocantePublicListado, SolicitudRrhhAreaAnepPublicListado } from './solicitud-datos-generales-public.fragment';

const SOLICITUD_CODIGO_EXTERNO_KEY = marker('csp.solicitud.codigo-externo');
const SOLICITUD_OBSERVACIONES_KEY = marker('csp.solicitud.observaciones');
const SOLICITUD_ENTIDAD_CONVOCANTE_KEY = marker('csp.solicitud-entidad-convocante');
const SOLICITUD_RRHH_NOMBRE_KEY = marker('csp.solicitud.solicitud-rrhh.nombre');
const SOLICITUD_RRHH_APELLIDOS_KEY = marker('csp.solicitud.solicitud-rrhh.apellidos');
const SOLICITUD_RRHH_NUMERO_DOCUMENTO_KEY = marker('csp.solicitud.solicitud-rrhh.numero-documento');
const SOLICITUD_RRHH_TIPO_DOCUMENTO_KEY = marker('csp.solicitud.solicitud-rrhh.tipo-documento');
const SOLICITUD_RRHH_TELEFONO_KEY = marker('csp.solicitud.solicitud-rrhh.telefono');
const SOLICITUD_RRHH_EMAIL_KEY = marker('csp.solicitud.solicitud-rrhh.email');
const SOLICITUD_RRHH_AREA_ANEP_KEY = marker('csp.solicitud.solicitud-rrhh.area-anep');

@Component({
  selector: 'sgi-solicitud-datos-generales-public',
  templateUrl: './solicitud-datos-generales-public.component.html',
  styleUrls: ['./solicitud-datos-generales-public.component.scss']
})
export class SolicitudDatosGeneralesPublicComponent extends FormFragmentComponent<ISolicitud> implements OnInit, OnDestroy {
  formPart: SolicitudDatosGeneralesPublicFragment;

  totalElementos: number;
  displayedColumns: string[];
  elementosPagina: number[];


  msgParamCodigoExternoEntity = {};
  msgParamObservacionesEntity = {};
  msgParamEntidadConvocanteEntity = {};
  msgParamNombreEntity = {};
  msgParamApellidosEntity = {};
  msgParamNumeroDocumentoEntity = {};
  msgParamTipoDocumentoEntity = {};
  msgParamTelefonoEntity = {};
  msgParamEmailEntity = {};
  msgParamAreaAnepEntity = {};

  dataSourceEntidadesConvocantes: MatTableDataSource<SolicitudModalidadEntidadConvocantePublicListado>;
  @ViewChild(MatPaginator, { static: false }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: false }) sort: MatSort;

  areasAnep = new MatTableDataSource<SolicitudRrhhAreaAnepPublicListado>();
  columnsAreasAnep = ['niveles', 'areaAnep', 'acciones'];

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
    public readonly actionService: SolicitudPublicActionService,
    private matDialog: MatDialog,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.DATOS_GENERALES, actionService);
    this.formPart = this.fragment as SolicitudDatosGeneralesPublicFragment;

    this.elementosPagina = [5, 10, 25, 100];
    this.displayedColumns = ['entidadConvocante', 'plan', 'programaConvocatoria', 'modalidadSolicitud', 'acciones'];
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.loadAreasAnep();
    this.dataSourceEntidadesConvocantes = new MatTableDataSource<SolicitudModalidadEntidadConvocantePublicListado>();

    this.dataSourceEntidadesConvocantes.sortingDataAccessor =
      (entidadConvocanteModalidad: SolicitudModalidadEntidadConvocantePublicListado, property: string) => {
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
      SOLICITUD_OBSERVACIONES_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamObservacionesEntity =
      { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.PLURAL });

    this.translate.get(
      SOLICITUD_ENTIDAD_CONVOCANTE_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamEntidadConvocanteEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE });

    this.translate.get(
      SOLICITUD_RRHH_NOMBRE_KEY
    ).subscribe((value) =>
      this.msgParamNombreEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR }
    );

    this.translate.get(
      SOLICITUD_RRHH_APELLIDOS_KEY
    ).subscribe((value) =>
      this.msgParamApellidosEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.PLURAL }
    );

    this.translate.get(
      SOLICITUD_RRHH_NUMERO_DOCUMENTO_KEY
    ).subscribe((value) =>
      this.msgParamNumeroDocumentoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR }
    );

    this.translate.get(
      SOLICITUD_RRHH_TIPO_DOCUMENTO_KEY
    ).subscribe((value) =>
      this.msgParamTipoDocumentoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR }
    );

    this.translate.get(
      SOLICITUD_RRHH_TELEFONO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamTelefonoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR }
    );

    this.translate.get(
      SOLICITUD_RRHH_EMAIL_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamEmailEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR }
    );

    this.translate.get(
      SOLICITUD_RRHH_AREA_ANEP_KEY
    ).subscribe((value) =>
      this.msgParamAreaAnepEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR }
    );

  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  /**
   * Apertura de modal de modadidad solicitud
   *
   * @param entidadConvocanteModalidad EntidadConvocanteModalidad que se carga en el modal para modificarlo.
   */
  openModalSelectModalidad(entidadConvocanteModalidad: SolicitudModalidadEntidadConvocantePublicListado): void {
    const data: SolicitudModalidadEntidadConvocantePublicModalData = {
      entidad: entidadConvocanteModalidad.entidadConvocante.entidad,
      plan: entidadConvocanteModalidad.plan,
      programa: entidadConvocanteModalidad.entidadConvocante.programa,
      modalidad: entidadConvocanteModalidad.modalidad?.value,
      readonly: this.formPart.readonly
    };

    const config = {
      data
    };

    const dialogRef = this.matDialog.open(SolicitudModalidadEntidadConvocantePublicModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (entidadConvocanteModalidadModal: SolicitudModalidadEntidadConvocantePublicModalData) => {

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

  deleteAreaAnep() {
    this.formPart.deleteAreaAnep();
  }

  openModal(): void {
    const config = {
      data: {
        selectedClasificaciones: [],
        tipoClasificacion: TipoClasificacionPublic.AREAS_ANEP,
        multiSelect: false
      }
    };
    const dialogRef = this.matDialog.open(ClasificacionPublicModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (clasificaciones) => {
        if (!!clasificaciones && clasificaciones.length === 1) {
          this.formPart.updateAreaAnep(clasificaciones[0]);
        }
      }
    );
  }

  private loadAreasAnep() {
    this.subscriptions.push(this.formPart.areasAnep$.subscribe((data) => {
      if (!data || data.length === 0) {
        this.areasAnep.data = [];
      } else {
        this.areasAnep.data = data;
      }
    }
    ));
  }
}
