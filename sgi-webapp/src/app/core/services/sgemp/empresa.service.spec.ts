import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { EmpresaService } from './empresa.service';

describe('EmpresaService', () => {
  let service: EmpresaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(EmpresaService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
