import { Group, Rule } from "./api";
import groupRuleService from "./api/GroupRuleService";
import { AxiosResponse } from "axios";
import { UpdateGroupNameInput } from "./EditGroups";

const INIT_GROUPS = "INIT_GROUPS";
const NEW_GROUP = "NEW_GROUP";
const REMOVE_GROUP = "REMOVE_GROUP";
const UPDATE_GROUP_RULE = "UPDATE_GROUP_RULE";
const UPDATE_GROUP_NAME = "UPDATE_GROUP_NAME";

export type GroupAction =
  | { type: typeof INIT_GROUPS; payload: { groups: Group[] } }
  | { type: typeof NEW_GROUP; payload: { group: Group } }
  | { type: typeof REMOVE_GROUP; payload: { groupName: string } }
  | { type: typeof UPDATE_GROUP_RULE; payload: { group: Group } }
  | {
      type: typeof UPDATE_GROUP_NAME;
      updateGroupNameInput: UpdateGroupNameInput;
    };

export abstract class GroupActions {
  static initGroup(dispatcher: React.Dispatch<GroupAction>) {
    groupRuleService.getGroups().then((resp: AxiosResponse<Group[]>) => {
      dispatcher({ type: INIT_GROUPS, payload: { groups: resp.data } });
    });
  }
  static newGroup(
    dispatcher: React.Dispatch<GroupAction>,
    groups: Map<string, Group>
  ) {
    const name = "untitled";
    let counter = 1;
    const maxTries = 100;
    while (counter < maxTries) {
      const modName = name + "-" + counter;
      if (!groups.has(modName)) {
        const newGroup: Group = {
          groupId: 0,
          name: modName,
          rules: []
        };
        groupRuleService.saveGroup(newGroup).then(response => {
          dispatcher({ type: NEW_GROUP, payload: { group: response.data } });
        });
        break;
      } else {
        counter++;
      }
    }
    if (counter === maxTries) {
      throw Error("(newGroup) too many tries");
    }
  }

  static removeGroup(
    dispatcher: React.Dispatch<GroupAction>,
    groupName: string
  ) {
    groupRuleService.deleteGroup(groupName).then(response => {
      dispatcher({ type: REMOVE_GROUP, payload: { groupName } });
    });
  }

  static addGroupRule(
    dispatcher: React.Dispatch<GroupAction>,
    groups: Map<string, Group>,
    groupName: string,
    rule: Rule
  ) {
    const group = groups.get(groupName);
    if (group) {
      group.rules = [...group.rules, rule];
      groupRuleService.saveGroup(group).then(response => {
        dispatcher({
          type: UPDATE_GROUP_RULE,
          payload: { group: response.data }
        });
      });
    }
  }

  static updateGroupRule(
    dispatcher: React.Dispatch<GroupAction>,
    groups: Map<string, Group>,
    group: Group,
    oldRulename: string,
    rule: Rule
  ) {
    if (rule.name !== oldRulename && rulenameExist(groups, rule.name)) {
      alert("rulename already exist!");
    }
    const oldRule = group.rules.find(r => r.name === oldRulename);
    if (oldRule) {
      oldRule.name = rule.name;
      oldRule.params = rule.params;
      groupRuleService.saveGroup(group).then(response => {
        dispatcher({
          type: UPDATE_GROUP_RULE,
          payload: { group: response.data }
        });
      });
    }
  }

  static removeGroupRule(
    dispatcher: React.Dispatch<GroupAction>,
    group: Group,
    ruleName: string
  ) {
    group.rules = group.rules.filter(r => {
      return r.name !== ruleName;
    });
    groupRuleService.saveGroup(group).then(response => {
      dispatcher({ type: UPDATE_GROUP_RULE, payload: { group } });
    });
  }

  static updateGroupName(
    dispatcher: React.Dispatch<GroupAction>,
    oldGroupName: string,
    newGroupName: string
  ) {
    groupRuleService
      .updateGroupName(oldGroupName, newGroupName)
      .then(response => {
        dispatcher({
          type: UPDATE_GROUP_NAME,
          updateGroupNameInput: { oldGroupName, newGroupName }
        });
      });
  }
}

export const initialState: Map<string, Group> = new Map();

export default function groupsReducer(
  state: Map<string, Group>,
  action: GroupAction
) {
  switch (action.type) {
    case INIT_GROUPS:
      return new Map(action.payload.groups.map(g => [g.name, g]));
    case NEW_GROUP:
      return handleNewGroup(state, action.payload.group);
    case REMOVE_GROUP:
      return handleRemoveGroup(state, action.payload.groupName);
    case UPDATE_GROUP_RULE:
      return handleUpdateGroupRule(state, action.payload.group);
    case UPDATE_GROUP_NAME:
      return handleUpdateGroupName(state, action.updateGroupNameInput);
    default:
      throw new Error();
  }
}

function handleNewGroup(
  groups: Map<string, Group>,
  group: Group
): Map<string, Group> {
  groups.set(group.name, group);
  return new Map(groups);
}

function handleRemoveGroup(
  groups: Map<string, Group>,
  groupName: string
): Map<string, Group> {
  groups.delete(groupName);
  return new Map(groups);
}

function handleUpdateGroupRule(
  groups: Map<string, Group>,
  group: Group
): Map<string, Group> {
  groups.set(group.name, group);
  return new Map(groups);
}

function handleUpdateGroupName(
  groups: Map<string, Group>,
  input: UpdateGroupNameInput
): Map<string, Group> {
  const group = groups.get(input.oldGroupName);
  if (group) {
    groups.delete(input.oldGroupName);
    group.name = input.newGroupName;
    return new Map([...groups.set(input.newGroupName, group).entries()].sort());
  } else {
    return groups;
  }
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
