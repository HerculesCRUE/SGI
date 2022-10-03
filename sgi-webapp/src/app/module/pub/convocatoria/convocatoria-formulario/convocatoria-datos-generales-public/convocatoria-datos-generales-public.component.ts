import { Component, OnInit, ViewChild } from '@angular/core';
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
import { TranslateService } from '@ngx-translate/core';
import { Observable, Subscription } from 'rxjs';
import { map } from 'rxjs/operators';
import { ConvocatoriaPublicActionService } from '../../convocatoria-public.action.service';
import { ConvocatoriaDatosGeneralesPublicFragment } from './convocatoria-datos-generales-public.fragment';

const AREA_KEY = marker('csp.area');
const AREA_TEMATICA_KEY = marker('csp.area-tematica');

export interface AreaTematicaListadoPublic {
  padre: string;
  areasTematicas: string;
}

@Component({
  selector: 'sgi-convocatoria-datos-generales-public',
  templateUrl: './convocatoria-datos-generales-public.component.html',
  styleUrls: ['./convocatoria-datos-generales-public.component.scss']
})
export class ConvocatoriaDatosGeneralesPublicComponent extends FormFragmentComponent<IConvocatoria> implements OnInit {
  formPart: ConvocatoriaDatosGeneralesPublicFragment;

  regimenesConcurrencia$: Observable<ITipoRegimenConcurrencia[]>;

  private subscriptions = [] as Subscription[];

  msgParamAreaEntities = {};
  msgParamAreaTematicaEntities = {};

  convocatoriaAreaTematicas = new MatTableDataSource<AreaTematicaListadoPublic>();
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
    protected actionService: ConvocatoriaPublicActionService,
    private readonly translate: TranslateService,
    tipoRegimenConcurrenciaService: TipoRegimenConcurrenciaService
  ) {
    super(actionService.FRAGMENT.DATOS_GENERALES, actionService);
    this.formPart = this.fragment as ConvocatoriaDatosGeneralesPublicFragment;

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
        const listadoAreas: AreaTematicaListadoPublic = {
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
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamAreaTematicaEntities = { entity: value });
  }

}
