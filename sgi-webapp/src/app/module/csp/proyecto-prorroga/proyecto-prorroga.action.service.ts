import { Injectable } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { IProyecto } from '@core/models/csp/proyecto';
import { IProyectoProrroga } from '@core/models/csp/proyecto-prorroga';
import { ActionService } from '@core/services/action-service';
import { ProyectoProrrogaDocumentoService } from '@core/services/csp/proyecto-prorroga-documento.service';
import { ProyectoProrrogaService } from '@core/services/csp/proyecto-prorroga.service';
import { DialogService } from '@core/services/dialog.service';
import { DocumentoService } from '@core/services/sgdoc/documento.service';
import { DateTime } from 'luxon';
import { NGXLogger } from 'ngx-logger';
import { PROYECTO_PRORROGA_DATA_KEY } from './proyecto-prorroga-data.resolver';
import { ProyectoProrrogaDatosGeneralesFragment } from './proyecto-prorroga-formulario/proyecto-prorroga-datos-generales/proyecto-prorroga-datos-generales.fragment';
import { ProyectoProrrogaDocumentosFragment } from './proyecto-prorroga-formulario/proyecto-prorroga-documentos/proyecto-prorroga-documentos.fragment';
import { PROYECTO_PRORROGA_ROUTE_PARAMS } from './proyecto-prorroga-route-params';

export interface IProyectoProrrogaData {
  proyecto: IProyecto;
  proyectoProrrogas: IProyectoProrroga[];
  readonly: boolean;
}

@Injectable()
export class ProyectoProrrogaActionService extends ActionService {

  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datosGenerales',
    DOCUMENTOS: 'documentos'
  };

  private datosGenerales: ProyectoProrrogaDatosGeneralesFragment;
  private documentos: ProyectoProrrogaDocumentosFragment;
  private readonly data: IProyectoProrrogaData;

  constructor(
    logger: NGXLogger,
    route: ActivatedRoute,
    proyectoProrrogaService: ProyectoProrrogaService,
    periodoSeguimientoDocumentoService: ProyectoProrrogaDocumentoService,
    documentoService: DocumentoService,
    dialogService: DialogService
  ) {
    super();

    this.data = route.snapshot.data[PROYECTO_PRORROGA_DATA_KEY];
    const id = Number(route.snapshot.paramMap.get(PROYECTO_PRORROGA_ROUTE_PARAMS.ID));

    if (id) {
      this.enableEdit();
    }

    this.datosGenerales = new ProyectoProrrogaDatosGeneralesFragment(id,
      proyectoProrrogaService, this.data.proyecto.id, this.getLastFechaConcesion(id), this.data.proyecto, this.data.readonly, dialogService);
    this.documentos = new ProyectoProrrogaDocumentosFragment(logger, id, proyectoProrrogaService,
      periodoSeguimientoDocumentoService, documentoService, this.data.proyecto.modeloEjecucion.id, this.data.readonly);

    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);
    this.addFragment(this.FRAGMENT.DOCUMENTOS, this.documentos);
  }

  private getLastFechaConcesion(id: number): DateTime {
    if (this.data.proyectoProrrogas.length) {
      if (this.data.proyectoProrrogas[this.data.proyectoProrrogas.length - 1].id === id) {
        return this.data.proyectoProrrogas[this.data.proyectoProrrogas.length - 2]?.fechaConcesion;
      }
      else {
        return this.data.proyectoProrrogas[this.data.proyectoProrrogas.length - 1].fechaConcesion;
      }
    }
    return null;
  }
}
