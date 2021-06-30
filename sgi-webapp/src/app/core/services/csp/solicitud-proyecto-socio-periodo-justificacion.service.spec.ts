import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { SolicitudProyectoSocioPeriodoJustificacionService } from './solicitud-proyecto-socio-periodo-justificacion.service';

describe('SolicitudProyectoSocioPeriodoJustificacionService', () => {
  let service: SolicitudProyectoSocioPeriodoJustificacionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        BrowserAnimationsModule,
        HttpClientTestingModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(SolicitudProyectoSocioPeriodoJustificacionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
