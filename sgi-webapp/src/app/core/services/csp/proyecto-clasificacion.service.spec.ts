import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { ProyectoClasificacionService } from './proyecto-clasificacion.service';

describe('ProyectoClasificacionService', () => {
  let service: ProyectoClasificacionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(ProyectoClasificacionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
