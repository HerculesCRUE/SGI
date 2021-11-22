import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { SolicitudProteccionProcedimientoService } from './solicitud-proteccion-procedimiento.service';


describe('SolicitudProteccionProcedimientoService', () => {
  let service: SolicitudProteccionProcedimientoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(SolicitudProteccionProcedimientoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
