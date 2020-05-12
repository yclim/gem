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
import {GroupAction, GroupActions, rulenameExist} from "./EditGroups";
import {FileStatAction, FileStatActions} from "./FileStat";
import RuleForm from "./RuleForm";
import groupRuleService from "./api/GroupRuleService";

interface IProps {
  group: Group;
  groupDispatcher: Dispatch<GroupAction>;
  focusGroup: Group | null;
  setFocusGroup: (g: Group) => void;
  newGroupRuleName: string | null;
  groups: Map<string, Group>;
  fileStatDispatcher: Dispatch<FileStatAction>;
  fileStat: number[];
}
const GroupCard: FunctionComponent<IProps> = ({
  group,
  groupDispatcher,
  focusGroup,
  setFocusGroup,
  newGroupRuleName,
  groups,
  fileStatDispatcher,
  fileStat
}) => {
  const [grp, setGrp] = useState(group);
  const [isOpen, setIsOpen] = useState<boolean>(false);
  const [selectedRule, setSelectedRule] = useState<Rule | null>(null);
  // editRule will be pass to GroupForm by reference so we can refer to the existing version
  const [editRule, setEditRule] = useState<Rule | null>(null);

  function renderMenu(r: Rule) {
    return (
      <Menu>
        <MenuItem text="Edit" icon="edit" onClick={() => handleDialogOpen(r)} />
        <MenuItem
          text="Delete"
          icon="trash"
          onClick={() => handleDeleteGroupRule(grp, r)}
        />
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
    GroupActions.updateGroupName(groupDispatcher, group.name, grp.name);
    FileStatActions.getFileStat(fileStatDispatcher);
  }

  function handleDeleteGroup() {
    GroupActions.removeGroup(groupDispatcher, grp.name);
    FileStatActions.getFileStat(fileStatDispatcher);
  }

  function handleDialogOpen(r: Rule) {
    setSelectedRule(r);
    setEditRule(JSON.parse(JSON.stringify(r)));
    setIsOpen(true);
  }

  function handleDialogClose() {
    setIsOpen(false);
  }

  function handleDialogSubmit() {
    if (editRule && selectedRule) {
      GroupActions.updateGroupRule(
        groupDispatcher,
        groups,
        group,
        selectedRule.name,
        editRule
      );
      FileStatActions.getFileStat(fileStatDispatcher);
    }

    setIsOpen(false);
  }

  function handleDeleteGroupRule(curGrp: Group, rule: Rule) {
    GroupActions.removeGroupRule(groupDispatcher, group, rule.name);
    FileStatActions.getFileStat(fileStatDispatcher);
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
          <Tag round={true}>{group.matchedCount}</Tag>
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
        {editRule && focusGroup ? (
          <RuleForm
            rule={editRule}
            setRule={setEditRule}
            groupName={focusGroup.name}
            handleSubmit={handleDialogSubmit}
            isUpdate={true}
          />
        ) : (
          ""
        )}
      </Dialog>
    </Card>
  );
};

export default GroupCard;
