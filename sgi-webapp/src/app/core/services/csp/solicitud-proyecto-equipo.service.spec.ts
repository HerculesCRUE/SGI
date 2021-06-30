import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { SolicitudProyectoEquipoService } from './solicitud-proyecto-equipo.service';

describe('SolicitudProyectoEquipoService', () => {
  let service: SolicitudProyectoEquipoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(SolicitudProyectoEquipoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
