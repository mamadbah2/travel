import { Component, input, output } from '@angular/core';

@Component({
  selector: 'app-cancel-button',
  templateUrl: './cancel-button.component.html',
  styleUrl: './cancel-button.component.css',
})
export class CancelButtonComponent {
  readonly canCancel = input.required<boolean>();
  readonly daysLeft = input(0);
  readonly cancelling = input(false);
  readonly confirmCancel = output<void>();
}
