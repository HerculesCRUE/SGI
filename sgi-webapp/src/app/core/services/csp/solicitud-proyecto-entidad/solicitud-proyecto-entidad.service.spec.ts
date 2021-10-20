import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { SolicitudProyectoEntidadService } from './solicitud-proyecto-entidad.service';

describe('SolicitudProyectoEntidadService', () => {
  let service: SolicitudProyectoEntidadService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(SolicitudProyectoEntidadService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
