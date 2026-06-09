const IST_OFFSET_MIN = 330;

export function startOfIstDay(d: Date = new Date()): Date {
  const utc = d.getTime();
  const ist = new Date(utc + IST_OFFSET_MIN * 60_000);
  return new Date(Date.UTC(ist.getUTCFullYear(), ist.getUTCMonth(), ist.getUTCDate()) - IST_OFFSET_MIN * 60_000);
}
