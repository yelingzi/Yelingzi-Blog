export interface MenuItem {
  name: string
  path: string
  icon: string
  colorClass: string
  children?: MenuItem[]
  external?: boolean
}

export interface Visit {
  success: boolean,
  time: string,
  week: string,
  ip: string,
  location: string,
  browser: string,
  system: string,
  low: string,
  high: string,
  tq: string,
  fl: string,
  fengxiang: string,
  tip: string
}
