import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { ProyectoEquipoService } from './proyecto-equipo.service';

describe('ProyectoEquipoService', () => {
  let service: ProyectoEquipoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(ProyectoEquipoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
