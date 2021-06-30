import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { ProyectoSocioService } from './proyecto-socio.service';

describe('ProyectoSocioService', () => {
  let service: ProyectoSocioService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(ProyectoSocioService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
