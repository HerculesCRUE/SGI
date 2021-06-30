import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { SolicitudProyectoAreaConocimientoService } from './solicitud-proyecto-area-conocimiento.service';

describe('SolicitudProyectoAreaConocimientoService', () => {
  let service: SolicitudProyectoAreaConocimientoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(SolicitudProyectoAreaConocimientoService);
  });


  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
