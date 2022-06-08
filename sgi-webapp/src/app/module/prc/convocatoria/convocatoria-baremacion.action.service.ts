import { Injectable } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { IConvocatoriaBaremacion } from '@core/models/prc/convocatoria-baremacion';
import { ActionService } from '@core/services/action-service';
import { ConfiguracionBaremoService } from '@core/services/prc/configuracion-baremo/configuracion-baremo.service';
import { ConvocatoriaBaremacionService } from '@core/services/prc/convocatoria-baremacion/convocatoria-baremacion.service';
import { ModuladorService } from '@core/services/prc/modulador/modulador.service';
import { AreaConocimientoService } from '@core/services/sgo/area-conocimiento.service';
import { NGXLogger } from 'ngx-logger';
import { ConvocatoriaBaremacionBaremosPuntuacionesFragment } from './convocatoria-baremacion-formulario/convocatoria-baremacion-baremos-puntuaciones/convocatoria-baremacion-baremos-puntuaciones.fragment';
import { ConvocatoriaBaremacionDatosGeneralesFragment } from './convocatoria-baremacion-formulario/convocatoria-baremacion-datos-generales/convocatoria-baremacion-datos-generales.fragment';
import { ModuladoresRangosFragment } from './convocatoria-baremacion-formulario/moduladores-rangos/moduladores-rangos.fragment';
import { CONVOCATORIA_BAREMACION_ROUTE_PARAMS } from './convocatoria-baremacion-params';
import { CONVOCATORIA_BAREMACION_DATA_KEY } from './convocatoria-baremacion.resolver';

export interface IConvocatoriaBaremacionData {
  canEdit: boolean;
  convocatoriaBaremacion: IConvocatoriaBaremacion;
}

@Injectable()
export class ConvocatoriaBaremacionActionService extends ActionService {

  public readonly id: number;
  private data: IConvocatoriaBaremacionData;
  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datos-generales',
    BAREMOS_PUNTUACIONES: 'baremos-puntuaciones',
    MODULADORES_RANGOS: 'moduladores-rangos',
  };

  private datosGenerales: ConvocatoriaBaremacionDatosGeneralesFragment;
  private baremosPuntuaciones: ConvocatoriaBaremacionBaremosPuntuacionesFragment
  private moduladoresRangos: ModuladoresRangosFragment;

  get canEdit(): boolean {
    return this.data?.canEdit ?? true;
  }

  constructor(
    logger: NGXLogger,
    route: ActivatedRoute,
    convocatoriaBaremacionService: ConvocatoriaBaremacionService,
    configuracionBaremoService: ConfiguracionBaremoService,
    areaConocimientoService: AreaConocimientoService,
    moduladorService: ModuladorService
  ) {
    super();

    this.id = Number(route.snapshot.paramMap.get(CONVOCATORIA_BAREMACION_ROUTE_PARAMS.ID));
    if (this.id) {
      this.data = route.snapshot.data[CONVOCATORIA_BAREMACION_DATA_KEY];
      this.enableEdit();
    }

    this.datosGenerales = new ConvocatoriaBaremacionDatosGeneralesFragment(
      this.id, this.data?.convocatoriaBaremacion, this.data?.canEdit, convocatoriaBaremacionService
    );
    this.moduladoresRangos = new ModuladoresRangosFragment(
      this.id, this.data?.convocatoriaBaremacion, this.data?.canEdit, convocatoriaBaremacionService,
      areaConocimientoService, moduladorService
    );

    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);

    if (this.isEdit()) {
      this.baremosPuntuaciones = new ConvocatoriaBaremacionBaremosPuntuacionesFragment(
        logger,
        this.id,
        this.data?.convocatoriaBaremacion,
        this.data?.canEdit,
        configuracionBaremoService,
        convocatoriaBaremacionService
      );

      this.addFragment(this.FRAGMENT.BAREMOS_PUNTUACIONES, this.baremosPuntuaciones);
      this.addFragment(this.FRAGMENT.MODULADORES_RANGOS, this.moduladoresRangos);

      // Sincronización de los baremos
      this.subscriptions.push(this.moduladoresRangos.initialized$.subscribe(value => {
        if (value) {
          this.baremosPuntuaciones.initialize();
        }
      }));
      this.subscriptions.push(this.baremosPuntuaciones.hasCostesIndirectosTipoRango$.subscribe(value => {
        this.moduladoresRangos.hasCostesIndirectosTipoRango$.next(value);
      }));

    }

  }
}
