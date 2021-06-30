import { TestBed } from '@angular/core/testing';

import { ProyectoSocioEquipoService } from './proyecto-socio-equipo.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';

describe('ProyectoSocioEquipoService', () => {
  let service: ProyectoSocioEquipoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(ProyectoSocioEquipoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
