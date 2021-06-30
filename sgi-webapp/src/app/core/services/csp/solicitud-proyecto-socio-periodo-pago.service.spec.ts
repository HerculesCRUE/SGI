import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { SolicitudProyectoSocioPeriodoPagoService } from './solicitud-proyecto-socio-periodo-pago.service';

describe('SolicitudProyectoSocioPeriodoPagoService', () => {
  let service: SolicitudProyectoSocioPeriodoPagoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(SolicitudProyectoSocioPeriodoPagoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
