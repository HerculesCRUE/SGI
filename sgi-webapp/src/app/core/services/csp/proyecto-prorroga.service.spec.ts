import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { ProyectoProrrogaService } from './proyecto-prorroga.service';

describe('ProyectoProrrogaService', () => {
  let service: ProyectoProrrogaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(ProyectoProrrogaService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
