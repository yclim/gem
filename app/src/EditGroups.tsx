import React, {
  FunctionComponent,
  useEffect,
  useReducer,
  useState
} from "react";
import RuleList from "./RuleList";
import GroupList from "./GroupList";
import { RouteComponentProps } from "@reach/router";
import { File, Group } from "./api";
import groupRuleService from "./api/GroupRuleService";
import "@blueprintjs/table/lib/css/table.css";
import FileList from "./FileList";
import { AxiosResponse } from "axios";

export interface AddGroupRuleInput {
  groupName: string;
  ruleId: string;
  ruleName: string;
  ruleParams: string[];
}

export interface UpdateGroupNameInput {
  oldGroupName: string;
  newGroupName: string;
}

export interface UpdateRuleInput {
  groupName: string;
  oldRuleName: string;
  newRuleName: string;
  ruleParams: string[];
}

const INIT_GROUPS = "INIT_GROUPS";
const NEW_GROUP = "NEW_GROUP";
const REMOVE_GROUP = "REMOVE_GROUP";
const ADD_GROUP_RULE = "ADD_GROUP_RULE";
const UPDATE_GROUP_NAME = "UPDATE_GROUP_NAME";
const UPDATE_RULE = "UPDATE_RULE";

export type GroupAction =
  | { type: typeof INIT_GROUPS; groups: Group[] }
  | { type: typeof NEW_GROUP }
  | { type: typeof REMOVE_GROUP; groupName: string }
  | { type: typeof ADD_GROUP_RULE; addGroupRuleInput: AddGroupRuleInput }
  | {
      type: typeof UPDATE_GROUP_NAME;
      updateGroupNameInput: UpdateGroupNameInput;
    }
  | { type: typeof UPDATE_RULE; updateRuleInput: UpdateRuleInput };

export abstract class GroupActions {
  static initGroup(groups: Group[]): GroupAction {
    return { type: INIT_GROUPS, groups };
  }
  static newGroupAction(): GroupAction {
    return { type: NEW_GROUP };
  }
  static removeGroup(groupName: string): GroupAction {
    // TODO implement UI and backend api to use this
    return { type: REMOVE_GROUP, groupName };
  }
  static addGroupRule(addGroupRuleInput: AddGroupRuleInput): GroupAction {
    return { type: ADD_GROUP_RULE, addGroupRuleInput };
  }
  static updateGroupName(
    updateGroupNameInput: UpdateGroupNameInput
  ): GroupAction {
    // TODO need a backend api to update group name
    return { type: UPDATE_GROUP_NAME, updateGroupNameInput };
  }
  static updateRule(updateRuleInput: UpdateRuleInput): GroupAction {
    return { type: UPDATE_RULE, updateRuleInput };
  }
}

export function groupsReducer(state: Map<string, Group>, action: GroupAction) {
  switch (action.type) {
    case INIT_GROUPS:
      return new Map(action.groups.map(g => [g.name, g]));
    case NEW_GROUP:
      return handleNewGroup(state);
    case REMOVE_GROUP:
      return handleRemoveGroup(state, action.groupName);
    case ADD_GROUP_RULE:
      return handleAddGroupRule(state, action.addGroupRuleInput);
    case UPDATE_GROUP_NAME:
      return handleUpdateGroupName(state, action.updateGroupNameInput);
    case UPDATE_RULE:
      return handleUpdateRule(state, action.updateRuleInput);
    default:
      throw new Error();
  }
}

function handleNewGroup(groups: Map<string, Group>): Map<string, Group> {
  const name = "untitled";
  let counter = 1;
  while (true) {
    const modName = name + "-" + counter;
    if (!groups.has(modName)) {
      return new Map(groups.set(modName, { name: modName, rules: [] }));
    } else {
      counter++;
    }
  }
}

function handleRemoveGroup(
  groups: Map<string, Group>,
  groupName: string
): Map<string, Group> {
  groups.delete(groupName);
  return groups;
}

function handleAddGroupRule(
  groups: Map<string, Group>,
  input: AddGroupRuleInput
): Map<string, Group> {
  const group = groups.get(input.groupName);
  if (group) {
    group.rules = [
      ...group.rules,
      {
        ruleId: input.ruleId,
        name: input.ruleName,
        params: input.ruleParams.map(p => {
          return { value: p };
        })
      }
    ];

    groupRuleService.saveGroup(group).then(response => {
      if (response.status !== 200) {
        alert("saveGroup fail with status: " + response.status);
      }
    });
    groups.set(input.groupName, group);
  }
  return new Map(groups);
}

function handleUpdateGroupName(
  groups: Map<string, Group>,
  input: UpdateGroupNameInput
): Map<string, Group> {
  const group = groups.get(input.oldGroupName);
  if (group) {
    groups.delete(input.oldGroupName);
    return new Map([...groups.set(input.newGroupName, group).entries()].sort());
  } else {
    return groups;
  }
}

function handleUpdateRule(
  groups: Map<string, Group>,
  input: UpdateRuleInput
): Map<string, Group> {
  if (rulenameExist(groups, input.newRuleName)) {
    alert("rulename already exist!");
    return groups;
  } else {
    const group = groups.get(input.groupName);
    if (group) {
      const rule = group.rules.find(r => r.name === input.oldRuleName);
      if (rule) {
        rule.name = input.newRuleName;
        rule.params = input.ruleParams.map(p => {
          return { value: p };
        });
      }
    }
  }

  return groups;
}

export function rulenameExist(
  groups: Map<string, Group>,
  name: string
): boolean {
  return (
    Array.from(groups).findIndex(
      e => e[1].rules.findIndex(r => r.name === name) > -1
    ) > -1
  );
}

const EditGroups: FunctionComponent<RouteComponentProps> = () => {
  const [groups, dispatcher] = useReducer(
    groupsReducer,
    new Map<string, Group>()
  );
  const [focusedGroupRuleName, setFocusedGroupRuleName] = useState<
    string | null
  >(null);
  const [currentGroup, setCurrentGroup] = useState<Group | null>(null);
  const [files, setFiles] = useState<File[]>([]);

  useEffect(() => {
    if (groups.size === 0) {
      groupRuleService.getGroups().then((resp: AxiosResponse<Group[]>) => {
        dispatcher(GroupActions.initGroup(resp.data));
      });
    }
  }, []);

  useEffect(() => {
    // TODO: call api to get all files that matched the group
  }, [currentGroup]);

  return (
    <div className="grid3">
      <RuleList
        groups={groups}
        groupDispatcher={dispatcher}
        setFocusedGroupRuleName={setFocusedGroupRuleName}
      />
      <GroupList
        groups={groups}
        groupDispatcher={dispatcher}
        currentGroup={currentGroup}
        setCurrentGroup={setCurrentGroup}
        focusedGroupRuleName={focusedGroupRuleName}
      />
      <FileList files={files} setFiles={setFiles} />
    </div>
  );
};

export default EditGroups;
