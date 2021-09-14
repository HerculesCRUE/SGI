import { Injectable } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { IInvencion } from '@core/models/pii/invencion';
import { ISolicitudProteccion } from '@core/models/pii/solicitud-proteccion';
import { ActionService } from '@core/services/action-service';
import { SolicitudProteccionService } from '@core/services/pii/invencion/solicitud-proteccion/solicitud-proteccion.service';
import { ViaProteccionService } from '@core/services/pii/via-proteccion/via-proteccion.service';
import { PaisService } from '@core/services/sgo/pais/pais.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { NGXLogger } from 'ngx-logger';
import { SOLICITUD_PROTECCION_DATA_KEY } from './solicitud-proteccion-data.resolver';
import { SolicitudProteccionDatosGeneralesFragment } from './solicitud-proteccion-formulario/solicitud-proteccion-datos-generales/solicitud-proteccion-datos-generales.fragment';
import { SOLICITUD_PROTECCION_ROUTE_PARAMS } from './solicitud-proteccion-route-params';

export interface ISolicitudProteccionData {
  invencion: IInvencion;
  solicitudesProteccion: ISolicitudProteccion[];
  readonly: boolean;
}

@Injectable()
export class SolicitudProteccionActionService extends ActionService {

  private datosGenerales: SolicitudProteccionDatosGeneralesFragment;

  private data: ISolicitudProteccionData;

  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datosGenerales'
  };

  constructor(
    private logger: NGXLogger,
    route: ActivatedRoute,
    private solicitudProteccionService: SolicitudProteccionService,
    personaService: PersonaService,
    private viaProteccionService: ViaProteccionService,
    private paisService: PaisService
  ) {
    super();

    this.data = route.snapshot.data[SOLICITUD_PROTECCION_DATA_KEY];
    const id = Number(route.snapshot.paramMap.get(SOLICITUD_PROTECCION_ROUTE_PARAMS.ID));
    if (id) {
      this.enableEdit();
    }

    const solicitudesPrevias = (this.data.solicitudesProteccion ?? []).some(elem => elem.activo === true);

    this.datosGenerales = new SolicitudProteccionDatosGeneralesFragment(
      logger,
      id,
      this.data.invencion.id,
      this.data.invencion.tipoProteccion.tipoPropiedad,
      this.data.invencion.titulo,
      solicitudesPrevias,
      this.solicitudProteccionService,
      this.data.readonly,
      this.viaProteccionService,
      this.paisService);

    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);

    // Inicializamos por defectos los datos generales
    this.datosGenerales.initialize();
  }
}
