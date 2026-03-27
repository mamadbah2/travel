import { Component, input, output } from '@angular/core';
import { RouterLink } from '@angular/router';
import { UserResponse } from '../../../../shared/models/api.models';
import { StatusBadgeComponent } from '../../../../shared/ui/status-badge/status-badge.component';
import { formatDate } from '../../../../shared/utils/date.utils';

@Component({
  selector: 'app-user-table',
  imports: [RouterLink, StatusBadgeComponent],
  templateUrl: './user-table.component.html',
  styleUrl: './user-table.component.css',
})
export class UserTableComponent {
  readonly users = input.required<UserResponse[]>();
  readonly ban = output<string>();
  readonly unban = output<string>();
  readonly deleteUser = output<string>();

  formatDate = formatDate;
}
