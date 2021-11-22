import { Injectable } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { IInvencion } from '@core/models/pii/invencion';
import { ISolicitudProteccion } from '@core/models/pii/solicitud-proteccion';
import { ActionService } from '@core/services/action-service';
import { PaisValidadoService } from '@core/services/pii/solicitud-proteccion/pais-validado/pais-validado.service';
import { SolicitudProteccionProcedimientoDocumentoService } from '@core/services/pii/solicitud-proteccion/solicitud-proteccion-procedimiento-documento/solicitud-proteccion-procedimiento-documento.service';
import { SolicitudProteccionProcedimientoService } from '@core/services/pii/solicitud-proteccion/solicitud-proteccion-procedimiento/solicitud-proteccion-procedimiento.service';
import { SolicitudProteccionService } from '@core/services/pii/solicitud-proteccion/solicitud-proteccion.service';
import { TipoCaducidadService } from '@core/services/pii/tipo-caducidad/tipo-caducidad.service';
import { TipoProcedimientoService } from '@core/services/pii/tipo-procedimiento/tipo-procedimiento.service';
import { ViaProteccionService } from '@core/services/pii/via-proteccion/via-proteccion.service';
import { DocumentoService } from '@core/services/sgdoc/documento.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { PaisService } from '@core/services/sgo/pais/pais.service';
import { NGXLogger } from 'ngx-logger';
import { SOLICITUD_PROTECCION_DATA_KEY } from './solicitud-proteccion-data.resolver';
import { SolicitudProteccionDatosGeneralesFragment } from './solicitud-proteccion-formulario/solicitud-proteccion-datos-generales/solicitud-proteccion-datos-generales.fragment';
import { SolicitudProteccionProcedimientosFragment } from './solicitud-proteccion-formulario/solicitud-proteccion-procedimientos/solicitud-proteccion-procedimientos.fragment';
import { SOLICITUD_PROTECCION_ROUTE_PARAMS } from './solicitud-proteccion-route-params';

export interface ISolicitudProteccionData {
  invencion: IInvencion;
  solicitudesProteccion: ISolicitudProteccion[];
  readonly: boolean;
}

@Injectable()
export class SolicitudProteccionActionService extends ActionService {

  private datosGenerales: SolicitudProteccionDatosGeneralesFragment;
  private procedimientos: SolicitudProteccionProcedimientosFragment;

  private data: ISolicitudProteccionData;

  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datosGenerales',
    PROCEDIMIENTOS: 'procedimientos',
  };

  constructor(
    private logger: NGXLogger,
    private route: ActivatedRoute,
    private solicitudProteccionService: SolicitudProteccionService,
    private viaProteccionService: ViaProteccionService,
    private paisService: PaisService,
    private empresaService: EmpresaService,
    private tipoCaducidadService: TipoCaducidadService,
    private paisValidadoService: PaisValidadoService,
    private solicitudProteccionProcedimientoService: SolicitudProteccionProcedimientoService,
    private procedimientoDocumentoService: SolicitudProteccionProcedimientoDocumentoService,
    private tipoProcedimientoService: TipoProcedimientoService,
    private documentoService: DocumentoService
  ) {
    super();

    this.data = route.snapshot.data[SOLICITUD_PROTECCION_DATA_KEY];
    const id = Number(route.snapshot.paramMap.get(SOLICITUD_PROTECCION_ROUTE_PARAMS.ID));
    if (id) {
      this.enableEdit();
    }

    this.datosGenerales = new SolicitudProteccionDatosGeneralesFragment(
      logger,
      id,
      this.data.invencion.id,
      this.data.invencion.tipoProteccion.tipoPropiedad,
      this.data.invencion.titulo,
      this.solicitudProteccionService,
      this.data.readonly,
      this.viaProteccionService,
      this.paisService,
      this.empresaService,
      this.tipoCaducidadService,
      this.data.solicitudesProteccion,
      this.paisValidadoService
    );

    this.procedimientos = new SolicitudProteccionProcedimientosFragment(
      id, this.solicitudProteccionService, this.solicitudProteccionProcedimientoService,
      this.procedimientoDocumentoService, this.tipoProcedimientoService, this.documentoService
    );

    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);
    this.addFragment(this.FRAGMENT.PROCEDIMIENTOS, this.procedimientos);

    // Inicializamos por defectos los datos generales
    this.datosGenerales.initialize();
  }
}
