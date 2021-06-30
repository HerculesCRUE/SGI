import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { ProyectoPeriodoSeguimientoDocumentoService } from './proyecto-periodo-seguimiento-documento.service';


describe('ProyectoPeriodoSeguimientoDocumento.Service', () => {
  let service: ProyectoPeriodoSeguimientoDocumentoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        HttpClientTestingModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(ProyectoPeriodoSeguimientoDocumentoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});