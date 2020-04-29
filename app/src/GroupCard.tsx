import React, { Dispatch, FunctionComponent, useState } from "react";
import {
  Alignment,
  Button,
  Card,
  EditableText,
  Elevation,
  Icon,
  Menu,
  MenuItem,
  Popover,
  Position,
  Tag
} from "@blueprintjs/core";
import { Group, Rule } from "./api";
import { GroupAction, GroupActions } from "./EditGroups";

interface IProps {
  group: Group;
  groupDispatcher: Dispatch<GroupAction>;
  focusGroup: Group | null;
  setFocusGroup: (g: Group) => void;
  focusedGroupRuleName: string | null;
}
const GroupCard: FunctionComponent<IProps> = ({
  group,
  groupDispatcher,
  focusGroup,
  setFocusGroup,
  focusedGroupRuleName
}) => {
  const [grp, setGrp] = useState(group);

  function renderMenu() {
    return (
      <Menu>
        <MenuItem text="Edit" icon="edit" />
        <MenuItem text="Delete" icon="trash" />
      </Menu>
    );
  }

  function renderGroupRule(ri: Rule) {
    return (
      <Popover
        key={`popover-${ri.name}`}
        position={Position.RIGHT_TOP}
        content={renderMenu()}
      >
        <Button
          alignText={Alignment.LEFT}
          rightIcon="more"
          text={ri.name}
          className={`group-card-rule ${
            focusedGroupRuleName === ri.name ? "highlight-effect" : ""
          }`}
        />
      </Popover>
    );
  }

  function handleClick() {
    setFocusGroup(group);
  }

  function handleChangeGroupName(e: string) {
    const g = { name: e, rules: grp.rules };
    setGrp(g);
  }

  function handleConfirm() {
    groupDispatcher(
      GroupActions.updateGroupName({
        oldGroupName: group.name,
        newGroupName: grp.name
      })
    );
  }

  function handleDeleteGroup() {
    groupDispatcher(GroupActions.removeGroup(grp.name));
  }

  return (
    <Card
      key={group.name}
      interactive={false}
      elevation={focusGroup === group ? Elevation.FOUR : Elevation.ONE}
      className={`group-card ${focusGroup === group ? "group-card-focus" : ""}`}
      onClick={handleClick}
    >
      <div className="group-card-topbar">
        <Icon icon="cross" onClick={() => handleDeleteGroup()} />
      </div>
      <div className="group-card-header">
        <div className="label">
          <EditableText
            value={grp.name}
            onChange={e => handleChangeGroupName(e)}
            onConfirm={() => handleConfirm()}
          />
        </div>
        <div className="counter">
          <Tag round={true}>123</Tag>
        </div>
      </div>
      <div>
        {group.rules.map(ri => {
          return renderGroupRule(ri);
        })}
      </div>
    </Card>
  );
};

export default GroupCard;
