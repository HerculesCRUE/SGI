import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { SolicitudProyectoPresupuestoService } from './solicitud-proyecto-presupuesto.service';

describe('SolicitudProyectoPresupuestoService', () => {
  let service: SolicitudProyectoPresupuestoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(SolicitudProyectoPresupuestoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
