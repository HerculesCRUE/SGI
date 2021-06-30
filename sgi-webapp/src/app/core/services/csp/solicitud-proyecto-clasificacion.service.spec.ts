import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { SolicitudProyectoClasificacionService } from './solicitud-proyecto-clasificacion.service';

describe('SolicitudProyectoClasificacionService', () => {
  let service: SolicitudProyectoClasificacionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(SolicitudProyectoClasificacionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
