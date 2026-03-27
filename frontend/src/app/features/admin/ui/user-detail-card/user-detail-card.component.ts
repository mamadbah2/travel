import { Component, input, output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UserResponse } from '../../../../shared/models/api.models';
import { StatusBadgeComponent } from '../../../../shared/ui/status-badge/status-badge.component';
import { formatDate } from '../../../../shared/utils/date.utils';

@Component({
  selector: 'app-user-detail-card',
  imports: [FormsModule, StatusBadgeComponent],
  templateUrl: './user-detail-card.component.html',
  styleUrl: './user-detail-card.component.css',
})
export class UserDetailCardComponent {
  readonly user = input.required<UserResponse>();
  readonly ban = output<void>();
  readonly unban = output<void>();
  readonly deleteUser = output<void>();
  readonly updateScore = output<number>();

  scoreValue = 0;
  formatDate = formatDate;
}
