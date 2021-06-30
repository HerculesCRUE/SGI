import { TestBed } from '@angular/core/testing';

import { ProyectoSocioPeriodoJustificacionService } from './proyecto-socio-periodo-justificacion.service';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('ProyectoSocioPeriodoJustificacionService', () => {
  let service: ProyectoSocioPeriodoJustificacionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(ProyectoSocioPeriodoJustificacionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
