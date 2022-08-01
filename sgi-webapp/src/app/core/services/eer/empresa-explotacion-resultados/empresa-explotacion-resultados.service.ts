import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IEmpresaAdministracionSociedad } from '@core/models/eer/empresa-administracion-sociedad';
import { IEmpresaComposicionSociedad } from '@core/models/eer/empresa-composicion-sociedad';
import { IEmpresaDocumento } from '@core/models/eer/empresa-documento';
import { IEmpresaEquipoEmprendedor } from '@core/models/eer/empresa-equipo-emprendedor';
import { IEmpresaExplotacionResultados } from '@core/models/eer/empresa-explotacion-resultados';
import { environment } from '@env';
import {
  CreateCtor, FindAllCtor, FindByIdCtor,
  mixinCreate, mixinFindAll, mixinFindById,
  mixinUpdate, SgiRestBaseService, SgiRestFindOptions,
  SgiRestListResult, UpdateCtor
} from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IEmpresaAdministracionSociedadResponse } from '../empresa-administracion-sociedad/empresa-administracion-sociedad-response';
import { EMPRESA_ADMINISTRACION_SOCIEDAD_RESPONSE_CONVERTER } from '../empresa-administracion-sociedad/empresa-administracion-sociedad-response.converter';
import { IEmpresaComposicionSociedadResponse } from '../empresa-composicion-sociedad/empresa-composicion-sociedad-response';
import { EMPRESA_COMPOSICION_SOCIEDAD_RESPONSE_CONVERTER } from '../empresa-composicion-sociedad/empresa-composicion-sociedad-response.converter';
import { IEmpresaDocumentoResponse } from '../empresa-documento/empresa-documento-response';
import { EMPRESA_DOCUMENTO_RESPONSE_CONVERTER } from '../empresa-documento/empresa-documento-response.converter';
import { IEmpresaEquipoEmprendedorResponse } from '../empresa-equipo-emprendedor/empresa-equipo-emprendedor-response';
import { EMPRESA_EQUIPO_EMPRENDEDOR_RESPONSE_CONVERTER } from '../empresa-equipo-emprendedor/empresa-equipo-emprendedor-response.converter';
import { IEmpresaExplotacionResultadosRequest } from './empresa-explotacion-resultados-request';
import { EMPRESA_EXPLOTACION_RESULTADOS_REQUEST_CONVERTER } from './empresa-explotacion-resultados-request.converter';
import { IEmpresaExplotacionResultadosResponse } from './empresa-explotacion-resultados-response';
import { EMPRESA_EXPLOTACION_RESULTADOS_RESPONSE_CONVERTER } from './empresa-explotacion-resultados-response.converter';


// tslint:disable-next-line: variable-name
const _EmpresaExplotacionResultadosMixinBase:
  CreateCtor<IEmpresaExplotacionResultados, IEmpresaExplotacionResultados, IEmpresaExplotacionResultadosRequest,
    IEmpresaExplotacionResultadosResponse> &
  UpdateCtor<number, IEmpresaExplotacionResultados, IEmpresaExplotacionResultados, IEmpresaExplotacionResultadosRequest,
    IEmpresaExplotacionResultadosResponse> &
  FindByIdCtor<number, IEmpresaExplotacionResultados, IEmpresaExplotacionResultadosResponse> &
  FindAllCtor<IEmpresaExplotacionResultados, IEmpresaExplotacionResultadosResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      mixinUpdate(
        mixinCreate(
          SgiRestBaseService,
          EMPRESA_EXPLOTACION_RESULTADOS_REQUEST_CONVERTER,
          EMPRESA_EXPLOTACION_RESULTADOS_RESPONSE_CONVERTER
        ),
        EMPRESA_EXPLOTACION_RESULTADOS_REQUEST_CONVERTER,
        EMPRESA_EXPLOTACION_RESULTADOS_RESPONSE_CONVERTER
      ),
      EMPRESA_EXPLOTACION_RESULTADOS_RESPONSE_CONVERTER
    ),
    EMPRESA_EXPLOTACION_RESULTADOS_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class EmpresaExplotacionResultadosService extends _EmpresaExplotacionResultadosMixinBase {
  private static readonly MAPPING = '/empresas';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.eer}${EmpresaExplotacionResultadosService.MAPPING}`,
      http,
    );
  }

  /**
   * Comprueba si existe el grupo
   *
   * @param id Identificador del grupo
   */
  exists(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  /**
   * Desactiva el grupo
   *
   * @param id Identificador del grupo
   */
  desactivar(id: number): Observable<void> {
    const url = `${this.endpointUrl}/${id}/desactivar`;
    return this.http.patch<void>(url, { id });
  }

  /**
   * Recupera la lista de miembros del equipo de la empresa de explotación de resultados
   *
   * @param id Identificador de la empresa de explotación de resultados
   * @param options opciones de búsqueda.
   */
  findMiembrosEquipoEmprendedor(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IEmpresaEquipoEmprendedor>> {
    return this.find<IEmpresaEquipoEmprendedorResponse, IEmpresaEquipoEmprendedor>(
      `${this.endpointUrl}/${id}/equipos-emprendedores`,
      options,
      EMPRESA_EQUIPO_EMPRENDEDOR_RESPONSE_CONVERTER
    );
  }

  /**
   * Obtiene todos los Documentos de una Empresa de explotacion de resultados
   * @param empresa Empresa de explotacion de resultados
   * @param options opciones de busqueda
   * @returns Documentos de una Empresa de explotacion de resultados
   */
  findDocumentos(
    empresa: IEmpresaExplotacionResultados, options?: SgiRestFindOptions
  ): Observable<SgiRestListResult<IEmpresaDocumento>> {
    return this.find<IEmpresaDocumentoResponse, IEmpresaDocumento>(
      `${this.endpointUrl}/${empresa?.id}/documentos`,
      options,
      EMPRESA_DOCUMENTO_RESPONSE_CONVERTER
    );
  }

  /**
   * Recupera la lista de composiciones de la sociedad de la empresa de explotación de resultados
   *
   * @param id Identificador de la empresa de explotación de resultados
   * @param options opciones de búsqueda.
   */
  findComposicionesSociedad(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IEmpresaComposicionSociedad>> {
    return this.find<IEmpresaComposicionSociedadResponse, IEmpresaComposicionSociedad>(
      `${this.endpointUrl}/${id}/composiciones-sociedades`,
      options,
      EMPRESA_COMPOSICION_SOCIEDAD_RESPONSE_CONVERTER
    );
  }

  /**
   * Recupera la lista de administraciones de la sociedad de la empresa de explotación de resultados
   *
   * @param id Identificador de la empresa de explotación de resultados
   * @param options opciones de búsqueda.
   */
  findAdministracionesSociedad(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IEmpresaAdministracionSociedad>> {
    return this.find<IEmpresaAdministracionSociedadResponse, IEmpresaAdministracionSociedad>(
      `${this.endpointUrl}/${id}/administraciones-sociedades`,
      options,
      EMPRESA_ADMINISTRACION_SOCIEDAD_RESPONSE_CONVERTER
    );
  }

}
