import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { SolicitudProteccionProcedimientoDocumentoService } from './solicitud-proteccion-procedimiento-documento.service';


describe('SolicitudProteccionProcedimientoDocumentoService', () => {
  let service: SolicitudProteccionProcedimientoDocumentoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(SolicitudProteccionProcedimientoDocumentoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
