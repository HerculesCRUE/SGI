import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { ProyectoPaqueteTrabajoService } from './proyecto-paquete-trabajo.service';

describe('ProyectoPaqueteTrabajoService', () => {
  let service: ProyectoPaqueteTrabajoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(ProyectoPaqueteTrabajoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
