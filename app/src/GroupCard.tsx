import React, { FunctionComponent, useContext, useState } from "react";
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
import RuleForm from "./RuleForm";
import { StoreContext } from "./StoreContext";

interface IProps {
  group: Group;
  focusGroup: Group | null;
  setFocusGroup: (g: Group) => void;
  newGroupRuleName: string | null;
}
const GroupCard: FunctionComponent<IProps> = ({
  group,
  focusGroup,
  setFocusGroup,
  newGroupRuleName
}) => {
  const context = useContext(StoreContext);

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
    setGrp({ ...grp, name: e });
  }

  function handleConfirm() {
    context.groupsAction?.updateGroupName(group.name, grp.name);
  }

  function handleDeleteGroup() {
    context.groupsAction?.removeGroup(grp.name);
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
      context.groupsAction?.updateGroupRule(group, selectedRule.name, editRule);
    }
    setIsOpen(false);
  }

  function handleDeleteGroupRule(curGrp: Group, rule: Rule) {
    context.groupsAction?.removeGroupRule(group, rule.name);
  }

  return (
    <Card
      key={group.name}
      interactive={false}
      elevation={focusGroup === group ? Elevation.FOUR : Elevation.ONE}
      className={`group-card ${focusGroup === group ? "group-card-focus" : ""}`}
      onClick={handleClick}
      style={{ paddingTop: "0px" }}
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
