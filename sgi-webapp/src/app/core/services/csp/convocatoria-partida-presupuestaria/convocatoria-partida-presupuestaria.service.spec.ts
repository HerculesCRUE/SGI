import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { ConvocatoriaPartidaPresupuestariaService } from './convocatoria-partida-presupuestaria.service';

describe('ConvocatoriaPartidaPresupuestariaService', () => {
  let service: ConvocatoriaPartidaPresupuestariaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(ConvocatoriaPartidaPresupuestariaService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
