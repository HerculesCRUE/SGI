import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { ProyectoAreaConocimientoService } from './proyecto-area-conocimiento.service';

describe('ProyectoAreaConocimientoService', () => {
  let service: ProyectoAreaConocimientoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(ProyectoAreaConocimientoService);
  });


  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
