import React, { Dispatch, FunctionComponent, useState } from "react";
import {
  Alignment,
  Button,
  Card,
  Dialog,
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
import RuleForm from "./RuleForm";

interface IProps {
  group: Group;
  groupDispatcher: Dispatch<GroupAction>;
  focusGroup: Group | null;
  setFocusGroup: (g: Group) => void;
  newGroupRuleName: string | null;
}
const GroupCard: FunctionComponent<IProps> = ({
  group,
  groupDispatcher,
  focusGroup,
  setFocusGroup,
  newGroupRuleName
}) => {
  const [grp, setGrp] = useState(group);
  const [isOpen, setIsOpen] = useState<boolean>(false);
  const [selectedRule, setSelectedRule] = useState<Rule | null>(null);

  function renderMenu(r: Rule) {
    return (
      <Menu>
        <MenuItem text="Edit" icon="edit" onClick={() => handleDialogOpen(r)} />
        <MenuItem text="Delete" icon="trash" onClick={() => handleDeleteGroupRule(grp, r)} />
      </Menu>
    );
  }

  function renderGroupRule(r: Rule) {
    return (
      <Popover
        key={`popover-${r.name}`}
        position={Position.RIGHT_TOP}
        content={renderMenu(r)}
      >
        <Button
          alignText={Alignment.LEFT}
          rightIcon="more"
          text={r.name}
          className={`group-card-rule ${
            newGroupRuleName === r.name ? "highlight-effect" : ""
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

  function handleDialogOpen(r: Rule) {
    setSelectedRule(r);
    setIsOpen(true);
  }

  function handleDialogClose() {
    setIsOpen(false);
  }

  function handleDialogSubmit() {
    console.log(selectedRule);
  }

  function handleDeleteGroupRule(group: Group, r: Rule) {
    groupDispatcher(GroupActions.removeGroupRule({groupName: group.name, rule: r}));
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

      <Dialog
        isOpen={isOpen}
        icon="annotation"
        onClose={handleDialogClose}
        title={`${selectedRule ? selectedRule.label : "-"}`}
        transitionDuration={100}
      >
        {selectedRule && focusGroup ? (
          <RuleForm
            rule={selectedRule}
            setRule={setSelectedRule}
            groupName={focusGroup.name}
            handleSubmit={handleDialogSubmit}
          />
        ) : (
          ""
        )}
      </Dialog>
    </Card>
  );
};

export default GroupCard;
