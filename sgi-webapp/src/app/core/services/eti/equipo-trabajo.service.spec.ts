import { TestBed } from '@angular/core/testing';

import { HttpClientTestingModule } from '@angular/common/http/testing';
import { EquipoTrabajoService } from './equipo-trabajo.service';
import { LoggerTestingModule } from 'ngx-logger/testing';

describe('EquipoTrabajoService', () => {
  let service: EquipoTrabajoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(EquipoTrabajoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
