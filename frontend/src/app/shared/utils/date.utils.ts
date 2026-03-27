export interface CancellationInfo {
  allowed: boolean;
  daysLeft: number;
  deadline: Date;
}

export function canCancelSubscription(travelStartDate: string): CancellationInfo {
  const start = new Date(travelStartDate);
  const deadline = new Date(start);
  deadline.setDate(deadline.getDate() - 3);

  const now = new Date();
  const diffMs = deadline.getTime() - now.getTime();
  const daysLeft = Math.ceil(diffMs / (1000 * 60 * 60 * 24));

  return {
    allowed: now < deadline,
    daysLeft: Math.max(0, daysLeft),
    deadline,
  };
}

export function formatDate(dateStr: string): string {
  return new Date(dateStr).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
  });
}

export function formatDateRange(start: string, end: string): string {
  return `${formatDate(start)} — ${formatDate(end)}`;
}
