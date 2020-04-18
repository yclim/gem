export interface RuleDef {
  ruleId: string;
  alias: string;
  paramDefs: ParamDef[];
  target: string;
}

export interface ParamDef {
  label: string;
  type: string;
}

export interface Group {
  groupName: string;
  rules: Rule[];
}

export interface Rule {
  ruleId: string;
  label: string;
  paramValues: string[];
}

export interface GroupFiles {
  groupName: string;
  files: File[];
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
