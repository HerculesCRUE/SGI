import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { ProyectoSocioPeriodoJustificacionDocumentoService } from './proyecto-socio-periodo-justificacion-documento.service';

describe('ProyectoSocioPeriodoJustificacionDocumentoService', () => {
  let service: ProyectoSocioPeriodoJustificacionDocumentoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(ProyectoSocioPeriodoJustificacionDocumentoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
