export interface Metadata {
  name: string;
  generateName?: string;
  labels?: {
    [key: string]: string;
  } | null;
  annotations?: {
    [key: string]: string;
  } | null;
  version?: number | null;
  creationTimestamp?: string | null;
  deletionTimestamp?: string | null;
}

export interface MapGroupSpec {
  displayName: string;
  coordinates: string;
  priority?: number;
}

export interface MapSpec {
  url: string;
  displayName: string;
  logo?: string;
  description?: string;
  priority?: number;
  groupName?: string;
}

export interface Map {
  spec: MapSpec;
  apiVersion: string;
  kind: string;
  metadata: Metadata;
}

export interface MapGroup {
  spec: MapGroupSpec;
  apiVersion: string;
  kind: string;
  metadata: Metadata;
}

export interface MapList {
  page: number;
  size: number;
  total: number;
  items: Array<Map>;
  first: boolean;
  last: boolean;
  hasNext: boolean;
  hasPrevious: boolean;
  totalPages: number;
}

export interface MapGroupList {
  page: number;
  size: number;
  total: number;
  items: Array<MapGroup>;
  first: boolean;
  last: boolean;
  hasNext: boolean;
  hasPrevious: boolean;
  totalPages: number;
}
