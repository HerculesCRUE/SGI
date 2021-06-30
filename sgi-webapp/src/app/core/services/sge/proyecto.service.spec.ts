import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { ProyectoSgeService } from './proyecto-sge.service';

describe('ProyectoService', () => {
  let service: ProyectoSgeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(ProyectoSgeService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
