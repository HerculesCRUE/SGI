import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { SolicitudProyectoEntidadFinanciadoraAjenaService } from './solicitud-proyecto-entidad-financiadora-ajena.service';

describe('SolicitudProyectoEntidadFinanciadoraAjenaService', () => {
  let service: SolicitudProyectoEntidadFinanciadoraAjenaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(SolicitudProyectoEntidadFinanciadoraAjenaService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
