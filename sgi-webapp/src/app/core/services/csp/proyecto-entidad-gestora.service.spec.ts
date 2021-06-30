import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { ProyectoEntidadGestoraService } from './proyecto-entidad-gestora.service';

describe('ProyectoEntidadGestoraService', () => {
  let service: ProyectoEntidadGestoraService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        BrowserAnimationsModule,
        HttpClientTestingModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(ProyectoEntidadGestoraService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
