import { Group } from "./api";
import { UpdateGroupNameInput } from "./EditGroups";

export const INIT_GROUPS = "INIT_GROUPS";
export const NEW_GROUP = "NEW_GROUP";
export const REMOVE_GROUP = "REMOVE_GROUP";
export const UPDATE_GROUP_RULE = "UPDATE_GROUP_RULE";
export const UPDATE_GROUP_NAME = "UPDATE_GROUP_NAME";

export type GroupDispatchType =
  | { type: typeof INIT_GROUPS; payload: { groups: Group[] } }
  | { type: typeof NEW_GROUP; payload: { group: Group } }
  | { type: typeof REMOVE_GROUP; payload: { groupName: string } }
  | { type: typeof UPDATE_GROUP_RULE; payload: { group: Group } }
  | {
      type: typeof UPDATE_GROUP_NAME;
      updateGroupNameInput: UpdateGroupNameInput;
    };

export const initialState: Map<string, Group> = new Map();

export default function groupsReducer(
  state: Map<string, Group>,
  action: GroupDispatchType
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
