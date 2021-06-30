import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { SolicitudProyectoSocioEquipoService } from './solicitud-proyecto-socio-equipo.service';

describe('SolicitudProyectoSocioEquipoService', () => {
  let service: SolicitudProyectoSocioEquipoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(SolicitudProyectoSocioEquipoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
