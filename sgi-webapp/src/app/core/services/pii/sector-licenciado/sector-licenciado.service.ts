import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ISectorLicenciado } from '@core/models/pii/sector-licenciado';
import { environment } from '@env';
import {
  CreateCtor, FindAllCtor, FindByIdCtor, mixinCreate,
  mixinFindAll, mixinFindById, mixinUpdate, RSQLSgiRestFilter,
  SgiRestBaseService, SgiRestFilterOperator, SgiRestFindOptions, UpdateCtor
} from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ISectorLicenciadoRequest } from './sector-licenciado-request';
import { SECTOR_LICENCIADO_REQUEST_CONVERTER } from './sector-licenciado-request.converter';
import { ISectorLicenciadoResponse } from './sector-licenciado-response';
import { SECTOR_LICENCIADO_RESPONSE_CONVERTER } from './sector-licenciado-response.converter';

// tslint:disable-next-line: variable-name
const _SectorLicenciadoServiceMixinBase:
  FindAllCtor<ISectorLicenciado, ISectorLicenciadoResponse> &
  FindByIdCtor<number, ISectorLicenciado, ISectorLicenciadoResponse> &
  CreateCtor<ISectorLicenciado, ISectorLicenciado, ISectorLicenciadoRequest, ISectorLicenciadoResponse> &
  UpdateCtor<number, ISectorLicenciado, ISectorLicenciado, ISectorLicenciadoRequest, ISectorLicenciadoResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      mixinCreate(
        mixinUpdate(
          SgiRestBaseService,
          SECTOR_LICENCIADO_REQUEST_CONVERTER,
          SECTOR_LICENCIADO_RESPONSE_CONVERTER
        ),
        SECTOR_LICENCIADO_REQUEST_CONVERTER,
        SECTOR_LICENCIADO_RESPONSE_CONVERTER
      ),
      SECTOR_LICENCIADO_RESPONSE_CONVERTER
    ),
    SECTOR_LICENCIADO_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class SectorLicenciadoService extends _SectorLicenciadoServiceMixinBase {

  private static readonly MAPPING = '/sectoreslicenciados';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.pii}${SectorLicenciadoService.MAPPING}`,
      http,
    );
  }

  /**
   * Busca todos los SectoresLicenciados asociados al Contrato con referencia pasada.
   * 
   * @param contratoRef referencia del contrato.
   * @returns SectoresLicenciados asociados al contrato
   */
  findByContratoRef(contratoRef: string): Observable<ISectorLicenciado[]> {
    const options: SgiRestFindOptions = {
      filter: new RSQLSgiRestFilter('contratoRef', SgiRestFilterOperator.EQUALS, contratoRef)
    };
    return this.find(this.endpointUrl, options, SECTOR_LICENCIADO_RESPONSE_CONVERTER).pipe(map(response => response.items));
  }

  /**
   * Busca todos los SectoresLicenciados asociados a la Invencion con id pasado.
   * 
   * @param invencionId id de la Invencion.
   * @returns SectoresLicenciados asociados a la Invencion.
   */
  findByInvencionId(invencionId: string): Observable<ISectorLicenciado[]> {
    const options: SgiRestFindOptions = {
      filter: new RSQLSgiRestFilter('invencionId', SgiRestFilterOperator.EQUALS, invencionId)
    };
    return this.find(this.endpointUrl, options, SECTOR_LICENCIADO_RESPONSE_CONVERTER).pipe(map(response => response.items));
  }

  /**
   * Elimina el SectorLicenciado con el id indicado
   * 
   * @param id SectorLicenciado.
   */
  deleteById(sectorLicenciado: ISectorLicenciado): Observable<void> {
    return this.http.delete<void>(`${this.endpointUrl}/${sectorLicenciado.id}`);
  }
}
