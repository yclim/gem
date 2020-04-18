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

export interface File {
  fileName: string;
  size: bigint;
  extension: string;
  directory: string;
  mimeType: string;
  data: any[];
}
