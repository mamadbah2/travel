import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { ApiConfig } from '../../../core/config/api.config';
import { CreateReportRequest, PageResponse, ReportResponse } from '../../../shared/models/api.models';

@Injectable({ providedIn: 'root' })
export class ReportService {
  private readonly http = inject(HttpClient);

  getReports(page = 0, size = 20): Promise<PageResponse<ReportResponse>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return firstValueFrom(
      this.http.get<PageResponse<ReportResponse>>(ApiConfig.reports.list, { params }),
    );
  }

  createReport(request: CreateReportRequest): Promise<ReportResponse> {
    return firstValueFrom(
      this.http.post<ReportResponse>(ApiConfig.reports.create, request),
    );
  }

  updateReportStatus(id: string, status: string): Promise<ReportResponse> {
    return firstValueFrom(
      this.http.put<ReportResponse>(ApiConfig.reports.byId(id), { status }),
    );
  }
}
