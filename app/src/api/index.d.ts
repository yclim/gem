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
    ruleId: string,
    label: string,
    paramValues: string[]
}

export interface GroupFiles {
    groupName: string;
    files: File[];
}

export interface File {
    fileName: string;
    fileSize: string;
    mimeType: string;
    rawText: string;
}


declare module RuleService {
    export function getRules(): Promise<RuleDef[]>;
    export function getGroups(): Promise<Group[]>;
    export function addRuleToGroup(gname: string, rname: string, rlabel: string): Promise<string>;
    export function getGroupFiles(gname: string): Promise<GroupFiles>;
    export function getFileTypes(): Promise<string[]>;
    export function getFilesByType(type: string): Promise<File[]>;
}

export default RuleService;