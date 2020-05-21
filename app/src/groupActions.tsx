import { Group, Rule } from "./api";
import { Dispatch } from "react";
import groupRuleService from "./api/GroupRuleService";
import { AxiosResponse } from "axios";
import {
  GroupDispatchType,
  INIT_GROUPS,
  NEW_GROUP,
  REMOVE_GROUP,
  UPDATE_GROUP_NAME,
  UPDATE_GROUP_RULE
} from "./groupReducer";

export interface GroupAction {
  initGroup: () => void;
  newGroup: () => void;
  removeGroup: (groupName: string) => void;
  addGroupRule: (groupName: string, rule: Rule) => void;
  updateGroupName: (oldGroupName: string, newGroupName: string) => void;
  updateGroupRule: (group: Group, oldRuleName: string, rule: Rule) => void;
  removeGroupRule: (group: Group, ruleName: string) => void;
  isRuleNameUsed: (ruleName: string) => boolean;
}

export function useGroupActions(
  state: Map<string, Group>,
  dispatch: Dispatch<GroupDispatchType>
): GroupAction {
  return {
    initGroup: () => {
      groupRuleService.getGroups().then((resp: AxiosResponse<Group[]>) => {
        dispatch({ type: INIT_GROUPS, payload: { groups: resp.data } });
      });
    },
    newGroup: () => {
      const name = "untitled";
      let counter = 1;
      const maxTries = 100;
      while (counter < maxTries) {
        const modName = name + "-" + counter;
        if (!state.has(modName)) {
          const newGroup: Group = {
            groupId: 0,
            name: modName,
            rules: []
          };
          groupRuleService.saveGroup(newGroup).then(response => {
            dispatch({ type: NEW_GROUP, payload: { group: response.data } });
          });
          break;
        } else {
          counter++;
        }
      }
      if (counter === maxTries) {
        throw Error("(newGroup) too many tries");
      }
    },
    removeGroup: groupName => {
      groupRuleService.deleteGroup(groupName).then(response => {
        dispatch({ type: REMOVE_GROUP, payload: { groupName } });
      });
    },

    addGroupRule: (groupName, rule) => {
      const group = state.get(groupName);
      if (group) {
        group.rules = [...group.rules, rule];
        groupRuleService.saveGroup(group).then(response => {
          dispatch({
            type: UPDATE_GROUP_RULE,
            payload: { group: response.data }
          });
        });
      }
    },
    updateGroupName: (oldGroupName, newGroupName) => {
      groupRuleService
        .updateGroupName(oldGroupName, newGroupName)
        .then(response => {
          dispatch({
            type: UPDATE_GROUP_NAME,
            updateGroupNameInput: { oldGroupName, newGroupName }
          });
        });
    },
    updateGroupRule: (group: Group, oldRuleName, rule) => {
      if (
        rule.name !== oldRuleName &&
        Array.from(state).findIndex(
          // mapEntry[0] give key, [1] gives value
          mapEntry => mapEntry[1].rules.findIndex(r => r.name === name) > -1
        ) > -1
      ) {
        alert("rulename already exist!");
      }

      const oldRule = group.rules.find(r => r.name === oldRuleName);
      if (oldRule) {
        oldRule.name = rule.name;
        oldRule.params = rule.params;
        groupRuleService.saveGroup(group).then(response => {
          dispatch({
            type: UPDATE_GROUP_RULE,
            payload: { group: response.data }
          });
        });
      }
    },
    removeGroupRule: (group, ruleName) => {
      group.rules = group.rules.filter(r => {
        return r.name !== ruleName;
      });
      groupRuleService.saveGroup(group).then(response => {
        dispatch({
          type: UPDATE_GROUP_RULE,
          payload: { group: response.data }
        });
      });
    },

    isRuleNameUsed: ruleName => {
      return (
        Array.from(state).findIndex(
          // mapEntry[0] give key, [1] gives value
          mapEntry => mapEntry[1].rules.findIndex(r => r.name === ruleName) > -1
        ) > -1
      );
    }
  };
}
