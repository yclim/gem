export interface Group {
  name: string;
  rules: Rule[];
  matchedCount?: bigint;
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
  size: bigint;
  extension: string;
  directory: string;
  mimeType: string;
  data: (ExcelFeature | CsvFeature | TikaFeature)[] | null;
}
