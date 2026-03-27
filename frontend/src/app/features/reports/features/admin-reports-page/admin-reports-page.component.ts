import { Component, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ReportService } from '../../data-access/report.service';
import { StatusBadgeComponent } from '../../../../shared/ui/status-badge/status-badge.component';
import { PaginationComponent } from '../../../../shared/ui/pagination/pagination.component';
import { ReportResponse } from '../../../../shared/models/api.models';
import { NotificationService } from '../../../../shared/data-access/notification.service';

@Component({
  selector: 'app-admin-reports-page',
  imports: [DatePipe, StatusBadgeComponent, PaginationComponent],
  templateUrl: './admin-reports-page.component.html',
  styleUrl: './admin-reports-page.component.css',
})
export class AdminReportsPageComponent {
  private readonly reportService = inject(ReportService);
  private readonly notify = inject(NotificationService);

  readonly reports = signal<ReportResponse[]>([]);
  readonly loading = signal(true);
  readonly page = signal(0);
  readonly totalPages = signal(0);

  constructor() {
    this.loadPage(0);
  }

  async loadPage(page: number): Promise<void> {
    this.loading.set(true);
    try {
      const res = await this.reportService.getReports(page);
      this.reports.set(res.content);
      this.page.set(res.page);
      this.totalPages.set(res.totalPages);
    } catch {
      this.reports.set([]);
    } finally {
      this.loading.set(false);
    }
  }

  async updateStatus(report: ReportResponse, status: string): Promise<void> {
    try {
      await this.reportService.updateReportStatus(report.id, status);
      this.reports.update(list =>
        list.map(r => r.id === report.id ? { ...r, status: status as any } : r),
      );
      this.notify.showSuccess(`Report marked as ${status.toLowerCase()}`);
    } catch {
      this.notify.showError('Failed to update report');
    }
  }
}
