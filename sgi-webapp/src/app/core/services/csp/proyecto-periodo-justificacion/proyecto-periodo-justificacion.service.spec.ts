import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { ProyectoPeriodoJustificacionService } from './proyecto-periodo-justificacion.service';

describe('ProyectoPeriodoJustificacionService', () => {
  let service: ProyectoPeriodoJustificacionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(ProyectoPeriodoJustificacionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
