import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { ProyectoPeriodoSeguimientoService } from './proyecto-periodo-seguimiento.service';

describe('ProyectoPeriodoSeguimientoService', () => {
  let service: ProyectoPeriodoSeguimientoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(ProyectoPeriodoSeguimientoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
