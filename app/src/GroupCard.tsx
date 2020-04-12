import React, { FunctionComponent, useState } from "react";
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

interface IProps {
  group: Group;
  updateGroups: (oldname: string, g: Group) => void;
  focusGrp: Group | null;
  setFocusGrp: (g: Group) => void;
}
const GroupCard: FunctionComponent<IProps> = ({
  group,
  updateGroups,
  focusGrp,
  setFocusGrp
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
        key={`popover-${ri.label}`}
        position={Position.RIGHT_TOP}
        content={renderMenu()}
      >
        <Button
          key={`button-${ri.label}`}
          alignText={Alignment.LEFT}
          rightIcon="more"
          text={ri.label}
          className="rule"
        ></Button>
      </Popover>
    );
  }

  function handleClick() {
    setFocusGrp(group);
  }

  function handleChangeGroupName(e: string) {
    let g = { groupName: e, rules: grp.rules };
    setGrp(g);
  }

  function handleConfirm() {
    updateGroups(group.groupName, grp);
  }

  return (
    <Card
      key={group.groupName}
      interactive={false}
      elevation={focusGrp === group ? Elevation.FOUR : Elevation.ONE}
      className={`card group-card ${
        focusGrp === group ? "group-card-focus" : ""
      }`}
      onClick={handleClick}
    >
      <div className="rule-topbar">
        {" "}
        <Icon icon="cross" />{" "}
      </div>
      <div className="rule-header">
        <div className="label">
          <EditableText
            value={grp.groupName}
            onChange={e => handleChangeGroupName(e)}
            onConfirm={() => handleConfirm()}
          />
        </div>
        <div className="counter">
          <Tag round={true}>123</Tag>
        </div>
      </div>
      <div className="flex-wrap">
        {group.rules.map(ri => {
          return renderGroupRule(ri);
        })}
      </div>
    </Card>
  );
};

export default GroupCard;
