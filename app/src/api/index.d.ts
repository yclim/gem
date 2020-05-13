export interface Group {
  groupId: number;
  name: string;
  rules: Rule[];
  matchedCount?: number;
  matchedFile?: File[];
}

export interface Rule {
  ruleId: string;
  label?: string;
  ruleType?: string;
  name: string;
  params: Parameter[];
}

export interface Parameter {
  label?: string;
  placeholder?: string;
  value: string;
  type?: string;
}

export interface ExcelFeature {
  metadata: Map<string, string>;
  sheetTableData: Map<string, string[][]>;
}

export interface CsvFeature {
  metadata: Map<string, string>;
  tableData: string[][];
  headers: string[];
}

export interface TikaFeature {
  metadata: Map<string, string>;
  content: string;
}

export interface File {
  fileName: string;
  size: number;
  extension: string;
  directory: string;
  mimeType: string;
  data: (ExcelFeature | CsvFeature | TikaFeature)[] | null;
}

export interface ExtractConfig {
  tableName: string;
  columnNames: string[];
  timestampColumns: TimestampColumn[];
  groupId: number;
}

export interface TimestampColumn {
  name: string;
  fromColumn: string;
  format: string;
  timezone?: string;
}
