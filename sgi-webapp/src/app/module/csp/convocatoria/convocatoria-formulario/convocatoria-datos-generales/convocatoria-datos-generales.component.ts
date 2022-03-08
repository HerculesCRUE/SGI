import { Component, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { CLASIFICACION_CVN_MAP } from '@core/enums/clasificacion-cvn';
import { FORMULARIO_SOLICITUD_MAP } from '@core/enums/formulario-solicitud';
import { MSG_PARAMS } from '@core/i18n';
import { ESTADO_MAP, IConvocatoria } from '@core/models/csp/convocatoria';
import { ITipoRegimenConcurrencia } from '@core/models/csp/tipo-regimen-concurrencia';
import { TipoRegimenConcurrenciaService } from '@core/services/csp/tipo-regimen-concurrencia.service';
import { DialogService } from '@core/services/dialog.service';
import { TranslateService } from '@ngx-translate/core';
import { Observable, Subscription } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { ConvocatoriaActionService } from '../../convocatoria.action.service';
import { AreaTematicaModalData, ConvocatoriaAreaTematicaModalComponent } from '../../modals/convocatoria-area-tematica-modal/convocatoria-area-tematica-modal.component';
import { ConvocatoriaDatosGeneralesFragment } from './convocatoria-datos-generales.fragment';

const MSG_DELETE_AREA_TEMATICA = marker('msg.delete.entity');
const AREA_KEY = marker('csp.area');
const AREA_TEMATICA_KEY = marker('csp.area-tematica');
const CONVOCATORIA_FECHA_PROVISIONAL_KEY = marker('csp.convocatoria.fecha-provisional');
const CONVOCATORIA_FECHA_CONCESION_KEY = marker('csp.convocatoria.fecha-concesion');
const CONVOCATORIA_AMBITO_GEOGRAFICO_KEY = marker('csp.convocatoria.ambito-geografico');
const CONVOCATORIA_CODIGO_REFERENCIA_KEY = marker('csp.convocatoria.codigo-referencia');
const CONVOCATORIA_DESCRIPCION_KEY = marker('csp.convocatoria.descripcion');
const CONVOCATORIA_DURACION_KEY = marker('csp.convocatoria.duracion');
const CONVOCATORIA_FINALIDAD_KEY = marker('csp.convocatoria.finalidad');
const CONVOCATORIA_MODELO_EJECUCION_KEY = marker('csp.convocatoria.modelo-ejecucion');
const CONVOCATORIA_OBSERVACIONES_KEY = marker('csp.convocatoria.observaciones');
const CONVOCATORIA_TITULO_KEY = marker('csp.convocatoria.titulo');
const CONVOCATORIA_UNIDAD_GESTION_KEY = marker('csp.convocatoria.unidad-gestion');
const CONVOCATORIA_FORMULARIO_SOLICITUD_KEY = marker('csp.convocatoria.tipo-formulario');
const CONVOCATORIA_VINCULADA_TOOLTIP_KEY = marker('csp.convocatoria.campo.vinculada');

export interface AreaTematicaListado {
  padre: string;
  areasTematicas: string;
}

@Component({
  selector: 'sgi-convocatoria-datos-generales',
  templateUrl: './convocatoria-datos-generales.component.html',
  styleUrls: ['./convocatoria-datos-generales.component.scss']
})
export class ConvocatoriaDatosGeneralesComponent extends FormFragmentComponent<IConvocatoria> implements OnInit {
  formPart: ConvocatoriaDatosGeneralesFragment;

  regimenesConcurrencia$: Observable<ITipoRegimenConcurrencia[]>;

  private subscriptions = [] as Subscription[];

  msgParamAmbitoGeograficoEntity = {};
  msgParamAreaEntities = {};
  msgParamAreaTematicaEntity = {};
  msgParamAreaTematicaEntities = {};
  msgParamCodigoReferenciaEntity = {};
  msgParamDescripcionEntity = {};
  msgParamDuracionEntity = {};
  msgParamFinalidadEntity = {};
  msgParamModeloEjecucionEntity = {};
  msgParamObservacionesEntity = {};
  msgParamFechaConcesionEntity = {};
  msgParamFechaProvisionalEntity = {};
  msgParamTituloEntity = {};
  msgParamUnidadGestionEntity = {};
  msgParamFormularioSolicitud = {};
  msgTooltip = {};
  textoDeleteAreaTematica: string;

  convocatoriaAreaTematicas = new MatTableDataSource<AreaTematicaListado>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;
  columns = ['padre', 'nombre', 'acciones'];

  get CLASIFICACION_CVN_MAP() {
    return CLASIFICACION_CVN_MAP;
  }

  get FORMULARIO_SOLICITUD_MAP() {
    return FORMULARIO_SOLICITUD_MAP;
  }

  get ESTADO_MAP() {
    return ESTADO_MAP;
  }

  constructor(
    protected actionService: ConvocatoriaActionService,
    private matDialog: MatDialog,
    private dialogService: DialogService,
    private readonly translate: TranslateService,
    tipoRegimenConcurrenciaService: TipoRegimenConcurrenciaService
  ) {
    super(actionService.FRAGMENT.DATOS_GENERALES, actionService);
    this.formPart = this.fragment as ConvocatoriaDatosGeneralesFragment;

    this.regimenesConcurrencia$ = tipoRegimenConcurrenciaService.findAll().pipe(
      map(response => response.items)
    );
  }

  ngOnInit() {
    super.ngOnInit();
    this.setupI18N();
    this.convocatoriaAreaTematicas.paginator = this.paginator;
    this.convocatoriaAreaTematicas.sort = this.sort;
    this.subscriptions.push(this.formPart.areasTematicas$.subscribe((data) => {
      if (!data || data.length === 0) {
        this.convocatoriaAreaTematicas.data = [];
      } else {
        const listadoAreas: AreaTematicaListado = {
          padre: data[0]?.padre.nombre,
          areasTematicas: data.map(area => area.convocatoriaAreaTematica.value.areaTematica.nombre).join(', ')
        };
        this.convocatoriaAreaTematicas.data = [listadoAreas];
      }
    }
    ));
  }

  private setupI18N(): void {
    this.translate.get(
      AREA_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamAreaEntities = { entity: value });

    this.translate.get(
      AREA_TEMATICA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamAreaTematicaEntity = { entity: value });

    this.translate.get(
      AREA_TEMATICA_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamAreaTematicaEntities = { entity: value });

    this.translate.get(
      CONVOCATORIA_FORMULARIO_SOLICITUD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe(
      (value) => this.msgParamFormularioSolicitud = {
        entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR
      }
    );
    this.translate.get(
      CONVOCATORIA_CODIGO_REFERENCIA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe(
      (value) => this.msgParamCodigoReferenciaEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR }
    );

    this.translate.get(
      CONVOCATORIA_UNIDAD_GESTION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamUnidadGestionEntity =
      { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      CONVOCATORIA_FECHA_PROVISIONAL_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFechaProvisionalEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE });

    this.translate.get(
      CONVOCATORIA_FECHA_CONCESION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFechaConcesionEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE });

    this.translate.get(
      CONVOCATORIA_TITULO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamTituloEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      CONVOCATORIA_MODELO_EJECUCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamModeloEjecucionEntity =
      { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      CONVOCATORIA_MODELO_EJECUCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamModeloEjecucionEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    this.translate.get(
      CONVOCATORIA_FINALIDAD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFinalidadEntity =
      { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      CONVOCATORIA_DURACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamDuracionEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE });

    this.translate.get(
      CONVOCATORIA_AMBITO_GEOGRAFICO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamAmbitoGeograficoEntity =
      { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      CONVOCATORIA_DESCRIPCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamDescripcionEntity = {
      entity: value,
      ...MSG_PARAMS.GENDER.MALE,
      ...MSG_PARAMS.CARDINALIRY.SINGULAR
    });

    this.translate.get(
      CONVOCATORIA_OBSERVACIONES_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamObservacionesEntity = {
      entity: value,
      ...MSG_PARAMS.GENDER.FEMALE,
      ...MSG_PARAMS.CARDINALIRY.PLURAL
    });

    this.translate.get(
      CONVOCATORIA_VINCULADA_TOOLTIP_KEY
    ).subscribe((value) => this.msgTooltip = { entity: value });

    this.translate.get(
      AREA_TEMATICA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_DELETE_AREA_TEMATICA,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoDeleteAreaTematica = value);
  }

  openModal(): void {
    const selected = this.formPart.areasTematicas$.value.map(element => element.convocatoriaAreaTematica.value.areaTematica);
    const newData: AreaTematicaModalData = {
      padre: undefined,
      areasTematicas: selected,
    };
    const config = {
      panelClass: 'sgi-dialog-container',
      data: newData
    };
    const dialogRef = this.matDialog.open(ConvocatoriaAreaTematicaModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (result) => {
        if (result) {
          this.formPart.updateListaAreasTematicas(result.areasTematicas, result.padre);
        }
      }
    );
  }

  deleteAreasTematicas(): void {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.textoDeleteAreaTematica).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.updateListaAreasTematicas([], this.formPart.areasTematicas$.value[0].padre);
          }
        }
      )
    );
  }
}
