import React, {
  FunctionComponent,
  useEffect,
  useReducer,
  useState
} from "react";
import RuleList from "./RuleList";
import GroupList from "./GroupList";
import { RouteComponentProps } from "@reach/router";
import { File, Group, Rule } from "./api";
import groupRuleService from "./api/GroupRuleService";
import "@blueprintjs/table/lib/css/table.css";
import FileList from "./FileList";
import { AxiosResponse } from "axios";

export interface UpdateGroupNameInput {
  oldGroupName: string;
  newGroupName: string;
}

const INIT_GROUPS = "INIT_GROUPS";
const NEW_GROUP = "NEW_GROUP";
const REMOVE_GROUP = "REMOVE_GROUP";
const UPDATE_GROUP_RULE = "UPDATE_GROUP_RULE";
const UPDATE_GROUP_NAME = "UPDATE_GROUP_NAME";
const GET_FILE_STAT = "GET_FILE_STAT";

export type CounterAction =
  { type: typeof GET_FILE_STAT; fileStat: number[]};

export abstract class CounterActions {
  static getFileStat(dispatcher: React.Dispatch<CounterAction>) {
    groupRuleService.getFileStat().then((resp: AxiosResponse<number[]>) => {
      dispatcher({type: GET_FILE_STAT, fileStat: resp.data});
    });
  }
}
export type GroupAction =
  | { type: typeof INIT_GROUPS; groups: Group[] }
  | { type: typeof NEW_GROUP; group: Group }
  | { type: typeof REMOVE_GROUP; groupName: string }
  | { type: typeof UPDATE_GROUP_RULE; group: Group }
  | {
      type: typeof UPDATE_GROUP_NAME;
      updateGroupNameInput: UpdateGroupNameInput;
    };

export abstract class GroupActions {
  static initGroup(dispatcher: React.Dispatch<GroupAction>) {
    groupRuleService.getGroups().then((resp: AxiosResponse<Group[]>) => {
      dispatcher({ type: INIT_GROUPS, groups: resp.data });
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
          name: modName,
          rules: []
        };
        groupRuleService.saveGroup(newGroup).then(response => {
          dispatcher({ type: NEW_GROUP, group: response.data });
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
      dispatcher({ type: REMOVE_GROUP, groupName });
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
        dispatcher({ type: UPDATE_GROUP_RULE, group: response.data  });
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
        dispatcher({ type: UPDATE_GROUP_RULE, group: response.data });
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
      dispatcher({ type: UPDATE_GROUP_RULE, group });
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

export function counterReducer(state: number[], action: CounterAction) {
  switch (action.type) {
    case GET_FILE_STAT: {
      return action.fileStat
    }
    default:
      throw new Error();
  }
}

export function groupsReducer(state: Map<string, Group>, action: GroupAction) {
  switch (action.type) {
    case INIT_GROUPS:
      return new Map(action.groups.map(g => [g.name, g]));
    case NEW_GROUP:
      return handleNewGroup(state, action.group);
    case REMOVE_GROUP:
      return handleRemoveGroup(state, action.groupName);
    case UPDATE_GROUP_RULE:
      return handleUpdateGroupRule(state, action.group);
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

const EditGroups: FunctionComponent<RouteComponentProps> = () => {
  const [groups, dispatcher] = useReducer(
    groupsReducer,
    new Map<string, Group>()
  );

  const [fileStat, countDispatcher] = useReducer(
      counterReducer,
      []
  );

  const [newGroupRuleName, setNewGroupRuleName] = useState<string | null>(null);
  const [currentGroup, setCurrentGroup] = useState<Group | null>(null);
  const [files, setFiles] = useState<File[]>([]);

  useEffect(() => {
    GroupActions.initGroup(dispatcher);
    CounterActions.getFileStat(countDispatcher)
  }, []);

  useEffect(() => {
    if(currentGroup != null){
        groupRuleService
          .getGroup(currentGroup.name)
          .then(response => {
                // Check for undefined to takecare of deleted group
                if (typeof response.data.matchedFile !== 'undefined') {
                    setFiles(response.data.matchedFile);
                } else {
                    setFiles([]);
                }
          });
    } else {
        setFiles([]);
    }
  }, [currentGroup, groups]);

  return (
    <div className="grid3">
      <RuleList
        groups={groups}
        groupDispatcher={dispatcher}
        setNewGroupRuleName={setNewGroupRuleName}
      />
      <GroupList
        groups={groups}
        groupDispatcher={dispatcher}
        currentGroup={currentGroup}
        setCurrentGroup={setCurrentGroup}
        newGroupRuleName={newGroupRuleName}
        countDispatcher={countDispatcher}
        fileStat={fileStat}
      />
      <FileList files={files} setFiles={setFiles} />
    </div>
  );
};

export default EditGroups;
